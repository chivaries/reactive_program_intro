package com.glamrock.rsocket.spring_boot.caffe_example.client;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Coffee {
    private String name;
    private int price;
}
