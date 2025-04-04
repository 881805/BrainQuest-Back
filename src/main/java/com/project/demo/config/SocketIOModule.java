package com.project.demo.config;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.project.demo.logic.entity.game.Game;
import com.project.demo.logic.entity.message.Message;
import org.springframework.stereotype.Service;

@Service
public class SocketIOModule {

    private SocketIOServer server;

    public SocketIOModule(SocketIOServer server) { // ✅ Inject the same instance
        this.server = server;
        this.server.addConnectListener(client -> {
            System.out.println("New connection: " + client.getSessionId());
            client.sendEvent("start", "Welcome to the server!");
        });

        this.server.addDisconnectListener(client -> {
            System.out.println("Disconnected: " + client.getSessionId());
        });

        SocketIONamespace chatNamespace = server.getNamespace("/chat");
        chatNamespace.addEventListener("sendMessage", Object.class, (client, data, ackSender) -> {
            System.out.println("Received raw message: " + data);
        });
        server.addEventListener("gameInfo", Game.class, (client, game, ackSender) -> {
            System.out.println("Received game object: " + game);

            // Process the game object here (e.g., storing it or sending it back to clients)
            // For example, you can emit a message back to the client with the game details:
            client.sendEvent("message", "Game received: " + game.getGameType());
        });

    }


    private ConnectListener onConnected() {
        return (client -> {
            System.out.println("New connection!"+ client.getSessionId());
        });
    }

    private DisconnectListener onDisconnected() {
        return (client -> {
            System.out.println("Disconnected: " + client.getSessionId());
        });
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop();
    }
}