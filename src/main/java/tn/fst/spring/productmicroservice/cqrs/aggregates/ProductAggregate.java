package tn.fst.spring.productmicroservice.cqrs.aggregates;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import static org.axonframework.modelling.command.AggregateLifecycle.apply;

import org.springframework.beans.factory.annotation.Autowired;
import tn.fst.spring.productmicroservice.cqrs.commands.CreateProductCommand;
import tn.fst.spring.productmicroservice.cqrs.events.ProductCreatedEvent;
import tn.fst.spring.productmicroservice.entities.Product;
import tn.fst.spring.productmicroservice.repositories.ProductRepository;

@Aggregate
public class ProductAggregate {

    @AggregateIdentifier
    private String id;
    private String name;
    private double price;

    public ProductAggregate() {}

    @CommandHandler
    public ProductAggregate(CreateProductCommand command) {
        // Publie un événement
        apply(new ProductCreatedEvent(command.getId(), command.getName(), command.getPrice()));
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent event) {
        this.id = String.valueOf(event.getId());
        this.name = event.getName();
        this.price = event.getPrice();
    }
}
