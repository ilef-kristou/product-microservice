package tn.fst.spring.productmicroservice.repositories;

import tn.fst.spring.productmicroservice.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    // JpaRepository fournit déjà les méthodes CRUD de base
}
