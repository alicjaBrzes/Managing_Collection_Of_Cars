package com.app.persistence.model;

import com.app.persistence.model.exception.CarModelException;
import com.app.persistence.model.type.Color;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Car {
    String model;
    BigDecimal price;
    Color color;
    int mileage;
    List<String> components;

    public boolean hasMileageGreaterThan(int limit) {
        if (limit <= 0) {
            throw new CarModelException("Limit value must be positive");
        }
        return mileage > limit;
    }
    public boolean hasPriceBetween(BigDecimal priceFrom, BigDecimal priceTo) {
        return price.compareTo(priceFrom) >= 0 && price.compareTo(priceTo) <= 0;
    }

    /** Creates Car object with builder design pattern.
     *
     * @return Car object with its components sorted in alphapetical order */
    public Car withSortedComponents() {
        return Car
                .builder()
                .model(model)
                .color(color)
                .mileage(mileage)
                .price(price)
                .components(components.stream().sorted().toList())
                .build();
    }
    public boolean hasComponent(String component) {
        return components.contains(component);
    }

    @Override
    public String toString() {
        return "MODEL: %s, PRICE: %s, COLOR: %s, MILEAGE: %d, COMPONENTS: [%s]".formatted(
                model,
                price,
                color,
                mileage,
                String.join("\t", components)
        );
    }
}
