package com.qb.websocketdemo.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天室 服务端
 */
@Component
@ServerEndpoint("/chat-room/{username}")
public class ChatRoomServerEndpoint {

    private static Map<String,Session> livingSessions = new ConcurrentHashMap<>();


    @OnOpen
    public void openSession(@PathParam("username") String username, Session session){
        livingSessions.put(session.getId(),session);
        this.sendTextAll("欢迎用户：["+username+"] 来到聊天室! ");
    }

    @OnMessage
    public void onMessage(@PathParam("username") String username,Session session,String message){
        //sendText(session,"用户["+username+"] ：" + message);
        sendTextAll("用户["+username+"] ：" + message);
    }
    private void sendTextAll(String msg){
        livingSessions.forEach((sessionId,session) -> {
            sendText(session,msg);
        });
    }
    @OnClose
    public void onClose(@PathParam("username") String username, Session session){
        livingSessions.remove(session.getId());
        //通知其他用户

        sendTextAll("用户["+username+"] 退出了聊天室！");

    }

    private void sendText(Session session,String message){
        RemoteEndpoint.Basic basic = session.getBasicRemote();
        try {
            basic.sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
