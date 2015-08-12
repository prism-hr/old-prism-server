package com.zuehlke.pgadmissions.utils;

import static java.math.RoundingMode.HALF_UP;

import java.math.BigDecimal;

public class PrismConversionUtils {

    public static BigDecimal doubleToBigDecimal(Double input, int precision) {
        return input == null ? null : BigDecimal.valueOf(input).setScale(precision, HALF_UP);
    }

    public static BigDecimal decimalObjectToBigDecimal(Object value, int precision) {
        Class<?> valueClass = value.getClass();
        if (valueClass.equals(Double.class)) {
            return doubleToBigDecimal((Double) value, precision);
        } else if (valueClass.equals(BigDecimal.class)) {
            return (BigDecimal) value;
        }
        throw new Error();
    }

    public static Integer longToInteger(Long input) {
        return input == null ? null : input.intValue();
    }

}
