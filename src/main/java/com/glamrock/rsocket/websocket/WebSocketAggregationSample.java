package com.glamrock.rsocket.websocket;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketConnector;
import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.ServerTransport;
import io.rsocket.transport.netty.WebsocketDuplexConnection;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.util.ByteBufPayload;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.time.Duration;

@Slf4j
public class WebSocketAggregationSample {
    public static void main(String[] args) {
        ServerTransport.ConnectionAcceptor connectionAcceptor =
                RSocketServer.create(SocketAcceptor.forRequestResponse(Mono::just))
                        .payloadDecoder(PayloadDecoder.ZERO_COPY)
                        .asConnectionAcceptor();

        DisposableServer server =
                HttpServer.create()
                        .host("localhost")
                        .port(0)
                        .handle((req, res) ->
                                res.sendWebsocket(
                                        (in, out) ->
                                                connectionAcceptor
                                                        .apply(new WebsocketDuplexConnection((Connection) in.aggregateFrames()))
                                                        .then(out.neverComplete()))
                        ).bindNow();

        WebsocketClientTransport transport =
                WebsocketClientTransport.create(server.host(), server.port());

        RSocket clientRSocket =
                RSocketConnector.create()
                        .keepAlive(Duration.ofMinutes(10), Duration.ofMinutes(10))
                        .payloadDecoder(PayloadDecoder.ZERO_COPY)
                        .connect(transport)
                        .block();

        Flux.range(1, 100)
                .concatMap(i -> clientRSocket.requestResponse(ByteBufPayload.create("Hello" + i)))
                .doOnNext(payload -> log.debug("Processed " + payload.getDataUtf8()))
                .blockLast();

        clientRSocket.dispose();
        server.dispose();
    }
}
