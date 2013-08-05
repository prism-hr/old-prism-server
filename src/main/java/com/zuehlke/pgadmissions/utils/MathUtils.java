package com.zuehlke.pgadmissions.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {

    public static Integer convertRatingToPercent(BigDecimal rating) {
        if (rating == null) {
            return null;
        }
        BigDecimal ratingPercentage = rating.multiply(new BigDecimal(100 / 5));
        ratingPercentage = ratingPercentage.setScale(0, RoundingMode.HALF_UP);
        return ratingPercentage.intValueExact();
    }

}
