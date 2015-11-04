package com.zuehlke.pgadmissions.dto.json;

import java.math.BigDecimal;

public class GoogleResultDTO {

    private GoogleGeometryDTO geometry;

    public GoogleGeometryDTO getGeometry() {
        return geometry;
    }

    public void setGeometry(GoogleGeometryDTO geometry) {
        this.geometry = geometry;
    }

    public static class GoogleGeometryDTO {

        private Location location;

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public static class Location {

            private BigDecimal lat;

            private BigDecimal lng;

            public BigDecimal getLat() {
                return lat;
            }

            public void setLat(BigDecimal lat) {
                this.lat = lat;
            }

            public BigDecimal getLng() {
                return lng;
            }

            public void setLng(BigDecimal lng) {
                this.lng = lng;
            }

        }

    }

}
