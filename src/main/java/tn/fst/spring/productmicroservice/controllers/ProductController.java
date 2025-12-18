package tn.fst.spring.productmicroservice.controllers;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import tn.fst.spring.productmicroservice.entities.Product;
import tn.fst.spring.productmicroservice.repositories.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${server.port}")
    private String serverPort;

    private final String USER_SERVICE_BASE_URL = "http://user-microservice/users";

    // --- GET all products réel avec Resilience4j ---
    @GetMapping
    @Retry(name = "myRetry", fallbackMethod = "fallbackGetAllProducts")
    @RateLimiter(name = "myRateLimiter", fallbackMethod = "fallbackGetAllProducts")
    @CircuitBreaker(name = "productmicroService", fallbackMethod = "fallbackGetAllProducts")
    public List<Product> getAllProducts() {

        List<Product> productsFromDb = productRepository.findAll();
        List<Product> productsWithPort = new ArrayList<>();

        if (productsFromDb.isEmpty()) {
            Product emptyProduct = new Product();
            emptyProduct.setName("No products found (port " + serverPort + ")");
            productsWithPort.add(emptyProduct);
        } else {
            for (Product p : productsFromDb) {
                productsWithPort.add(copyProductWithPort(p));
            }
        }
        return productsWithPort;
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable UUID id) {
        return productRepository.findById(id)
                .map(this::copyProductWithPort)
                .orElse(null);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        Product saved = productRepository.save(product);
        return copyProductWithPort(saved);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable UUID id, @RequestBody Product product) {
        return productRepository.findById(id)
                .map(p -> {
                    product.setId(id);
                    Product updated = productRepository.save(product);
                    return copyProductWithPort(updated);
                })
                .orElse(null);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable UUID id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return "Deleted product " + id + " on port " + serverPort;
        }
        return "Product not found";
    }

    // --- Endpoint pour récupérer les users via Ribbon ---
    @GetMapping("/users")
    public List<Map<String, Object>> getUsersFromUserService() {
        List<Map<String, Object>> users = restTemplate.getForObject(USER_SERVICE_BASE_URL, List.class);
        return users;
    }

    // --- Méthode fallback pour Resilience4j ---
    public List<Product> fallbackGetAllProducts(Exception e) {
        List<Product> fallbackList = new ArrayList<>();
        Product fallbackProduct = new Product();
        fallbackProduct.setName("Service indisponible – veuillez réessayer plus tard");
        fallbackList.add(fallbackProduct);
        return fallbackList;
    }

    // --- Méthode utilitaire pour copier un produit et ajouter le port ---
    private Product copyProductWithPort(Product p) {
        Product copy = new Product();
        copy.setId(p.getId());
        copy.setName(p.getName() + " (port " + serverPort + ")");
        copy.setPrice(p.getPrice());
        return copy;
    }
}
