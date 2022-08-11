package com.neu.server;

import com.neu.server.nodeManager.NodeManager;
import com.neu.server.sharableResource.SharableResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;


@SpringBootApplication
@EntityScan(basePackages = "com.neu.user")
public class ServerApplication implements CommandLineRunner {

    @Value("${netty.port}")
    private int port;

    @Value("${server.port}")
    private int httpPort;

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        SharableResource.myPort = port;
        SharableResource.myHttpPort = httpPort;
        new NodeManager(port);
    }
}
