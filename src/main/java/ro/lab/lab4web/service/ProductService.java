package ro.lab.lab4web.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import ro.lab.lab4web.dto.ProductRequest;
import ro.lab.lab4web.dto.ProductResponse;
import ro.lab.lab4web.exception.ProductNotFoundException;
import ro.lab.lab4web.model.Product;

@Service
public class ProductService {

    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public ProductService() {
        createProduct(new ProductRequest("Laptop", BigDecimal.valueOf(3500), 8, "Electronice"));
        createProduct(new ProductRequest("Mouse", BigDecimal.valueOf(80), 25, "Electronice"));
        createProduct(new ProductRequest("Caiet", BigDecimal.valueOf(12), 50, "Papetarie"));
    }

    public List<ProductResponse> getAllProducts() {
        return products.values()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getProductById(Long id) {
        return toResponse(findProduct(id));
    }

    public long countProducts() {
        return products.size();
    }

    public List<ProductResponse> searchProductsByName(String name) {
        String searchedName = name.toLowerCase();

        return products.values()
                .stream()
                .filter(product -> product.getName().toLowerCase().contains(searchedName))
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse createProduct(ProductRequest request) {
        validateProduct(request);

        Long id = nextId.getAndIncrement();
        Product product = new Product(
                id,
                request.name(),
                request.price(),
                request.stock(),
                request.category()
        );

        products.put(id, product);
        return toResponse(product);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        validateProduct(request);

        Product product = findProduct(id);
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(request.category());

        return toResponse(product);
    }

    public void deleteProduct(Long id) {
        Product removedProduct = products.remove(id);

        if (removedProduct == null) {
            throw new ProductNotFoundException(id);
        }
    }

    private Product findProduct(Long id) {
        Product product = products.get(id);

        if (product == null) {
            throw new ProductNotFoundException(id);
        }

        return product;
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
                product.getCategory()
        );
    }
}
