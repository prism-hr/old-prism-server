package uk.co.alumeni.prism.rest.representation.advert;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.PrismAdvertFunction;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertIndustry;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRelation;

public class AdvertCategoriesRepresentation {

    private List<PrismAdvertIndustry> industries;

    private List<PrismAdvertFunction> functions;

    private List<AdvertThemeRepresentation> themes;

    private List<ResourceRepresentationRelation> locations;

    private List<ResourceRepresentationRelation> possibleLocations;

    public List<PrismAdvertIndustry> getIndustries() {
        return industries;
    }

    public void setIndustries(List<PrismAdvertIndustry> industries) {
        this.industries = industries;
    }

    public List<PrismAdvertFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<PrismAdvertFunction> functions) {
        this.functions = functions;
    }

    public List<AdvertThemeRepresentation> getThemes() {
        return themes;
    }

    public void setThemes(List<AdvertThemeRepresentation> themes) {
        this.themes = themes;
    }

    public List<ResourceRepresentationRelation> getLocations() {
        return locations;
    }

    public void setLocations(List<ResourceRepresentationRelation> locations) {
        this.locations = locations;
    }

    public List<ResourceRepresentationRelation> getPossibleLocations() {
        return possibleLocations;
    }

    public void setPossibleLocations(List<ResourceRepresentationRelation> possibleLocations) {
        this.possibleLocations = possibleLocations;
    }

    public AdvertCategoriesRepresentation withIndustries(List<PrismAdvertIndustry> industries) {
        this.industries = industries;
        return this;
    }

    public AdvertCategoriesRepresentation withFunctions(List<PrismAdvertFunction> functions) {
        this.functions = functions;
        return this;
    }

    public AdvertCategoriesRepresentation withThemes(List<AdvertThemeRepresentation> themes) {
        this.themes = themes;
        return this;
    }

    public AdvertCategoriesRepresentation withLocations(List<ResourceRepresentationRelation> locations) {
        this.locations = locations;
        return this;
    }

    public AdvertCategoriesRepresentation withPossibleLocations(List<ResourceRepresentationRelation> possibleLocations) {
        this.possibleLocations = possibleLocations;
        return this;
    }

}
