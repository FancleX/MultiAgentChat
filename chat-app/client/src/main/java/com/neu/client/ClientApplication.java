package com.neu.client;

import com.neu.client.dispatcher.ClientTaskDispatcher;
import com.neu.client.driver.ClientDriver;
import com.neu.p2pConnectionGroup.P2PConnectionGroup;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ClientApplication implements CommandLineRunner {

    private static int port;

    public static void main(String[] args) {
        // TODO: take port from args to start the client
        port = Integer.parseInt(args[0]);
        new SpringApplicationBuilder(ClientApplication.class).web(WebApplicationType.NONE).run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        new ClientDriver(port);
    }
}
