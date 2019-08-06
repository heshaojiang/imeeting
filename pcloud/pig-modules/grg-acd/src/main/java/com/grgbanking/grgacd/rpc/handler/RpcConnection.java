package com.grgbanking.grgacd.rpc.handler;

import org.kurento.jsonrpc.Session;
import org.kurento.jsonrpc.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Object representing client-server WebSocket sessions. Stores information
 * about the connection itself and all the active RPC transactions for each one
 * of them.
 *
 * @author Pablo Fuente (pablofuenteperez@gmail.com)
 */
public class RpcConnection {

	private static final Logger log = LoggerFactory.getLogger(RpcConnection.class);

	private Session session;
	private ConcurrentMap<Integer, Transaction> transactions;
	private String rpcSessionId;
	private String clientId;
	private String clientType;

	public RpcConnection(Session session,String clientId,String clientType) {
		this.session = session;
		this.transactions = new ConcurrentHashMap<>();
		this.rpcSessionId = session.getSessionId();
		this.clientId = clientId;
		this.clientType = clientType;
	}

	public Session getSession() {
		return session;
	}

	public String getRpcSessionId() {
		return rpcSessionId;
	}

	public void setRpcSessionId(String rpcSessionId) {
		this.rpcSessionId = rpcSessionId;
	}

	public Transaction getTransaction(Integer transactionId) {
		return transactions.get(transactionId);
	}

	public void addTransaction(Integer transactionId, Transaction t) {
		Transaction oldT = transactions.putIfAbsent(transactionId, t);
		if (oldT != null) {
			log.error("Found an existing transaction for the key {}", transactionId);
		}
	}

	public void removeTransaction(Integer transactionId) {
		transactions.remove(transactionId);
	}

	public Collection<Transaction> getTransactions() {
		return transactions.values();
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientId() {
		return clientId;
	}
	public String getClientType() {
		return clientType;
	}

}
