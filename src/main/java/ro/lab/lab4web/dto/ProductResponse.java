package ro.lab.lab4web.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        int stock,
        String category,
        Instant createdAt
) {
}
