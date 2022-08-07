package com.neu.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        // TODO: take port from args to start the client
        SpringApplication.run(ClientApplication.class, args);
    }

}
