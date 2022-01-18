package com.glamrock.rsocket.spring_boot.caffe_example.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/*
    https://brunch.co.kr/@springboot/271 참고
 */
@SpringBootApplication
public class ServerDemoApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .main(ServerDemoApplication.class)
                .sources(ServerDemoApplication.class)
                .profiles("server")
                .run(args);
    }
}
