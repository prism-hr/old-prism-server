package com.zuehlke.pgadmissions.dto.json;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationSearchResponseDTO {

    private List<Results> results;
    
    private String status;
    
    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Results {
        
        @JsonProperty("place_id")
        private String googleId;
        
        private Geometry geometry;

        public String getGoogleId() {
            return googleId;
        }

        public void setGoogleId(String googleId) {
            this.googleId = googleId;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }
        
        public static class Geometry {
            
            private Location location;
            
            @JsonProperty("location_type")
            private String locationType;

            @JsonProperty("viewport")
            private Region viewPort;

            public Location getLocation() {
                return location;
            }

            public void setLocation(Location location) {
                this.location = location;
            }

            public String getLocationType() {
                return locationType;
            }


            public void setLocationType(String locationType) {
                this.locationType = locationType;
            }

            public Region getViewPort() {
                return viewPort;
            }

            public void setViewPort(Region viewPort) {
                this.viewPort = viewPort;
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
            
            public static class Region {
              
                @JsonProperty("northeast")
                private Location northEast;
                
                @JsonProperty("southwest")
                private Location southWest;

                public Location getNorthEast() {
                    return northEast;
                }

                public void setNorthEast(Location northEast) {
                    this.northEast = northEast;
                }

                public Location getSouthWest() {
                    return southWest;
                }

                public void setSouthWest(Location southWest) {
                    this.southWest = southWest;
                }
                
            }
            
        }
        
    }
    
}
