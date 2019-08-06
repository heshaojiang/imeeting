package com.grgbanking.grgacd.rpc.Service;

import com.google.gson.JsonObject;
import com.grgbanking.grgacd.rpc.exception.OpenViduException;
import com.grgbanking.grgacd.rpc.handler.RpcConnection;
import org.apache.commons.lang.StringUtils;
import org.kurento.jsonrpc.Session;
import org.kurento.jsonrpc.Transaction;
import org.kurento.jsonrpc.message.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author jinwen
 * @date 2019-05-17
 */
@Service
public class RpcNotificationService {

	private static final Logger log = LoggerFactory.getLogger(RpcNotificationService.class);

	// 保存 rpcSessionId 与 RpcConnection的关系
	private ConcurrentMap<String, RpcConnection> rpcConnections = new ConcurrentHashMap<>();
	// 保存 clientId 与  rpcSessionId 的关系
	private ConcurrentMap<String,String> clientMap = new ConcurrentHashMap<>();

	public RpcConnection newRpcConnection(Transaction t, String clientId, String clientType) {
		//先判断原有队列中是否已经存在相同clientId的连接。有则清空其标记
		if (StringUtils.isNotEmpty(clientId)) {
			for (RpcConnection rpcConnection:rpcConnections.values()) {
				if (clientId.equals(rpcConnection.getClientId())){
					log.info("Has same clientId:{} in rpcConnection:{}",clientId,rpcConnection.getRpcSessionId());
					rpcConnection.setClientId(null);
				}
			}
		}

		String sessionId = t.getSession().getSessionId();
		RpcConnection connection = new RpcConnection(t.getSession(),clientId,clientType);
		RpcConnection oldConnection = rpcConnections.putIfAbsent(sessionId, connection);
		if (oldConnection != null) {
			log.warn("Concurrent initialization of rpcSession #{}", sessionId);
			connection = oldConnection;
		}
		return connection;
	}
	public RpcConnection addTransaction(Transaction t, Request<JsonObject> request) {
		String sessionId = t.getSession().getSessionId();
		RpcConnection connection = rpcConnections.get(sessionId);
		if (connection != null)
			connection.addTransaction(request.getId(), t);
		return connection;
	}
	public void addClientId(String clientId,String rpcSessionId) {
		log.info("addClientId client:{},rpcSessionId:{}",clientId,rpcSessionId);
		clientMap.put(clientId,rpcSessionId);
	}

//	public String getClientId(){
//		Iterator<String> iterator = clientMap.keySet().iterator();
//		while (iterator.hasNext()){
//			return iterator.next();
//		}
//		return null;
//	}

	public boolean checkClientIdOnline(String clientId) {
		String rpcSessionId = getRpcSessionId(clientId);
		boolean bOnline = !StringUtils.isEmpty(rpcSessionId);
		log.info("getCheckClientIdOnline client:{},bOnline:{}",clientId,bOnline);
		return bOnline;
	}
	public String getRpcSessionId(String clientId) {
		String rpcSessionId = null;
		if (StringUtils.isNotEmpty(clientId))
			rpcSessionId = clientMap.get(clientId);
		log.info("getRpcSessionId client:{},rpcSessionId:{}",clientId,rpcSessionId);
		return rpcSessionId;
	}
	public void removeClientId(String clientId) {
		log.info("removeClientId client:{}",clientId);
		if (StringUtils.isNotEmpty(clientId)) {
			clientMap.remove(clientId);
		}
	}
	public void sendResponse(String sessionId, Integer transactionId, Object result) {
		Transaction t = getAndRemoveTransaction(sessionId, transactionId);
		if (t == null) {
			log.error("No transaction {} found for paticipant with private id {}, unable to send result {}",
					transactionId, sessionId, result);
			return;
		}
		try {
			t.sendResponse(result);
		} catch (Exception e) {
			log.error("Exception responding to participant ({})", sessionId, e);
		}
	}

	public void sendErrorResponse(String sessionId, Integer transactionId, Object data,
			OpenViduException error) {
		Transaction t = getAndRemoveTransaction(sessionId, transactionId);
		if (t == null) {
			log.error("No transaction {} found for paticipant with private id {}, unable to send result {}",
					transactionId, sessionId, data);
			return;
		}
		try {

			String dataVal = data != null ? data.toString() : null;
			t.sendError(error.getCodeValue(), error.getMessage(), dataVal);
		} catch (Exception e) {
			log.error("Exception sending error response to user ({})", transactionId, e);
		}
	}
	public void sendNotificationToClient(final String clientId, final String method, final Object params) {
		String rpcSessionId = getRpcSessionId(clientId);
		if (StringUtils.isEmpty(rpcSessionId)){
			log.error("No rpcSessionId found for clientId {}, unable to send notification {}: {}",
					clientId, method, params);
			return;
		}
		RpcConnection rpcSession = rpcConnections.get(rpcSessionId);
		if (rpcSession == null || rpcSession.getSession() == null) {
			log.error("No rpc session found for rpcSessionId {}, unable to send notification {}: {}",
					rpcSessionId, method, params);
			return;
		}
		Session s = rpcSession.getSession();

		try {
			log.info("sendNotification clientId:{} method:{} params:{}",clientId,method,params);
			s.sendNotification(method, params);
			log.info("sendNotification end");
		} catch (Exception e) {
			log.error("Exception sending notification '{}': {} to participant with rpcSessionId {}", method, params,
					rpcSessionId, e);
		}
	}
	public void sendNotification(final String sessionId, final String method, final Object params) {
		RpcConnection rpcSession = rpcConnections.get(sessionId);
		if (rpcSession == null || rpcSession.getSession() == null) {
			log.error("No rpc session found for private id {}, unable to send notification {}: {}",
					sessionId, method, params);
			return;
		}
		Session s = rpcSession.getSession();

		try {
			log.info("success connection for client");
			s.sendNotification(method, params);
			log.info("发送消息给客户端");
		} catch (Exception e) {
			log.error("Exception sending notification '{}': {} to participant with private id {}", method, params,
					sessionId, e);
		}
	}

	public RpcConnection closeRpcSession(String sessionId) {
		RpcConnection rpcConnection = rpcConnections.remove(sessionId);
		if (rpcConnection == null || rpcConnection.getSession() == null) {
			log.error("No session found for id {}, unable to cleanup", sessionId);
			return null;
		}

		// 删除 clientId
		removeClientId(rpcConnection.getClientId());

		Session s = rpcConnection.getSession();
		try {
			s.close();
			log.info("Closed session for rtpSessionId {}", sessionId);
			this.showRpcConnections();
			return rpcConnection;
		} catch (IOException e) {
			log.error("Error closing session for rtpSessionId {}", sessionId, e);
		}
		return null;
	}

	private Transaction getAndRemoveTransaction(String sessionId, Integer transactionId) {
		RpcConnection rpcSession = rpcConnections.get(sessionId);
		if (rpcSession == null) {
			log.warn("Invalid WebSocket session id {}", sessionId);
			return null;
		}
		log.trace("#{} - {} transactions", sessionId, rpcSession.getTransactions().size());
		Transaction t = rpcSession.getTransaction(transactionId);
		rpcSession.removeTransaction(transactionId);
		return t;
	}

	public void showRpcConnections() {
		log.info("<PRIVATE_ID, RPC_CONNECTION>: {}", this.rpcConnections.toString());
	}

	public RpcConnection getRpcConnection(String sessionId) {
		return this.rpcConnections.get(sessionId);
	}

}
