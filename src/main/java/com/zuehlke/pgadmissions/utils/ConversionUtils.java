package com.zuehlke.pgadmissions.utils;

import java.math.BigDecimal;

public class ConversionUtils {

    public static BigDecimal floatToBigDecimal(Float input) throws Exception {
        return input == null ? null : BigDecimal.valueOf(input);
    }
    
}
