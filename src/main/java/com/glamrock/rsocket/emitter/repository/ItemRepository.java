package com.glamrock.rsocket.emitter.repository;

import com.glamrock.rsocket.emitter.model.Item;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ItemRepository {
    private List<Item> items;

    @PostConstruct
    public void init(){
        items = new ArrayList<>();
        items.add(Item.builder().name("item1").price(1000).description("아이템1").build());
        items.add(Item.builder().name("item2").price(2000).description("아이템2").build());
    }

    public Mono<Item> save(Item item) {
        items.add(item);
        return Mono.just(item);
    }

    public Flux<Item> findAll() {
        return Flux.fromIterable(items);
    }
}
