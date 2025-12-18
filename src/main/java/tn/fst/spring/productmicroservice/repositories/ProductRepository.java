package tn.fst.spring.productmicroservice.repositories;

import tn.fst.spring.productmicroservice.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // JpaRepository fournit déjà les méthodes CRUD de base
}