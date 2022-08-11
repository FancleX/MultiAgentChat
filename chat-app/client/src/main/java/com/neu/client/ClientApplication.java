package com.neu.client;

import com.neu.client.driver.ClientDriver;
import com.neu.client.sharableResource.SharableResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.net.InetAddress;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
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
        port = Integer.parseInt(args[0]);
        new SpringApplicationBuilder(ClientApplication.class).web(WebApplicationType.NONE).run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        String hostName = InetAddress.getLocalHost().getHostName();
        SharableResource.baseURL = baseURL;
        SharableResource.serverHostname = serverHostname;
        SharableResource.serverPort = serverPort;
        new ClientDriver(hostName, port);
    }
}
