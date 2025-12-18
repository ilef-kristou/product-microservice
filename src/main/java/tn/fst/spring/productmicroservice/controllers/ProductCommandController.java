package tn.fst.spring.productmicroservice.controllers;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.productmicroservice.entities.Product;
import tn.fst.spring.productmicroservice.cqrs.commands.CreateProductCommand;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/products/commands")
public class ProductCommandController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public ProductCommandController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    public Long createProduct(@RequestBody Product product) {
        // Génère un ID temporaire ou utilise null (la BD générera l'ID via auto-increment)
        CreateProductCommand command = new CreateProductCommand(
                null,  // L'ID sera généré par la base de données
                product.getName(),
                product.getPrice()
        );
        return commandGateway.sendAndWait(command); // Retourne l'ID généré
    }
}