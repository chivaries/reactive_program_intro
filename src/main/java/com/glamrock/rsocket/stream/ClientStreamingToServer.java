package com.glamrock.rsocket.stream;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketConnector;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
public class ClientStreamingToServer {
    public static void main(String[] args) throws InterruptedException {
        RSocketServer.create(
                        SocketAcceptor.forRequestStream(
                                payload ->
                                        Flux.interval(Duration.ofMillis(100))
                                                .map(aLong -> DefaultPayload.create("Interval: " + aLong))))
                .bindNow(TcpServerTransport.create("localhost", 7000));

        RSocket socket =
                RSocketConnector.create()
                        .setupPayload(DefaultPayload.create("test", "test"))
                        .connect(TcpClientTransport.create("localhost", 7000))
                        .block();

        final Payload payload = DefaultPayload.create("Hello");
        socket.requestStream(payload)
                .map(Payload::getDataUtf8)
                .doOnNext(log::debug)
                .take(10)
                .then()
                .doFinally(signalType -> socket.dispose())
                .then()
                .block();

        Thread.sleep(1000000);
    }
}
