package com.app.persistence.model;

import com.app.persistence.validator.Validator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CarValidator implements Validator<Car> {
    @Override
    public Map<String, String> validate(Car car) {
        var errors = new HashMap<String, String>();

        if (car == null) {
            errors.put("car", "object is null");
            return errors;
        }

        var model = car.model;

        if (model == null) {
            errors.put("model", "is null");
        } else if (!model.matches("[A-Z ]+")) {
            errors.put("model", "doesn't match regex");
        }

        var color = car.color;

        if (color == null) {
            errors.put("color", "is null");
        } else if (!color.getClass().isEnum()) {
            errors.put("color", "is not an enum type");
        }

        var milleage = car.mileage;

        if (milleage < 0) {
            errors.put("milleage", "is negative");
        }

        var price = car.price;

        if (price == null) {
            errors.put("price", "is null");
        } else if (price.compareTo(BigDecimal.ZERO) < 0) {
            errors.put("price", "is negative");
        }
        var components = car.components;

        if (components == null) {
            errors.put("components", "null");
        } else if (components.stream().allMatch(c -> c != null && c.matches("[A-Z]+"))) {
            errors.put("components", "must contain only uppercase letters items");
        }

        return errors;
    }
}
