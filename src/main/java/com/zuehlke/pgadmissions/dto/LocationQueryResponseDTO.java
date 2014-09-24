package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationQueryResponseDTO {

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
        
        @JsonProperty("address_components")
        private List<AddressComponent> addressComponents;
        
        @JsonProperty("formatted_address")
        private String formattedAddress;
        
        @JsonProperty("postcode_localities")
        private List<String> postcodeLocalities;
        
        private Geometry geometry;

        @JsonProperty("partial_match")
        private String partialMatch;
        
        private List<String> types;
        
        public final List<AddressComponent> getAddressComponents() {
            return addressComponents;
        }

        public final void setAddressComponents(List<AddressComponent> addressComponents) {
            this.addressComponents = addressComponents;
        }
        
        public final List<String> getPostcodeLocalities() {
            return postcodeLocalities;
        }

        public final void setPostcodeLocalities(List<String> postcodeLocalities) {
            this.postcodeLocalities = postcodeLocalities;
        }

        public final String getFormattedAddress() {
            return formattedAddress;
        }

        public final void setFormattedAddress(String formattedAddress) {
            this.formattedAddress = formattedAddress;
        }

        public final Geometry getGeometry() {
            return geometry;
        }

        public final void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }
        
        public final String getPartialMatch() {
            return partialMatch;
        }

        public final void setPartialMatch(String partialMatch) {
            this.partialMatch = partialMatch;
        }

        public final List<String> getTypes() {
            return types;
        }

        public final void setTypes(List<String> types) {
            this.types = types;
        }

        public static class AddressComponent {
           
            @JsonProperty("long_name")
            private String longName;
            
            @JsonProperty("short_name")
            private String shortName;
            
            private List<String> types;
            
            public final String getLongName() {
                return longName;
            }

            public final void setLongName(String longName) {
                this.longName = longName;
            }

            public final String getShortName() {
                return shortName;
            }

            public final void setShortName(String shortName) {
                this.shortName = shortName;
            }

            public final List<String> getTypes() {
                return types;
            }

            public final void setTypes(List<String> types) {
                this.types = types;
            }
            
        }
        
        public static class Geometry {
            
            private Region bounds;
            
            private Location location;
            
            @JsonProperty("location_type")
            private String locationType;

            @JsonProperty("viewport")
            private Region viewPort;
            
            public final Region getBounds() {
                return bounds;
            }

            public final void setBounds(Region bounds) {
                this.bounds = bounds;
            }

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
