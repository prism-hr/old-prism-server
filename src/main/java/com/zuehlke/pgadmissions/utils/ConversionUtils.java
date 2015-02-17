package com.zuehlke.pgadmissions.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class ConversionUtils {

    public static BigDecimal floatToBigDecimal(Float input, int precision) {
        return input == null ? null : BigDecimal.valueOf(input).setScale(precision, RoundingMode.HALF_UP);
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
