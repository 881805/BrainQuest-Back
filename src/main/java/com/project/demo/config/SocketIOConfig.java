package com.project.demo.config;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketIOConfig {

    @Value("${server.port}")
    private int port;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname("localhost");
        config.setPort(8081); // Change to a different port
        config.setOrigin("*");
        config.setTransports(Transport.WEBSOCKET);
        SocketIOServer server = new SocketIOServer(config);
        SocketIONamespace agentsNamespace = server.addNamespace("/chat");
        return server;
    }

    @Bean
    public CommandLineRunner socketIORunner(SocketIOServer socketIOServer, SocketIOModule socketIOModule) {
        return args -> {
            socketIOModule.start();
            System.out.println("SocketIO server started on port: 8081");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Stopping SocketIO server...");
                socketIOModule.stop();
                System.out.println("SocketIO server stopped.");
            }));
        };
    }
}