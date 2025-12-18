package tn.fst.spring.productmicroservice.cqrs.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CreateProductCommand {

    @TargetAggregateIdentifier
    private final String id;   // <-- Ajout de l'ID
    private final String name;
    private final double price;

    public CreateProductCommand(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
}
