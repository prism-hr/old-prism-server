package com.zuehlke.pgadmissions.dto;

public class InstitutionDTOSql extends InstitutionDTO<Double> {

    private Double addressCoordinateLatitude;

    private Double addressCoordinateLongitude;

    private Double targetingRelevance;

    private Double targetingDistance;

    @Override
    public Double getAddressCoordinateLatitude() {
        return addressCoordinateLatitude;
    }

    @Override
    public void setAddressCoordinateLatitude(Double addressCoordinateLatitude) {
        this.addressCoordinateLatitude = addressCoordinateLatitude;
    }

    @Override
    public Double getAddressCoordinateLongitude() {
        return addressCoordinateLongitude;
    }

    @Override
    public void setAddressCoordinateLongitude(Double addressCoordinateLongitude) {
        this.addressCoordinateLongitude = addressCoordinateLongitude;
    }

    @Override
    public Double getTargetingRelevance() {
        return targetingRelevance;
    }

    @Override
    public void setTargetingRelevance(Double targetingRelevance) {
        this.targetingRelevance = targetingRelevance;
    }

    @Override
    public Double getTargetingDistance() {
        return targetingDistance;
    }

    @Override
    public void setTargetingDistance(Double targetingDistance) {
        this.targetingDistance = targetingDistance;
    }

}
