package com.zuehlke.pgadmissions.domain.location;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class GeographicLocation {

    @Column(name = "location_x")
    private BigDecimal locationX;

    @Column(name = "location_y")
    private BigDecimal locationY;

    public final BigDecimal getLocationX() {
        return locationX;
    }

    public final void setLocationX(BigDecimal locationX) {
        this.locationX = locationX;
    }

    public final BigDecimal getLocationY() {
        return locationY;
    }

    public final void setLocationY(BigDecimal locationY) {
        this.locationY = locationY;
    }

    public GeographicLocation withLocationX(BigDecimal locationX) {
        this.locationX = locationX;
        return this;
    }

    public GeographicLocation withLocationY(BigDecimal locationY) {
        this.locationY = locationY;
        return this;
    }

}
