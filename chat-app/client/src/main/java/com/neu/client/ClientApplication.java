package com.neu.client;

import com.neu.client.driver.ClientDriver;
import com.neu.client.sharableResource.SharableResource;
import com.neu.preConnectionTest.PreConnectionTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.net.InetAddress;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@Slf4j
public class ClientApplication implements CommandLineRunner {

    // client port
    private static int port;

    @Value("${base.url}")
    private String baseURL;

    @Value("${netty.server.hostname}")
    private String serverHostname;

    @Value("${netty.server.port}")
    private int serverPort;

    public static void main(String[] args) {
        // take port from args to start the client
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                log.error("Invalid arguments provided");
                System.exit(1);
            }
        } else {
            log.error("Please run the application with <port> parameters");
            System.exit(1);
        }

        new SpringApplicationBuilder(ClientApplication.class).web(WebApplicationType.NONE).run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        String hostName = InetAddress.getLocalHost().getHostName();
        SharableResource.baseURL = baseURL;
        SharableResource.serverHostname = serverHostname;
        SharableResource.serverPort = serverPort;
        // test system ports if they are available for the application to start
        boolean nettyPort = PreConnectionTest.testPortAvailable(port);
        if (!nettyPort) {
            log.error("Please try to use another port to start the application");
            System.exit(1);
        }
        new ClientDriver(hostName, port);
    }
}
