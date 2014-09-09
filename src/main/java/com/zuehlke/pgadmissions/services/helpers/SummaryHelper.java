package com.zuehlke.pgadmissions.services.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SummaryHelper {

    public static Integer incrementRunningCount(Integer currentCount) {
        return currentCount == null ? 1 : currentCount + 1;
    }
    
    public static Integer decrementRunningCount(Integer currentCount) {
        return currentCount == 0 ? 0 : currentCount - 1;
    }

    public static BigDecimal computeRunningAverage(Integer currentValueCount, BigDecimal currentAverage, BigDecimal newValue) {
        if (currentAverage == null) {
            return newValue;
        }

        BigDecimal currentValueCountConverted = new BigDecimal(currentValueCount.toString()).setScale(2);
        BigDecimal newValueCountConverted = currentValueCountConverted.add(new BigDecimal(1));

        return currentValueCountConverted.multiply(currentAverage).add(newValue).divide(newValueCountConverted).setScale(2, RoundingMode.HALF_UP);
    }
    
    public static BigDecimal computeRunningAverage(Integer currentValueCount, BigDecimal currentAverage, Integer newValue) {
        return computeRunningAverage(currentValueCount, currentAverage, new BigDecimal(newValue).setScale(2));
    }
    
}
