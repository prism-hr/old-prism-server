package com.zuehlke.pgadmissions.rest.dto.resource;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import uk.co.alumeni.prism.api.model.resource.ResourceParentDefinition;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;

public class ResourceParentDTO implements ResourceCreationDTO, ResourceParentDefinition<AdvertDTO> {

    private ResourceDTO parentResource;

    private Integer workflowPropertyConfigurationVersion;

    @Valid
    @NotNull
    private AdvertDTO advert;

    private List<ResourceConditionDTO> conditions;

    @Override
    public ResourceDTO getParentResource() {
        return parentResource;
    }

    @Override
    public void setParentResource(ResourceDTO parentResource) {
        this.parentResource = parentResource;
    }

    @Override
    public Integer getWorkflowPropertyConfigurationVersion() {
        return workflowPropertyConfigurationVersion;
    }

    @Override
    public void setWorkflowPropertyConfigurationVersion(Integer workflowPropertyConfigurationVersion) {
        this.workflowPropertyConfigurationVersion = workflowPropertyConfigurationVersion;
    }

    @Override
    public AdvertDTO getAdvert() {
        return advert;
    }

    @Override
    public void setAdvert(AdvertDTO advert) {
        this.advert = advert;
    }

    public List<ResourceConditionDTO> getConditions() {
        return conditions;
    }

    public void setConditions(List<ResourceConditionDTO> conditions) {
        this.conditions = conditions;
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
