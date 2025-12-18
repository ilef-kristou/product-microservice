package tn.fst.spring.productmicroservice.cqrs.events;

public class ProductCreatedEvent {
    private final Long id;  // Changé en Long
    private final String name;
    private final double price;

    public ProductCreatedEvent(Long id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
}