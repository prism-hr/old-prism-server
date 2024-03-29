package uk.co.alumeni.prism.utils;

import uk.co.alumeni.prism.PrismConstants;

import java.math.BigDecimal;

public class PrismGeocodingUtils {

    public static BigDecimal getHaversineDistance(BigDecimal bLat, BigDecimal bLon, BigDecimal tLat, BigDecimal tLon) {
        if (!(bLat == null || bLon == null || tLat == null || tLon == null)) {
            double lat1 = bLat.doubleValue();
            double lon1 = bLon.doubleValue();

            double lat2 = tLat.doubleValue();
            double lon2 = tLon.doubleValue();

            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            lat1 = Math.toRadians(lat1);
            lat2 = Math.toRadians(lat2);

            double a = Math.pow(Math.sin(dLat / 2), 2)
                    + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
            double c = 2 * Math.asin(Math.sqrt(a));
            return PrismConversionUtils.doubleToBigDecimal(PrismConstants.EARTH_RADIUS_MILES * c, PrismConstants.GEOCODING_PRECISION);

        }
        return null;
    }

}
