package com.zuehlke.pgadmissions.rest.representation.address;

import java.math.BigDecimal;

public class CoordinatesRepresentation {

    private BigDecimal latitude;

    private BigDecimal longitude;

    public CoordinatesRepresentation() {
    }

    public CoordinatesRepresentation(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
}
