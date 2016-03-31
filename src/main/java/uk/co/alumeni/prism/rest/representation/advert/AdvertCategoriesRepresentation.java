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

    private List<String> themesDisplay;

    private List<String> locationsDisplay;

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

    public List<String> getThemesDisplay() {
        return themesDisplay;
    }

    public void setThemesDisplay(List<String> themesDisplay) {
        this.themesDisplay = themesDisplay;
    }

    public List<String> getLocationsDisplay() {
        return locationsDisplay;
    }

    public void setLocationsDisplay(List<String> locationsDisplay) {
        this.locationsDisplay = locationsDisplay;
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

    public AdvertCategoriesRepresentation withThemesDisplay(List<String> themesDisplay) {
        this.themesDisplay = themesDisplay;
        return this;
    }

    public AdvertCategoriesRepresentation withLocationsDisplay(List<String> locationsDisplay) {
        this.locationsDisplay = locationsDisplay;
        return this;
    }

}
