package com.zuehlke.pgadmissions.utils;

import static java.math.RoundingMode.HALF_UP;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PrismConversionUtils {

    public static BigDecimal doubleToBigDecimal(Double input, int precision) {
        return input == null ? null : BigDecimal.valueOf(input).setScale(precision, HALF_UP);
    }

    public static Integer longToInteger(Long input) {
        return input == null ? null : input.intValue();
    }

    public static Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

}
