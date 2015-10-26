package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;

public class ResourceParentRepresentation extends ResourceRepresentationExtended {

    private String importedCode;

    private List<PrismOpportunityCategory> opportunityCategories;

    @Override
    public String getImportedCode() {
        return importedCode;
    }

    @Override
    public void setImportedCode(String importedCode) {
        this.importedCode = importedCode;
    }

    public List<PrismOpportunityCategory> getOpportunityCategories() {
        return opportunityCategories;
    }

    public void setOpportunityCategories(List<PrismOpportunityCategory> opportunityCategories) {
        this.opportunityCategories = opportunityCategories;
    }

}
