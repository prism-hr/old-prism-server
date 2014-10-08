package com.zuehlke.pgadmissions.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConversionUtils {

    public static BigDecimal floatToBigDecimal(Float input, int precision) {
        return input == null ? null : BigDecimal.valueOf(input).setScale(precision, RoundingMode.HALF_UP);
    }
    
}
