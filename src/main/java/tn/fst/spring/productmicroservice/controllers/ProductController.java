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

import java.util.*;

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

    // --- GET all products ---
    @GetMapping
    @Retry(name = "myRetry", fallbackMethod = "fallbackGetAllProducts")
    @RateLimiter(name = "myRateLimiter", fallbackMethod = "fallbackGetAllProducts")
    @CircuitBreaker(name = "productmicroService", fallbackMethod = "fallbackGetAllProducts")
    public List<Product> getAllProducts() {
        System.out.println("getAllProducts appelé à " + new Date());

        // Récupère les produits et ajoute le port à CHACUN
        List<Product> products = productRepository.findAll();
        List<Product> productsWithPort = new ArrayList<>();

        for (Product product : products) {
            productsWithPort.add(copyProductWithPort(product));
        }

        return productsWithPort;
    }

    // Fallback pour getAllProducts - avec port aussi
    public List<Product> fallbackGetAllProducts(Throwable e) {
        System.out.println("FALLBACK getAllProducts activé: " + e.getMessage());

        List<Product> fallbackList = new ArrayList<>();
        Product fallbackProduct = new Product();
        fallbackProduct.setId(-1L);
        fallbackProduct.setName("Service temporairement indisponible (port " + serverPort + ")");
        fallbackProduct.setPrice(0.0);
        fallbackList.add(fallbackProduct);

        return fallbackList;
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
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
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productRepository.findById(id)
                .map(p -> {
                    product.setId(id);
                    Product updated = productRepository.save(product);
                    return copyProductWithPort(updated);
                })
                .orElse(null);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
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

    // --- Méthode utilitaire pour copier un produit et ajouter le port ---
    private Product copyProductWithPort(Product p) {
        Product copy = new Product();
        copy.setId(p.getId());
        copy.setName(p.getName() + " (port " + serverPort + ")");
        copy.setPrice(p.getPrice());
        return copy;
    }

    // --- Endpoint de test Resilience4j ---
    @GetMapping("/test/rate-limiter")
    @RateLimiter(name = "myRateLimiter", fallbackMethod = "testRateLimiterFallback")
    public String testRateLimiter() {
        return "Requête acceptée à " + new Date() + " (port " + serverPort + ")";
    }

    public String testRateLimiterFallback(Throwable e) {
        return "⏸️ Rate limit atteint ! Veuillez patienter. " + new Date() + " (port " + serverPort + ")";
    }

    @GetMapping("/test/retry")
    @Retry(name = "myRetry", fallbackMethod = "testRetryFallback")
    public String testRetry() {
        System.out.println("Tentative testRetry à " + new Date());
        return "✅ Succès après retry à " + new Date() + " (port " + serverPort + ")";
    }

    public String testRetryFallback(Throwable e) {
        return "❌ Échec après 3 tentatives. Erreur: " + e.getMessage() + " (port " + serverPort + ")";
    }
}