package com.zuehlke.pgadmissions.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MathUtils {

    public static Integer convertRatingToPercent(BigDecimal rating) {
        if (rating == null) {
            return null;
        }
        BigDecimal ratingPercentage = rating.multiply(new BigDecimal(100 / 5));
        ratingPercentage = ratingPercentage.setScale(0, RoundingMode.HALF_UP);
        return ratingPercentage.intValueExact();
    }
    
    public static String formatRating(BigDecimal rating){
        if (rating == null) {
            return null;
        }
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(0);
        df.setGroupingUsed(false);
        return df.format(rating);
    }

}
