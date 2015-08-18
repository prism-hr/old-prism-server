package com.zuehlke.pgadmissions.utils;

import static com.zuehlke.pgadmissions.utils.PrismConstants.TARGETING_PRECISION;

import java.math.BigDecimal;

import com.zuehlke.pgadmissions.domain.location.AddressCoordinates;

public class PrismGeocodingUtils {

    public static BigDecimal getHaversideDistance(AddressCoordinates baseCoordinates, AddressCoordinates targetCoordinates) {
        if (!(baseCoordinates == null || targetCoordinates == null)) {
            double baseLatitude = baseCoordinates.getLatitude().doubleValue();
            double baseLongitude = baseCoordinates.getLongitude().doubleValue();

            double targetLatitude = targetCoordinates.getLatitude().doubleValue();
            double targetLongitude = targetCoordinates.getLongitude().doubleValue();

            Double distance = (3959 * Math.acos(Math.cos(Math.toRadians(baseLatitude)) //
                    * Math.cos(Math.toRadians(targetLatitude)) //
                    * Math.cos((Math.toRadians(targetLongitude) - Math.toRadians(baseLongitude))) + (Math.sin(Math.toRadians(baseLatitude))
                            * Math.sin(Math.toRadians(targetLatitude)))));
            
            return PrismConversionUtils.doubleToBigDecimal(distance, TARGETING_PRECISION);
        }
        return null;
    }

}
