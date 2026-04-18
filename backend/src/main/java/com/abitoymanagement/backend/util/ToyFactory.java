package com.abitoymanagement.backend.util;

import com.abitoymanagement.backend.model.ElectronicToy;
import com.abitoymanagement.backend.model.SoftToy;
import com.abitoymanagement.backend.model.Toy;
import java.math.BigDecimal;
import java.util.Locale;

public final class ToyFactory {

    private ToyFactory() {
    }

    public static Toy createToy(String toyId, String name, String category, String brand, BigDecimal price,
                                int quantity, String ageGroup, String description, String imageFileName,
                                String toyType) {
        String resolvedToyType = resolveToyType(toyType, category);

        return switch (resolvedToyType) {
            case "Electronic Toy" -> new ElectronicToy(
                    toyId, name, category, brand, price, quantity, ageGroup, description, imageFileName);
            case "Soft Toy" -> new SoftToy(
                    toyId, name, category, brand, price, quantity, ageGroup, description, imageFileName);
            default -> new Toy(
                    toyId, name, category, brand, price, quantity, ageGroup, description, imageFileName);
        };
    }

    private static String resolveToyType(String toyType, String category) {
        if (toyType != null && !toyType.isBlank()) {
            return normalizeToyType(toyType);
        }

        String normalizedCategory = category == null ? "" : category.toLowerCase(Locale.ROOT);
        if (normalizedCategory.contains("electronic") || normalizedCategory.contains("remote")) {
            return "Electronic Toy";
        }
        if (normalizedCategory.contains("soft") || normalizedCategory.contains("plush")) {
            return "Soft Toy";
        }
        return "General Toy";
    }

    private static String normalizeToyType(String toyType) {
        String normalizedToyType = toyType.trim().toLowerCase(Locale.ROOT);
        if (normalizedToyType.contains("electronic")) {
            return "Electronic Toy";
        }
        if (normalizedToyType.contains("soft")) {
            return "Soft Toy";
        }
        return "General Toy";
    }
}
