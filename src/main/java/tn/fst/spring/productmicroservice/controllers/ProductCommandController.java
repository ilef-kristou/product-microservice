package tn.fst.spring.productmicroservice.controllers;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.productmicroservice.entities.Product;
import tn.fst.spring.productmicroservice.cqrs.commands.CreateProductCommand;
import tn.fst.spring.productmicroservice.cqrs.queries.GetProductByIdQuery;

import java.util.UUID;
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
    public String createProduct(@RequestBody Product product) {
        String id = UUID.randomUUID().toString();  // Génère un ID unique
        CreateProductCommand command = new CreateProductCommand(
                id,                      // <-- ID obligatoire
                product.getName(),
                product.getPrice()
        );
        commandGateway.sendAndWait(command);          // Envoi de la commande à l’aggregate
        return id;                                    // Retourne l’ID créé
    }

    @GetMapping("/{id}")
    public CompletableFuture<Product> getProductById(@PathVariable String id) {
        // Crée un objet GetProductByIdQuery avec l'ID
        GetProductByIdQuery query = new GetProductByIdQuery(id);
        return queryGateway.query(query, Product.class);
    }
}
