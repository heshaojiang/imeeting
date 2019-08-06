package com.grgbanking.grgacd.rpc.handler;

import com.github.pig.common.security.util.TokenUtil;
import com.github.pig.common.util.R;
import com.github.pig.common.util.exception.GrgException;
import com.google.gson.JsonObject;
import com.grgbanking.grgacd.rpc.Service.EventService;
import com.grgbanking.grgacd.rpc.Service.RpcNotificationService;
import com.grgbanking.grgacd.rpc.common.AcdMsgType;
import org.apache.commons.lang.StringUtils;
import org.kurento.jsonrpc.DefaultJsonRpcHandler;
import org.kurento.jsonrpc.Session;
import org.kurento.jsonrpc.Transaction;
import org.kurento.jsonrpc.internal.ws.WebSocketServerSession;
import org.kurento.jsonrpc.message.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yjw
 * @date 2019-05-10
 */
@Service
public class JsonRpcServerHandler extends DefaultJsonRpcHandler<JsonObject> {

  private static Logger log = LoggerFactory.getLogger(JsonRpcServerHandler.class);
  public  static Map<String,String> sessionMap = new HashMap<>();
//  public  static String sessionId = null,clientId = null;

  public static final String CLOSE_TYPE_Logout = "closeTypeLogout";
  public static final String CLOSE_TYPE_ConnClose = "closeTypeConnClose";
  public static final String CLOSE_TYPE_TranErr = "closeTypeTranErr";

  @Autowired
  private CacheManager cacheManager ;

  @Autowired
  public  RpcNotificationService notificationService;

  @Autowired
  public EventService eventService;

  @Autowired
  private TokenUtil tokenUtil;

  @Override
  public void handleRequest(Transaction transaction, Request<JsonObject> request) throws Exception {
    String rpcSessionId = null;
    try {
      rpcSessionId = transaction.getSession().getSessionId();
      if(rpcSessionId == null){
        log.warn("No RpcSession information Found");
        throw new Exception("No rpcSession information Found");
      }
      if (AcdMsgType.LOGIN_METHOD.equals(request.getMethod())) {
          //新建 RpcConnection 记录连接信息
          String clientId = getStringParam(request, "clientId");
          String clientType = getStringParam(request, "clientType");
          notificationService.newRpcConnection(transaction, clientId, clientType);
      } else if (notificationService.getRpcConnection(rpcSessionId) == null){
          //当前sessionId未login不处理。
        log.warn("NO rpcConnection Found Client");
        throw new Exception("No rpcSession information Found");
      }

      notificationService.addTransaction(transaction, request);

      log.info("hello client :{}",rpcSessionId);
    } catch (Throwable e) {
          log.error("Error getting WebSocket session ID from transaction {}", transaction, e);
      throw e;
    }


    transaction.startAsync();

    switch (request.getMethod()) {
      case AcdMsgType.LOGIN_METHOD:
          clientLogin(transaction, request);
        break;
      case AcdMsgType.LOGOUT_METHOD:
          clientLogout(transaction, request);
        break;
    }


  }
  public static String getStringParam(Request<JsonObject> request, String key) {
    if (request.getParams() == null || request.getParams().get(key) == null) {
      log.warn("Request element '" + key + "' is missing in method '" + request.getMethod());
      return null;
    }
    return request.getParams().get(key).getAsString();
  }

  public void clientLogin(Transaction transaction, Request<JsonObject> request) {

    String rpcSessionId = transaction.getSession().getSessionId();
    String clientId = getStringParam(request, "clientId");
    String clientName = getStringParam(request, "clientName");
    String token = getStringParam(request, "accessToken");
    //客户端类型 Caller or Agent
    String clientType = getStringParam(request, "clientType");
    String platformType = getStringParam(request, "platformType");
    log.info("clientLogin:{} rpcSessionId:{},clientId:{},token:{},clientName:{},platformType:{}",clientType,rpcSessionId,clientId,token,clientName,platformType);

    if (StringUtils.isNotEmpty(clientId)) {
      //把ClientId 与 rpcSessionId的关系保存到map中
      notificationService.addClientId(clientId,rpcSessionId);
    }
    R r = eventService.sendLogin(clientId,clientType,clientName,token,platformType);
    notificationService.sendResponse(rpcSessionId, request.getId(), r);


  }
  public void clientLogout(Transaction transaction, Request<JsonObject> request) throws GrgException {
      String clientId = getStringParam(request, "clientId");
      String rpcSessionId = transaction.getSession().getSessionId();
      log.info("clientLogout rpcSessionId:{},clientId:{}",rpcSessionId,clientId);
      R r = new R(true);
      notificationService.sendResponse(rpcSessionId, request.getId(), r);
      closeRpcSession(rpcSessionId,CLOSE_TYPE_Logout);
  }
  public void closeRpcSession(String rpcSessionId,String closeType) throws GrgException {
      log.info("closeRpcSession rpcSessionId:{},closeType:{}",rpcSessionId,closeType);
      RpcConnection rpcConnection = notificationService.closeRpcSession(rpcSessionId);
      if (rpcConnection != null) {
          String clientType = rpcConnection.getClientType();
          String clientId = rpcConnection.getClientId();
          log.info("closeRpcSession ClientType:{},clientId:{}",clientType,clientId);
          switch (closeType) {
              case CLOSE_TYPE_Logout:
                  eventService.sendLogout(clientId,clientType);
                  break;
              case CLOSE_TYPE_ConnClose:
                  eventService.sendConnectionClosed(clientId,clientType);
                  break;
              case CLOSE_TYPE_TranErr:
                  eventService.sendTransportError(clientId,clientType);
                  break;
          }

      }

  }

  /**
   * 建立连接
   * @param rpcSession
   * @throws Exception
   */
   @Override
   public void afterConnectionEstablished(Session rpcSession) throws Exception {
     log.info("afterConnectionEstablished getSessionId: {}", rpcSession.getSessionId());
     //sessionMap.put("sessionId",rpcSession.getSessionId());
     if (rpcSession instanceof WebSocketServerSession) {
       InetAddress address;
       HttpHeaders headers = ((WebSocketServerSession) rpcSession).getWebSocketSession().getHandshakeHeaders();
       if (headers.containsKey("x-real-ip")) {
         address = InetAddress.getByName(headers.get("x-real-ip").get(0));
       } else {
         address = ((WebSocketServerSession) rpcSession).getWebSocketSession().getRemoteAddress().getAddress();
       }
       rpcSession.getAttributes().put("remoteAddress", address);
     }
  }

  /**
   *  正常关闭，退出登录
   * @param rpcSession
   * @param status
   * @throws Exception
   */
    @Override
    public void afterConnectionClosed(Session rpcSession, String status) throws Exception {
        if (rpcSession == null) {
            log.warn("afterConnectionClosed rpcSession is null");
            return;
        }
        log.info("afterConnectionClosed rpcSession:{},status:{}",rpcSession.getSessionId(),status);
        closeRpcSession(rpcSession.getSessionId(),CLOSE_TYPE_ConnClose);
    }
  /**
   * 发生错误退出登录，关闭session
   * @param rpcSession
   * @throws Exception
   */
  @Override
  public void handleTransportError(Session rpcSession, Throwable exception) throws Exception {
      if (rpcSession == null) {
          log.warn("handleTransportError rpcSession is null");
          return;
      }
      closeRpcSession(rpcSession.getSessionId(),CLOSE_TYPE_TranErr);

      log.warn("Transport exception for WebSocket session: {} - Exception: {}", rpcSession.getSessionId(),
              exception.getMessage());
      if ("IOException".equals(exception.getClass().getSimpleName())
              && "Broken pipe".equals(exception.getCause().getMessage())) {
          log.warn("rpcSession id {} unexpectedly closed the websocket", rpcSession.getSessionId());
      }
      if ("EOFException".equals(exception.getClass().getSimpleName())) {
          log.warn("rpcSession id {} EOFException", rpcSession.getSessionId());
      }
  }






}
