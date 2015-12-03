package uk.co.alumeni.prism.rest.dto.advert;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.PrismAdvertFunction;
import uk.co.alumeni.prism.domain.definitions.PrismAdvertIndustry;
import uk.co.alumeni.prism.rest.dto.TagDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceRelationDTO;

public class AdvertCategoriesDTO {

    private List<PrismAdvertIndustry> industries;

    private List<PrismAdvertFunction> functions;

    private List<TagDTO> themes;

    private List<ResourceRelationDTO> locations;

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

    public List<TagDTO> getThemes() {
        return themes;
    }

    public void setThemes(List<TagDTO> themes) {
        this.themes = themes;
    }

    public List<ResourceRelationDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<ResourceRelationDTO> locations) {
        this.locations = locations;
    }

}
