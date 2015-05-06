package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;

public class ResourceParentDTO extends ResourceDTO {

    @NotNull
    private AdvertDTO advert;

    private LocalDate endDate;

    private ResourceParentAttributesDTO attributes;

    public AdvertDTO getAdvert() {
        return advert;
    }

    public void setAdvert(AdvertDTO advert) {
        this.advert = advert;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public ResourceParentAttributesDTO getAttributes() {
        return attributes;
    }

    public void setAttributes(ResourceParentAttributesDTO attributes) {
        this.attributes = attributes;
    }

    public static class ResourceParentAttributesDTO {

        private List<ResourceConditionDTO> resourceConditions;

        private List<String> studyLocations;

        public List<ResourceConditionDTO> getResourceConditions() {
            return resourceConditions;
        }

        public void setResourceConditions(List<ResourceConditionDTO> resourceConditions) {
            this.resourceConditions = resourceConditions;
        }

        public List<String> getStudyLocations() {
            return studyLocations;
        }

        public void setStudyLocations(List<String> studyLocations) {
            this.studyLocations = studyLocations;
        }

    }

    public static class ResourceConditionDTO {

        private PrismActionCondition actionCondition;

        private Boolean partnerMode;

        public PrismActionCondition getActionCondition() {
            return actionCondition;
        }

        public void setActionCondition(PrismActionCondition actionCondition) {
            this.actionCondition = actionCondition;
        }

        public Boolean getPartnerMode() {
            return partnerMode;
        }

        public void setPartnerMode(Boolean partnerMode) {
            this.partnerMode = partnerMode;
        }
    }

}