package ro.lab.lab4web.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ro.lab.lab4web.dto.ProductRequest;
import ro.lab.lab4web.dto.ProductResponse;
import ro.lab.lab4web.dto.UpdateStockRequest;
import ro.lab.lab4web.exception.ProductNotFoundException;
import ro.lab.lab4web.model.Product;
import ro.lab.lab4web.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;

        createProduct(new ProductRequest("Laptop", BigDecimal.valueOf(3500), 8, "Electronice"));
        createProduct(new ProductRequest("Mouse", BigDecimal.valueOf(80), 25, "Electronice"));
        createProduct(new ProductRequest("Caiet", BigDecimal.valueOf(12), 50, "Papetarie"));
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getProductById(Long id) {
        return toResponse(findProduct(id));
    }

    public long countProducts() {
        return productRepository.count();
    }

    public List<ProductResponse> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductResponse> findProductsWithPriceLessThan(BigDecimal price) {
        return productRepository.findByPriceLessThan(price)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse createProduct(ProductRequest request) {
        validateProduct(request);

        Product product = new Product(
                null,
                request.name(),
                request.price(),
                request.stock(),
                request.category(),
                null
        );

        return toResponse(productRepository.save(product));
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        validateProduct(request);

        Product product = findProduct(id);
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(request.category());

        return toResponse(productRepository.save(product));
    }

    public ProductResponse updateStock(Long id, UpdateStockRequest request) {
        validateStock(request);

        Product product = findProduct(id);
        product.setStock(request.stock());

        return toResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        if (!productRepository.deleteById(id)) {
            throw new ProductNotFoundException(id);
        }
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private void validateProduct(ProductRequest request) {
        List<String> errors = new ArrayList<>();

        if (request.name() == null || request.name().isBlank()) {
            errors.add("name is required");
        }

        if (request.price() == null || request.price().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("price must be strictly positive");
        }

        if (request.stock() < 0) {
            errors.add("stock cannot be negative");
        }

        if (request.category() == null || request.category().isBlank()) {
            errors.add("category is required");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }
    }

    private void validateStock(UpdateStockRequest request) {
        List<String> errors = new ArrayList<>();

        if (request.stock() == null) {
            errors.add("stock is required");
        } else if (request.stock() < 0) {
            errors.add("stock cannot be negative");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                product.getCreatedAt()
        );
    }
}
