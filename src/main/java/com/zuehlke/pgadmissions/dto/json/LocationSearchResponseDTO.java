package com.zuehlke.pgadmissions.dto.json;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationSearchResponseDTO {

    private List<Results> results;
    
    private String status;
    
    public final List<Results> getResults() {
        return results;
    }

    public final void setResults(List<Results> results) {
        this.results = results;
    }

    public final String getStatus() {
        return status;
    }

    public final void setStatus(String status) {
        this.status = status;
    }

    public static class Results {
        
        @JsonProperty("place_id")        
        private String placeId;
        
        private Geometry geometry;
        
        public String getPlaceId() {
            return placeId;
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }

        public final Geometry getGeometry() {
            return geometry;
        }

        public final void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }
        
        public static class Geometry {
            
            private Location location;
            
            @JsonProperty("location_type")
            private String locationType;

            @JsonProperty("viewport")
            private Region viewPort;

            public final Location getLocation() {
                return location;
            }

            public final void setLocation(Location location) {
                this.location = location;
            }

            public final String getLocationType() {
                return locationType;
            }


            public final void setLocationType(String locationType) {
                this.locationType = locationType;
            }

            public final Region getViewPort() {
                return viewPort;
            }

            public final void setViewPort(Region viewPort) {
                this.viewPort = viewPort;
            }

            public static class Location {
                
                private BigDecimal lat;
                
                private BigDecimal lng;

                public final BigDecimal getLat() {
                    return lat;
                }

                public final void setLat(BigDecimal lat) {
                    this.lat = lat;
                }

                public final BigDecimal getLng() {
                    return lng;
                }

                public final void setLng(BigDecimal lng) {
                    this.lng = lng;
                }
                
            }
            
            public static class Region {
              
                @JsonProperty("northeast")
                private Location northEast;
                
                @JsonProperty("southwest")
                private Location southWest;

                public final Location getNorthEast() {
                    return northEast;
                }

                public final void setNorthEast(Location northEast) {
                    this.northEast = northEast;
                }

                public final Location getSouthWest() {
                    return southWest;
                }

                public final void setSouthWest(Location southWest) {
                    this.southWest = southWest;
                }
                
            }
            
        }
        
    }
    
}
