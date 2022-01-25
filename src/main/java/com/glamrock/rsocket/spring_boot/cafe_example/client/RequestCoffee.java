package com.glamrock.rsocket.spring_boot.cafe_example.client;

import lombok.Data;

@Data
public class RequestCoffee {
    private String name;

    public RequestCoffee(){
    }

    public RequestCoffee(String name){
        this.name = name;
    }

}