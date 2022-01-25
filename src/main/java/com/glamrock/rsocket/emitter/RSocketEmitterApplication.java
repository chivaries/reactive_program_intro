package com.glamrock.rsocket.emitter;

import com.glamrock.rsocket.spring_boot.stock_trading_example.server.ServerApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class RSocketEmitterApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .main(RSocketEmitterApplication.class)
                .sources(RSocketEmitterApplication.class)
                .profiles("server")
                .run(args);
    }
}
