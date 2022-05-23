package com.app.persistence.model;

import com.app.persistence.model.type.Color;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public interface CarUtils {
    Function<Car, String> toModel = car -> car.model;

    Function<Car, BigDecimal> toPrice = car -> car.price;
    org.eclipse.collections.api.block.function.Function<Car, BigDecimal> toPrice2 = car -> car.price;

    Function<Car, Color> toColor = car -> car.color;

    Comparator<Car> compareByMileageAsc = Comparator.comparing(toModel);

    Comparator<Car> compareByMileageDesc = Comparator.comparing(toModel, Comparator.reverseOrder());
    ToIntFunction<Car> toMileage2 = car -> car.mileage;

    Function<Car, List<String>> toComponents = car -> car.components;

    Comparator<Car> compareByModelAsc = Comparator.comparing(toModel);
    Comparator<Car> compareByModelDesc = Comparator.comparing(toModel, Comparator.reverseOrder());

}
