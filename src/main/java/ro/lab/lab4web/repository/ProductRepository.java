package ro.lab.lab4web.repository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import ro.lab.lab4web.model.Product;

@Repository
public class ProductRepository {

    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public List<Product> findAll() {
        return products.values()
                .stream()
                .sorted(Comparator.comparing(Product::getId))
                .toList();
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    public long count() {
        return products.size();
    }

    public List<Product> findByNameContainingIgnoreCase(String name) {
        String searchedName = name.toLowerCase();

        return products.values()
                .stream()
                .filter(product -> product.getName().toLowerCase().contains(searchedName))
                .sorted(Comparator.comparing(Product::getId))
                .toList();
    }

    public List<Product> findByPriceLessThan(BigDecimal price) {
        return products.values()
                .stream()
                .filter(product -> product.getPrice().compareTo(price) < 0)
                .sorted(Comparator.comparing(Product::getId))
                .toList();
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(nextId.getAndIncrement());
        }

        products.put(product.getId(), product);
        return product;
    }

    public boolean deleteById(Long id) {
        return products.remove(id) != null;
    }
}
