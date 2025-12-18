package tn.fst.spring.productmicroservice.cqrs.events;

import java.util.UUID;

public class ProductCreatedEvent {
    private final String id;
    private final String name;
    private final double price;

    public ProductCreatedEvent(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public UUID getId() { return UUID.fromString(id); }
    public String getName() { return name; }
    public double getPrice() { return price; }
}
