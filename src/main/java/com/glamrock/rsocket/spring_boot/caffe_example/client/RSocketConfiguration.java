package com.glamrock.rsocket.spring_boot.caffe_example.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeTypeUtils;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
public class RSocketConfiguration {
    @Bean
    RSocketRequester rSocketRequester() {
        RSocketRequester.Builder builder = RSocketRequester.builder();
        return builder
                .rsocketConnector(connector ->
                        connector.reconnect(Retry.fixedDelay(2, Duration.ofSeconds(2)))
                )
                .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
                .tcp("localhost", 7000);
    }
}
