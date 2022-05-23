package com.app.persistence.converter;

import com.app.persistence.model.Car;

import java.util.List;

public class CarsJsonConverter extends JsonConverter<List<Car>> {
    public CarsJsonConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
