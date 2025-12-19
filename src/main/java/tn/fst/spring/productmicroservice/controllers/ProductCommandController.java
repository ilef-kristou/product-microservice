package tn.fst.spring.productmicroservice.controllers;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.productmicroservice.cqrs.queries.GetProductByIdQuery;
import tn.fst.spring.productmicroservice.entities.Product;
import tn.fst.spring.productmicroservice.cqrs.commands.CreateProductCommand;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/products/commands")
public class ProductCommandController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private long tempId = 1L;

    public ProductCommandController(CommandGateway commandGateway , QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    public String createProduct(@RequestBody Product product) {
        // Utiliser UNIQUEMENT des nombres
        Long productId = System.currentTimeMillis() % 1000000;  // Juste un nombre

        double price = product.getPrice() != null ? product.getPrice() : 0.0;

        CreateProductCommand command = new CreateProductCommand(
                String.valueOf(productId),  // Long, pas String
                product.getName() != null ? product.getName() : "Produit",
                price
        );

        try {
            commandGateway.sendAndWait(command);
            return "Produit créé avec ID: " + productId;
        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }

    @GetMapping("/{id}")
    public CompletableFuture<Product> getProductById(@PathVariable Long id) {
        GetProductByIdQuery query = new GetProductByIdQuery(id);
        return queryGateway.query(query, Product.class);
    }
}