package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;

public class ResourceParentDTO extends ResourceDTO {

    @Valid
    @NotNull
    private AdvertDTO advert;

    private List<ResourceConditionDTO> resourceConditions;

    public AdvertDTO getAdvert() {
        return advert;
    }

    public void setAdvert(AdvertDTO advert) {
        this.advert = advert;
    }
    
    public List<ResourceConditionDTO> getResourceConditions() {
        return resourceConditions;
    }

    public void setResourceConditions(List<ResourceConditionDTO> resourceConditions) {
        this.resourceConditions = resourceConditions;
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
