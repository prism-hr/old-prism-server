package uk.co.alumeni.prism.rest.dto.application;

import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.rest.dto.resource.ResourceCreationDTO;

public class ApplicationDTO extends ResourceCreationDTO {

    private PrismOpportunityCategory opportunityCategory;

    public PrismOpportunityCategory getOpportunityCategory() {
        return opportunityCategory;
    }

    public void setOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
    }

}
