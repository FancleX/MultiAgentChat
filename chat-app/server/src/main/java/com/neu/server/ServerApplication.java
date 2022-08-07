package com.neu.server;

import com.neu.server.nodeManager.NodeManager;
import com.neu.server.nodeManager.dispatcher.TaskDispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication implements CommandLineRunner {

    @Value("${netty.port}")
    private int port;

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        new NodeManager(port, new TaskDispatcher());
    }
}
