package com.zuehlke.pgadmissions.dto;

import java.util.Comparator;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;

public class ResourceChildCreationDTO {

    private ResourceParent<?> resource;

    private Boolean partnerMode;

    public ResourceParent<?> getResource() {
        return resource;
    }

    public void setResource(ResourceParent<?> resource) {
        this.resource = resource;
    }

    public Boolean getPartnerMode() {
        return partnerMode;
    }

    public void setPartnerMode(Boolean partnerMode) {
        this.partnerMode = partnerMode;
    }

    public ResourceChildCreationDTO withResource(ResourceParent<?> resource) {
        this.resource = resource;
        return this;
    }

    public ResourceChildCreationDTO withPartnerMode(Boolean partnerMode) {
        this.partnerMode = partnerMode;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(resource.getResourceScope(), resource.getId());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        ResourceChildCreationDTO other = (ResourceChildCreationDTO) object;
        return resource.getResourceScope().equals(other.getResource().getResourceScope()) && resource.getId().equals(other.getResource().getId());
    }

    public static class ResourceChildCreationDTOComparator implements Comparator<ResourceChildCreationDTO> {

        @Override
        public int compare(ResourceChildCreationDTO resource1, ResourceChildCreationDTO resource2) {
            return resource1.getResource().getName().compareTo(resource2.getResource().getName());
        }
    }

}
