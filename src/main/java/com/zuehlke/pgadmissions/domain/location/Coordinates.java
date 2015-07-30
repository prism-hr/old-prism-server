package com.zuehlke.pgadmissions.domain.location;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Coordinates {

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

    public Coordinates withLatitude(BigDecimal latitude) {
        this.latitude = latitude;
        return this;
    }

    public Coordinates withLongitude(BigDecimal longitude) {
        this.longitude = longitude;
        return this;
    }

}
