package com.zuehlke.pgadmissions.utils;

import static com.zuehlke.pgadmissions.utils.PrismConstants.TARGETING_PRECISION;

import java.math.BigDecimal;

public class PrismGeocodingUtils {

    public static BigDecimal getHaversineDistance(BigDecimal baseLatitude, BigDecimal baseLongitude, BigDecimal targetLatitude, BigDecimal targetLongitude) {
        if (!(baseLatitude == null || baseLongitude == null || targetLatitude == null || targetLongitude == null)) {
            double baseLatitudeDouble = baseLatitude.doubleValue();
            double baseLongitudeDouble = baseLongitude.doubleValue();

            double targetLatitudeDouble = targetLatitude.doubleValue();
            double targetLongitudeDouble = targetLongitude.doubleValue();

            Double distance = (3959 * Math.acos(Math.cos(Math.toRadians(baseLatitudeDouble)) //
                    * Math.cos(Math.toRadians(targetLatitudeDouble)) //
                    * Math.cos((Math.toRadians(targetLongitudeDouble) - Math.toRadians(baseLongitudeDouble))) + (Math.sin(Math.toRadians(baseLatitudeDouble))
                            * Math.sin(Math.toRadians(baseLongitudeDouble)))));

            return PrismConversionUtils.doubleToBigDecimal(distance, TARGETING_PRECISION);
        }
        return null;
    }

}
