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

    private static int port;

    @Value("${server.port}")
    private int httpPort;

    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);

                SpringApplication.run(ServerApplication.class, args);
            } catch (NumberFormatException e) {
                System.out.println("Invalid format of ports");
            }
        } else {
            System.out.println("Please specify <p2p port> to start the application");
        }

    }

    @Override
    public void run(String... args) throws Exception {
        SharableResource.myPort = port;
        SharableResource.myHttpPort = httpPort;
        log.info("Localhost address: " + InetAddress.getLocalHost().getHostAddress());
        // test system port if they are available for the application to start
        boolean nettyPort = PreConnectionTest.testPortAvailable(port);
        if (!nettyPort) {
            log.error("Please try to use another ports to start the application");
            System.exit(1);
        }
        new NodeManager(port);
    }
}
