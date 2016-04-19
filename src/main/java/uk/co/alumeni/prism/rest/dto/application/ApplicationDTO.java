package uk.co.alumeni.prism.rest.dto.application;

import javax.validation.Valid;

import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.rest.dto.resource.ResourceCreationDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceDTO;

public class ApplicationDTO extends ResourceCreationDTO {

    @Valid
    private ResourceDTO parentResource;

    private PrismOpportunityCategory opportunityCategory;

    @Override
    public ResourceDTO getParentResource() {
        return parentResource;
    }

    @Override
    public void setParentResource(ResourceDTO parentResource) {
        this.parentResource = parentResource;
    }

    public PrismOpportunityCategory getOpportunityCategory() {
        return opportunityCategory;
    }

    public void setOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
    }

}
