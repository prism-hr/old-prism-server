package com.zuehlke.pgadmissions.dto.resource;

import java.math.BigDecimal;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

import jersey.repackaged.com.google.common.collect.Sets;

public class ResourceTargetDTO extends ResourceStandardDTO {

    private String addressDomicileName;

    private String addressLine1;

    private String addressLine2;

    private String addressTown;

    private String addressRegion;

    private String addressCode;

    private String addressGoogleId;

    private BigDecimal addressCoordinateLatitude;

    private BigDecimal addressCoordinateLongitude;

    private Boolean selected;

    private BigDecimal rating;

    private BigDecimal targetingRelevance;

    private BigDecimal targetingDistance;

    private Set<ResourceTargetDTO> departments = Sets.newTreeSet();

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

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal ratingAverage) {
        this.rating = ratingAverage;
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

    public Set<ResourceTargetDTO> getDepartments() {
        return departments;
    }

    public void addDepartment(ResourceTargetDTO department) {
        this.departments.add(department);
    }

    public ResourceTargetDTO withId(PrismScope scope, Integer id) {
        setId(scope, id);
        return this;
    }

    @Override
    public ResourceTargetDTO getParentResource() {
        return super.getParentResource(ResourceTargetDTO.class);
    }

    @Override
    public ResourceTargetDTO getEnclosingResource(PrismScope scope) {
        return super.getEnclosingResource(scope, ResourceTargetDTO.class);
    }

    @Override
    public int compareTo(Object object) {
        ResourceTargetDTO other = (ResourceTargetDTO) object;
        int relevanceComparison = ObjectUtils.compare(other.targetingRelevance, targetingRelevance);
        return relevanceComparison == 0 ? super.compareTo(other) : relevanceComparison;
    }

}
