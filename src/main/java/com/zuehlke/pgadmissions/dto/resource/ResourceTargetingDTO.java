package com.zuehlke.pgadmissions.dto.resource;

import java.math.BigDecimal;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

import jersey.repackaged.com.google.common.collect.Sets;

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

    private Integer selectedId;

    private Boolean endorsed;

    private BigDecimal targetingRelevance;
    
    private BigDecimal targetingDistance;
   
    private Set<ResourceTargetingDTO> departments = Sets.newTreeSet();

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

    public Integer getSelectedId() {
        return selectedId;
    }

    public void setSelectedId(Integer selectedId) {
        this.selectedId = selectedId;
    }

    public Boolean getEndorsed() {
        return endorsed;
    }

    public void setEndorsed(Boolean endorsed) {
        this.endorsed = endorsed;
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

    public Set<ResourceTargetingDTO> getDepartments() {
        return departments;
    }

    public void addDepartment(ResourceTargetingDTO department) {
        this.departments.add(department);
    }

    public Boolean getSelected() {
        return selectedId != null;
    }
    
    public ResourceTargetingDTO withId(PrismScope scope, Integer id) {
        setId(scope, id);
        return this;
    }
    
    @Override
    public ResourceTargetingDTO getParentResource() {
        return super.getParentResource(ResourceTargetingDTO.class);
    }
    
    @Override
    public ResourceTargetingDTO getEnclosingResource(PrismScope scope) {
        return super.getEnclosingResource(scope, ResourceTargetingDTO.class);
    }

    @Override
    public int compareTo(Object object) {
        if (object.getClass().equals(ResourceTargetingDTO.class)) {
            ResourceTargetingDTO other = (ResourceTargetingDTO) object;
            
            if (compareToWithoutRelevance(other) == 0) {
                return 0;
            }
            
            int relevanceComparison = ObjectUtils.compare(other.getTargetingRelevance(), targetingRelevance);
            if (relevanceComparison == 0) {
                return compareToWithoutRelevance(other);
            }
            return relevanceComparison;
        }
        return 0;
    }

    private int compareToWithoutRelevance(ResourceTargetingDTO other) {
        int nameComparison = ObjectUtils.compare(getName(), other.getName());
        return (nameComparison == 0) ? ObjectUtils.compare(getId(), other.getId()) : nameComparison;
    }

}
