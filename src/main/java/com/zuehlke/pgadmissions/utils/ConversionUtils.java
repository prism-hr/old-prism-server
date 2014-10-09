package com.zuehlke.pgadmissions.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.BooleanUtils;

public class ConversionUtils {

    public static BigDecimal floatToBigDecimal(Float input, int precision) {
        return input == null ? null : BigDecimal.valueOf(input).setScale(precision, RoundingMode.HALF_UP);
    }
    
    public static String booleanToString(Boolean value, String trueString, String falseString) {
        return BooleanUtils.isTrue(value) ? trueString : falseString;
    }
    
}
