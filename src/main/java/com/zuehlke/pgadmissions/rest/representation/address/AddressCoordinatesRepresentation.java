package com.zuehlke.pgadmissions.rest.representation.address;

import java.math.BigDecimal;

public class AddressCoordinatesRepresentation {

    private BigDecimal latitude;

    private BigDecimal longitude;

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

    public AddressCoordinatesRepresentation withLatitude(BigDecimal latitude) {
        this.latitude = latitude;
        return this;
    }

    public AddressCoordinatesRepresentation withLongitude(BigDecimal longitude) {
        this.longitude = longitude;
        return this;
    }

}
