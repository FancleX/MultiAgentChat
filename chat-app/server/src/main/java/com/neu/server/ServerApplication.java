package com.neu.server;

import com.neu.preConnectionTest.PreConnectionTest;
import com.neu.server.nodeManager.NodeManager;
import com.neu.server.sharableResource.SharableResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.net.InetAddress;


@SpringBootApplication
@EntityScan(basePackages = "com.neu.user")
@Slf4j
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
        log.info("Localhost address: " + InetAddress.getLocalHost().getHostAddress());
        // test system port if they are available for the application to start
        boolean nettyPort = PreConnectionTest.testPortAvailable(this.port);
        if (!nettyPort) {
            log.error("Please try to use another ports to start the application");
            System.exit(1);
        }
        new NodeManager(port);
    }
}
