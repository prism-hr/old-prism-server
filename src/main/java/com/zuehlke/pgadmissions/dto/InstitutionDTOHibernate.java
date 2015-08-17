package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;

public class InstitutionDTOHibernate extends InstitutionDTO<BigDecimal> {

    private BigDecimal addressCoordinateLatitude;

    private BigDecimal addressCoordinateLongitude;

    private BigDecimal targetingRelevance;

    private BigDecimal targetingDistance;

    @Override
    public BigDecimal getAddressCoordinateLatitude() {
        return addressCoordinateLatitude;
    }

    @Override
    public void setAddressCoordinateLatitude(BigDecimal addressCoordinateLatitude) {
        this.addressCoordinateLatitude = addressCoordinateLatitude;
    }

    @Override
    public BigDecimal getAddressCoordinateLongitude() {
        return addressCoordinateLongitude;
    }

    @Override
    public void setAddressCoordinateLongitude(BigDecimal addressCoordinateLongitude) {
        this.addressCoordinateLongitude = addressCoordinateLongitude;
    }

    @Override
    public BigDecimal getTargetingRelevance() {
        return targetingRelevance;
    }

    @Override
    public void setTargetingRelevance(BigDecimal targetingRelevance) {
        this.targetingRelevance = targetingRelevance;
    }

    @Override
    public BigDecimal getTargetingDistance() {
        return targetingDistance;
    }

    @Override
    public void setTargetingDistance(BigDecimal targetingDistance) {
        this.targetingDistance = targetingDistance;
    }

}
