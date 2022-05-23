package com.app.api;

import com.app.persistence.model.type.StatisticsSource;
import com.app.service.CarsService;
import com.app.service.type.Sort;

import java.math.BigDecimal;

public class App {
    public static void main(String[] args) {
        try {
            final var FILENAME = "C:\\Users\\ala28\\Desktop\\Managing Collection Of Cars\\cars.json";
            var carsService = new CarsService(FILENAME);

            System.out.println();
            System.out.println("-------collection of Cars sorted by color in descending order-------");
            var descendingColorSortedCars = carsService.sort(Sort.COLOR, true);
            descendingColorSortedCars.forEach(System.out::println);

            System.out.println("--------Cars with mileage greater than a passed parameter---------");
            var mileageGreaterThan = carsService.findAllWithMileageGreaterThan(1700);
            System.out.println(mileageGreaterThan);

            System.out.println("---------map with Cars classified and counted by colors---------");
            var byColors = carsService.countByColors();
            System.out.println(byColors);

            System.out.println("------------map with most expensive Car for model------------------");
            var mostExpensiveCarForModel = carsService.findMostExpensiveCarForModel();
            System.out.println(mostExpensiveCarForModel);

            System.out.println("------------Cars statistics in terms of mileage--------------");
            var statistics = carsService.carStatistics(StatisticsSource.MILEAGE);
            System.out.println(statistics);

            System.out.println("-----------------most expensive Car or Cars------------------");
            var mostExpensiveCar = carsService.getMostExpensiveCar();
            System.out.println(mostExpensiveCar);

            System.out.println("------------collection of Cars with sorted components--------------");
            var withSortedComponents = carsService.getCarsWithSortedComponents();
            System.out.println(withSortedComponents);

            System.out.println("---------map with Cars grouped by components---------");
            var byComponents = carsService.groupByComponents();
            System.out.println(byComponents);

            System.out.println("-----------collection of Cars within price range-------------");
            var withinPriceRange = carsService.getCarsWithinPriceRange(new BigDecimal(140), new BigDecimal(160));
            System.out.println(withinPriceRange);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }
}
