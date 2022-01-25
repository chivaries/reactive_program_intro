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
public class WebSocketHeaderSample {
    public static void main(String[] args) {
        ServerTransport.ConnectionAcceptor connectionAcceptor =
                RSocketServer.create(SocketAcceptor.forRequestResponse(Mono::just))
                        .payloadDecoder(PayloadDecoder.ZERO_COPY)
                        .asConnectionAcceptor();

        DisposableServer server =
                HttpServer.create()
                        .host("localhost")
                        .port(0)
                        .route(routes ->
                                routes.get(
                                        "/",
                                        (req, res) -> {
                                            if (req.requestHeaders().containsValue("Authorization", "test", true)) {
                                                return res.sendWebsocket(
                                                        (in, out) ->
                                                                connectionAcceptor
                                                                        .apply(new WebsocketDuplexConnection((Connection) in))
                                                                        .then(out.neverComplete()));
                                            }
                                            res.status(HttpResponseStatus.UNAUTHORIZED);
                                            return res.send();
                                        }
                                )
                        ).bindNow();

        log.debug("\n\nStart of Authorized WebSocket Connection\n----------------------------------\n");

        WebsocketClientTransport transport =
                WebsocketClientTransport.create(server.host(), server.port())
                        .header("Authorization", "test");

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

        log.debug("\n\nStart of Unauthorized WebSocket Upgrade\n----------------------------------\n");

        RSocketConnector.create()
                .keepAlive(Duration.ofMinutes(10), Duration.ofMinutes(10))
                .payloadDecoder(PayloadDecoder.ZERO_COPY)
                .connect(WebsocketClientTransport.create(server.host(), server.port()))
                .block();
    }
}
