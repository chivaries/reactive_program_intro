package com.glamrock.rsocket.spring_boot.stock_trading_example.client;

import com.glamrock.rsocket.spring_boot.stock_trading_example.model.MarketData;
import com.glamrock.rsocket.spring_boot.stock_trading_example.model.MarketDataRequest;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class MarketDataRestController {
    private final Random random = new Random();
    private final RSocketRequester rSocketRequester;

    public MarketDataRestController(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    @GetMapping("/current/{stock}")
    public Publisher<MarketData> current(@PathVariable("stock") String stock) {
        return rSocketRequester.route("currentMarketData")
                .data(new MarketDataRequest(stock))
                .retrieveMono(MarketData.class);
    }

    @GetMapping(value = "/feed/{stock}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<MarketData> feed(@PathVariable("stock") String stock) {
        return rSocketRequester.route("feedMarketData")
                .data(new MarketDataRequest(stock))
                .retrieveFlux(MarketData.class);
    }

    @GetMapping(value = "/collect")
    public Publisher<Void> collect() {
        return rSocketRequester.route("collectMarketData")
                .data(getMarketData())
                .send();
    }

    private MarketData getMarketData() {
        return new MarketData("X", random.nextInt(10));
    }
}
