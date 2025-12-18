package tn.fst.spring.productmicroservice.services;

import tn.fst.spring.productmicroservice.entities.Product;
import tn.fst.spring.productmicroservice.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    // CRUD methods
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Optional<Product> getProductById(UUID id) {
        return repository.findById(id);
    }

    public Product saveProduct(Product product) {
        return repository.save(product);
    }

    public Product updateProduct(UUID id, Product product) {
        return repository.findById(id)
                .map(p -> {
                    p.setName(product.getName());
                    p.setPrice(product.getPrice());
                    return repository.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void deleteProduct(UUID id) {
        repository.deleteById(id);
    }
}
