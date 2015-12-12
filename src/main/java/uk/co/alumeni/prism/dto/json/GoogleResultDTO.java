package uk.co.alumeni.prism.dto.json;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GoogleResultDTO {

    @JsonProperty("address_components")
    private List<GoogleAddressComponentDTO> components;

    private GoogleGeometryDTO geometry;

    public List<GoogleAddressComponentDTO> getComponents() {
        return components;
    }

    public void setComponents(List<GoogleAddressComponentDTO> components) {
        this.components = components;
    }

    public GoogleGeometryDTO getGeometry() {
        return geometry;
    }

    public void setGeometry(GoogleGeometryDTO geometry) {
        this.geometry = geometry;
    }

    public static class GoogleAddressComponentDTO {

        @JsonProperty("long_name")
        private String name;

        private List<String> types;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getTypes() {
            return types;
        }

        public void setTypes(List<String> types) {
            this.types = types;
        }

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
