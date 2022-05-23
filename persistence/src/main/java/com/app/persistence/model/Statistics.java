package com.app.persistence.model;

import org.eclipse.collections.impl.collector.BigDecimalSummaryStatistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.IntSummaryStatistics;

public record Statistics(BigDecimal min, BigDecimal avg, BigDecimal max) {
    public static Statistics fromIntSummaryStatistics(IntSummaryStatistics iss) {
        return new Statistics(
                BigDecimal.valueOf(iss.getMin()),
                new BigDecimal(iss.getAverage()).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(iss.getMax()));
    }

    public static Statistics fromBigDecimalSummaryStatistics(BigDecimalSummaryStatistics bss) {
        return new Statistics(
                bss.getMin(),
                bss.getAverage(),
                bss.getMax());
    }
}
