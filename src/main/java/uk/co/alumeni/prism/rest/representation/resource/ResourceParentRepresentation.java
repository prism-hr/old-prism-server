package uk.co.alumeni.prism.rest.representation.resource;

import java.util.List;
import java.util.Set;

import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.domain.definitions.PrismResourceContext;
import uk.co.alumeni.prism.rest.representation.advert.AdvertThemeRepresentation;

public class ResourceParentRepresentation extends ResourceRepresentationExtended {

    private String importedCode;

    private List<PrismOpportunityCategory> opportunityCategories;

    private Set<PrismResourceContext> contexts;

    private List<AdvertThemeRepresentation> suggestedThemes;

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

    public Set<PrismResourceContext> getContexts() {
        return contexts;
    }

    public void setContexts(Set<PrismResourceContext> contexts) {
        this.contexts = contexts;
    }

    public List<AdvertThemeRepresentation> getSuggestedThemes() {
        return suggestedThemes;
    }

    public void setSuggestedThemes(List<AdvertThemeRepresentation> suggestedThemes) {
        this.suggestedThemes = suggestedThemes;
    }
}
