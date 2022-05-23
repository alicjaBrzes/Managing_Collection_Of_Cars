package com.app.service;

import com.app.persistence.converter.CarsJsonConverter;
import com.app.persistence.model.Car;
import com.app.persistence.model.CarValidator;
import com.app.persistence.model.Statistics;
import com.app.persistence.model.type.Color;
import com.app.persistence.model.type.StatisticsSource;
import com.app.persistence.validator.Validator;
import com.app.service.exception.CarsServiceException;
import com.app.service.type.Sort;
import org.eclipse.collections.impl.collector.BigDecimalSummaryStatistics;
import org.eclipse.collections.impl.collector.Collectors2;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.app.persistence.model.CarUtils.*;
import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

public class CarsService {
    private final List<Car> cars;

    public CarsService(String filename) {
        this.cars = init(filename);
    }

    private static List<Car> init(String filename) {
        var carValidator = new CarValidator();

        return new CarsJsonConverter(filename)
                .fromJson()
                .orElseThrow(() -> new CarsServiceException("Cannot read data from json %s%n".formatted(filename)))
                .stream()
                .peek(car -> Validator.validate(car, carValidator))
                .toList();
    }

    /** Sorts collection of Car type elements according to model name, color, price and mileage.
     * In addition it has to be defined whether sorting will hold in descending or ascending order.
     *
     * @param sort sorts collection by chosen criterion
     * @param descending true if descending order, false if ascending
     * @return collection of sorted Car type elements */
    public List<Car> sort(Sort sort, boolean descending) {
        if (sort == null) {
            throw new CarsServiceException("Sort object is null");
        }

        return switch (sort) {

            case COLOR -> cars
                    .stream()
                    .sorted(descending ? comparing(toColor, reverseOrder()) : comparing(toColor))
                    .toList();

            case MODEL -> cars
                    .stream()
                    .sorted(descending ? compareByModelDesc : compareByModelAsc)
                    .toList();

            case PRICE -> cars
                    .stream()
                    .sorted(descending ? comparing(toPrice, reverseOrder()) : comparing(toPrice))
                    .toList();

            case MILEAGE -> cars
                    .stream()
                    .sorted(descending ? compareByMileageDesc : compareByMileageAsc)
                    .toList();
        };
    }

    /** Selects Car type elements, which mileage has greater value than value passed as the parameter.
     *
     * @param mileage value in kilometers
     * @return collection of Car type elements */

    public List<Car> findAllWithMileageGreaterThan(int mileage) {
        return cars
                .stream()
                .filter(car -> car.hasMileageGreaterThan(mileage))
                .toList();
    }

    /** Transforms list of Car type elements to a map where Car's Color is a key. Map's value is an amount
     * of Car type elements, which have equal Color. Map is sorted in descending order according to values.
     *
     * @return map with Color as a key and quantity of Car elements as a value */

    public Map<Color, Long> countByColors() {
        return cars
                .stream()
                .collect(Collectors.groupingBy(
                        toColor,
                        counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, Long::max, LinkedHashMap::new));
    }

    /** Transforms list of Car type elements to a map where Car's Model name is a key. Map's value is a
     * Car object, which represents the most expensive Car with this Model name.
     *
     * @return map with Model name as a key and Car object as a value */
    public Map<String, List<Car>> findMostExpensiveCarForModel() {

        return cars
                .stream()
                .collect(Collectors.groupingBy(
                        toModel,
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(toPrice),
                                entries -> entries
                                        .entrySet()
                                        .stream()
                                        .max(Map.Entry.comparingByKey())
                                        .orElseThrow()
                                        .getValue()
                        )
                ));
    }

    /** Writes Cars statistics in comparison. These pieces of information consists of a minium, average and
     * maximum value for fields that describes price and mileage of the Cars. This method depending on StatisticsSource
     * enum type calls {@link #mileageStatistics()} or {@link #priceStatistics()} method.
     *
     * @param statisticsSource enum with MILEAGE and PRICE types
     * @return Statistics object created with record */

    public Statistics carStatistics(StatisticsSource statisticsSource) {
        return switch (statisticsSource) {
            case MILEAGE -> mileageStatistics();
            case PRICE -> priceStatistics();
            default -> throw new CarsServiceException("Cannot calculate statistics");
        };
    }
    private Statistics mileageStatistics() {
        var stats = cars
                .stream()
                .collect(summarizingInt(toMileage2));
        return Statistics.fromIntSummaryStatistics(stats);
    }
    private Statistics priceStatistics() {
        BigDecimalSummaryStatistics stats = cars
                .stream()
                .collect(Collectors2.summarizingBigDecimal(toPrice2));
        return Statistics.fromBigDecimalSummaryStatistics(stats);
    }

    /** Returns Car with the maximum price value. If there are more than one Car with the maximum price,
     * method returns collection of this Cars.
     *
     * @return Car object or collection of Cars */

    public List<Car> getMostExpensiveCar() {
        return cars
                .stream()
                .collect(Collectors.groupingBy(toPrice))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByKey())
                .orElseThrow(() -> new CarsServiceException("...."))
                .getValue();
    }

    /** Returns collection of Cars where each of them has collection of components sorted in an alphabetical order.
     * In order to sort components this method calls {@link Car#withSortedComponents()} method.
     *
     * @return collection of Cars with its components sorted */

    public List<Car> getCarsWithSortedComponents() {
        return cars
                .stream()
                .map(Car::withSortedComponents)
                .toList();
    }

    /** Transforms list of Car type elements to a map where component name is a key. Map's value is the
     * collection of Cars which owns this component. Pairs in the map are sorted in descending order.
     * This operation is performed according to quantity of elements in the collection that represents
     * pair's value.
     *
     * @return map with component's name as a key and Cars collection as a value */
    public Map<String, List<Car>> groupByComponents() {
        return cars
                .stream()
                .flatMap(car -> toComponents.apply(car).stream())
                .distinct()
                .collect(toMap(Function.identity(), component -> cars
                        .stream()
                        .filter(car -> car.hasComponent(component))
                        .toList()))
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(e -> e.getValue().size(), reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> x,  LinkedHashMap::new));
    }

    /** Selects Cars with a price range between parameters <a, b>. Collection is sorted in an alphabetical order
     * according to the Cars model names. In order to choose Cars with required price this method calls
     * {@link Car#hasPriceBetween(BigDecimal priceFrom, BigDecimal priceTo)} method.
     *
     * @param priceFrom the lower limit of the range
     * @param priceTo the upper limit of the range
     * @return collection of Cars within price range */

    public List<Car> getCarsWithinPriceRange(BigDecimal priceFrom, BigDecimal priceTo) {
        if (priceFrom == null) {
            throw new IllegalArgumentException("Min price is null");
        }

        if (priceTo == null) {
            throw new IllegalArgumentException("Max price is null");
        }

        if (priceTo.compareTo(priceFrom) < 0) {
            throw new IllegalArgumentException("Price range is not correct");
        }

        if (priceFrom.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price range must contain only positive values");
        }

        return cars
                .stream()
                .filter(car -> car.hasPriceBetween(priceFrom, priceTo))
                .sorted(Comparator.comparing(toModel))
                .toList();
    }

    /** Writes custom pieces of information about collection of Car type objects.
     *
     * @return String value with Car description */

    @Override
    public String toString() {
        return cars
                .stream()
                .map(Car::toString)
                .collect(Collectors.joining("\n"));
    }
}
