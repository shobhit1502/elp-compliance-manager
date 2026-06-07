package com.elp.compliance_manager.product;

import com.elp.compliance_manager.product.dto.ProductRequestDTO;
import com.elp.compliance_manager.product.dto.ProductResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponseDTO createProduct(ProductRequestDTO request) {
        if (productRepository.existsByNameAndVersionAndEdition(
                request.getName(),
                request.getVersion(),
                request.getEdition())) {
            throw new RuntimeException("Product already exists with same name, version and edition");
        }
        Product product = Product.builder()
                .name(request.getName())
                .version(request.getVersion())
                .edition(request.getEdition())
                .category(request.getCategory())
                .description(request.getDescription())
                .build();
        return toResponseDTO(productRepository.save(product));
    }

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Product not found with id: " + id));
        return toResponseDTO(product);
    }

    public List<ProductResponseDTO> getProductsByCategory(
            ProductCategory category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO updateProduct(Long id,
                                            ProductRequestDTO request) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Product not found with id: " + id));
        existing.setName(request.getName());
        existing.setVersion(request.getVersion());
        existing.setEdition(request.getEdition());
        existing.setCategory(request.getCategory());
        existing.setDescription(request.getDescription());
        return toResponseDTO(productRepository.save(existing));
    }

    public void deactivateProduct(Long id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Product not found with id: " + id));
        existing.setActive(false);
        productRepository.save(existing);
    }

    private ProductResponseDTO toResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .version(product.getVersion())
                .edition(product.getEdition())
                .category(product.getCategory())
                .description(product.getDescription())
                .isActive(product.isActive())
                .createdAt(product.getCreatedAt())
                .build();
    }
}