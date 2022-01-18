package com.glamrock.rsocket.spring_boot.stock_trading_example.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/*
 https://www.baeldung.com/spring-boot-rsocket 참고
 */
@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .main(ServerApplication.class)
                .sources(ServerApplication.class)
                .profiles("server")
                .run(args);
    }
}
