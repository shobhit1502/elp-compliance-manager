package com.elp.compliance_manager.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(ProductCategory category);

    List<Product> findByIsActiveTrue();

    List<Product> findByNameContainingIgnoreCase(String name);

    boolean existsByNameAndVersionAndEdition(
            String name, String version, String edition);
}