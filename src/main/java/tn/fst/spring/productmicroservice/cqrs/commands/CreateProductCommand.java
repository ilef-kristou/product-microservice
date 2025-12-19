package tn.fst.spring.productmicroservice.cqrs.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CreateProductCommand {

    @TargetAggregateIdentifier
    private final Long id;   // Changé en Long
    private final String name;
    private final double price;

    public CreateProductCommand(String id, String name, double price) {
        this.id = Long.valueOf(id);
        this.name = name;
        this.price = price;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
}