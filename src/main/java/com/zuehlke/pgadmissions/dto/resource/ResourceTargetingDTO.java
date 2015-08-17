package com.zuehlke.pgadmissions.dto.resource;

import java.math.BigDecimal;

public class ResourceTargetingDTO extends ResourceStandardDTO {

    private String addressDomicileName;

    private String addressLine1;

    private String addressLine2;

    private String addressTown;

    private String addressRegion;

    private String addressCode;

    private String addressGoogleId;

    private BigDecimal addressCoordinateLatitude;

    private BigDecimal addressCoordinateLongitude;

    private BigDecimal targetingRelevance;

    private BigDecimal targetingDistance;

    public String getAddressDomicileName() {
        return addressDomicileName;
    }

    public void setAddressDomicileName(String addressDomicileName) {
        this.addressDomicileName = addressDomicileName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressTown() {
        return addressTown;
    }

    public void setAddressTown(String addressTown) {
        this.addressTown = addressTown;
    }

    public String getAddressRegion() {
        return addressRegion;
    }

    public void setAddressRegion(String addressRegion) {
        this.addressRegion = addressRegion;
    }

    public String getAddressCode() {
        return addressCode;
    }

    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    public String getAddressGoogleId() {
        return addressGoogleId;
    }

    public void setAddressGoogleId(String addressGoogleId) {
        this.addressGoogleId = addressGoogleId;
    }

    public BigDecimal getAddressCoordinateLatitude() {
        return addressCoordinateLatitude;
    }

    public void setAddressCoordinateLatitude(BigDecimal addressCoordinateLatitude) {
        this.addressCoordinateLatitude = addressCoordinateLatitude;
    }

    public BigDecimal getAddressCoordinateLongitude() {
        return addressCoordinateLongitude;
    }

    public void setAddressCoordinateLongitude(BigDecimal addressCoordinateLongitude) {
        this.addressCoordinateLongitude = addressCoordinateLongitude;
    }

    public BigDecimal getTargetingRelevance() {
        return targetingRelevance;
    }

    public void setTargetingRelevance(BigDecimal targetingRelevance) {
        this.targetingRelevance = targetingRelevance;
    }

    public BigDecimal getTargetingDistance() {
        return targetingDistance;
    }

    public void setTargetingDistance(BigDecimal targetingDistance) {
        this.targetingDistance = targetingDistance;
    }

}
