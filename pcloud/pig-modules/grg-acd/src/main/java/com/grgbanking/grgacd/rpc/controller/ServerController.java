package com.grgbanking.grgacd.rpc.controller;


import com.grgbanking.grgacd.rpc.Service.RpcNotificationService;
import com.grgbanking.grgacd.rpc.handler.JsonRpcServerHandler;
import org.kurento.jsonrpc.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/notify")
@RestController
public class ServerController  {

    @Autowired
    JsonRpcServerHandler jsonRpcServerHandler;
    RpcNotificationService rpcNotificationService = new RpcNotificationService();

    Session session;

    @RequestMapping(method = RequestMethod.GET,value = "/send/{id}")
     public String sendMsg(@PathVariable("id") Object id){
        //rpcNotificationService.sendNotification(session.getSessionId(),"sendMsg",params);
       System.out.print(id);
        return "test";
    }

}


