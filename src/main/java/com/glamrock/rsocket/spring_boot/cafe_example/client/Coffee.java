package com.glamrock.rsocket.spring_boot.cafe_example.client;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Coffee {
    private String name;
    private int price;
}
