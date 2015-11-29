package uk.co.alumeni.prism.rest.representation.advert;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.PrismAdvertFunction;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertIndustry;

public class AdvertCategoriesRepresentation {

    private List<PrismAdvertIndustry> industries;

    private List<PrismAdvertFunction> functions;
    
    private List<AdvertThemeRepresentation> themes;

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
    
}
