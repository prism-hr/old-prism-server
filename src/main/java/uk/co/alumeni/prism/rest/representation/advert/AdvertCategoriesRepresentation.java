package uk.co.alumeni.prism.rest.representation.advert;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.PrismAdvertFunction;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertIndustry;
import uk.co.alumeni.prism.rest.representation.resource.ResourceLocationRepresentationRelation;

public class AdvertCategoriesRepresentation {

    private List<PrismAdvertIndustry> industries;

    private List<PrismAdvertFunction> functions;

    private List<AdvertThemeRepresentation> themes;

    private List<ResourceLocationRepresentationRelation> locations;

    private List<ResourceLocationRepresentationRelation> possibleLocations;

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

    public List<ResourceLocationRepresentationRelation> getLocations() {
        return locations;
    }

    public void setLocations(List<ResourceLocationRepresentationRelation> locations) {
        this.locations = locations;
    }

    public List<ResourceLocationRepresentationRelation> getPossibleLocations() {
        return possibleLocations;
    }

    public void setPossibleLocations(List<ResourceLocationRepresentationRelation> possibleLocations) {
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

    public AdvertCategoriesRepresentation withLocations(List<ResourceLocationRepresentationRelation> locations) {
        this.locations = locations;
        return this;
    }

    public AdvertCategoriesRepresentation withPossibleLocations(List<ResourceLocationRepresentationRelation> possibleLocations) {
        this.possibleLocations = possibleLocations;
        return this;
    }

}
