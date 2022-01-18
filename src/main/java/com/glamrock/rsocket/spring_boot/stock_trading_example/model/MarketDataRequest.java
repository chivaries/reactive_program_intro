package com.glamrock.rsocket.spring_boot.stock_trading_example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketDataRequest {
    private String stock;
}
