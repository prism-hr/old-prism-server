package com.zuehlke.pgadmissions.domain.location;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AddressCoordinates {

    @Column(name = "location_x")
    private BigDecimal latitude;

    @Column(name = "location_y")
    private BigDecimal longitude;

    public final BigDecimal getLatitude() {
        return latitude;
    }

    public final void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public final BigDecimal getLongitude() {
        return longitude;
    }

    public final void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public AddressCoordinates withLatitude(BigDecimal latitude) {
        this.latitude = latitude;
        return this;
    }

    public AddressCoordinates withLongitude(BigDecimal longitude) {
        this.longitude = longitude;
        return this;
    }

}
