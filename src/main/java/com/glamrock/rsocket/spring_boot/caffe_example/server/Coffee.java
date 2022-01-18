package com.glamrock.rsocket.spring_boot.caffe_example.server;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Coffee {
    private String name;
    private int price;
}
