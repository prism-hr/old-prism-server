package com.zuehlke.pgadmissions.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class GeographicLocation {

    @Column(name = "location_x")
    private BigDecimal locationX;
    
    @Column(name = "location_y")
    private BigDecimal locationY;
    
    @Column(name = "location_view_ne_x")
    private BigDecimal locationViewNeX;
    
    @Column(name = "location_view_ne_y")
    private BigDecimal locationViewNeY;
    
    @Column(name = "location_view_sw_x")
    private BigDecimal locationViewSwX;
    
    @Column(name = "location_view_sw_y")
    private BigDecimal locationViewSwY;

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

    public final BigDecimal getLocationViewNeX() {
        return locationViewNeX;
    }

    public final void setLocationViewNeX(BigDecimal locationViewNeX) {
        this.locationViewNeX = locationViewNeX;
    }

    public final BigDecimal getLocationViewNeY() {
        return locationViewNeY;
    }

    public final void setLocationViewNeY(BigDecimal locationViewNeY) {
        this.locationViewNeY = locationViewNeY;
    }

    public final BigDecimal getLocationViewSwX() {
        return locationViewSwX;
    }

    public final void setLocationViewSwX(BigDecimal locationViewSwX) {
        this.locationViewSwX = locationViewSwX;
    }

    public final BigDecimal getLocationViewSwY() {
        return locationViewSwY;
    }

    public final void setLocationViewSwY(BigDecimal locationViewSwY) {
        this.locationViewSwY = locationViewSwY;
    }
    
    public GeographicLocation withLocationX(BigDecimal locationX) {
        this.locationX = locationX;
        return this;
    }
    
    public GeographicLocation withLocationY(BigDecimal locationY) {
        this.locationY = locationY;
        return this;
    }
    
    public GeographicLocation withLocationViewNeX(BigDecimal locationViewNeX) {
        this.locationViewNeX = locationViewNeX;
        return this;
    }
    
    public GeographicLocation withLocationViewNeY(BigDecimal locationViewNeY) {
        this.locationViewNeY = locationViewNeY;
        return this;
    }
    
    public GeographicLocation withLocationViewSwX(BigDecimal locationViewSwX) {
        this.locationViewSwX = locationViewSwX;
        return this;
    }
    
    public GeographicLocation withLocationViewSwY(BigDecimal locationViewSwY) {
        this.locationViewSwY = locationViewSwY;
        return this;
    }
    
}
