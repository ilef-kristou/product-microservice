package tn.fst.spring.productmicroservice.cqrs.projections;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tn.fst.spring.productmicroservice.cqrs.events.ProductCreatedEvent;
import tn.fst.spring.productmicroservice.entities.Product;
import tn.fst.spring.productmicroservice.repositories.ProductRepository;
import tn.fst.spring.productmicroservice.cqrs.queries.GetProductByIdQuery;

@Component
public class ProductProjection {

    @Autowired
    private ProductRepository productRepository;

    @EventHandler
    public void on(ProductCreatedEvent event) {
        Product product = new Product();
        product.setId(event.getId()); // ID Long
        product.setName(event.getName());
        product.setPrice(event.getPrice());
        productRepository.save(product);
    }

    @QueryHandler
    public Product handle(GetProductByIdQuery query) {
        return productRepository.findById(query.getId()).orElse(null);
    }
}