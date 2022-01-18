package com.glamrock.rsocket.spring_boot.caffe_example.client;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ClientDemoApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .main(ClientDemoApplication.class)
                .sources(ClientDemoApplication.class)
                .profiles("client")
                .run(args);
    }
}
