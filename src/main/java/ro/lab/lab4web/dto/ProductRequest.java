package ro.lab.lab4web.dto;

import java.math.BigDecimal;

public record ProductRequest(
        String name,
        BigDecimal price,
        int stock,
        String category
) {
}
