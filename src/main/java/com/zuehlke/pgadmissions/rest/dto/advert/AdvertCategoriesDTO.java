package com.zuehlke.pgadmissions.rest.dto.advert;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;

import java.util.List;
import java.util.Map;

public class AdvertCategoriesDTO {

    private List<PrismAdvertIndustry> industries;

    private List<PrismAdvertFunction> functions;

    private List<String> themes;

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

    public List<String> getThemes() {
        return themes;
    }

    public void setThemes(List<String> themes) {
        this.themes = themes;
    }

    public Map<String, List<? extends Object>> getCategories() {
        Map<String, List<? extends Object>> maps = ImmutableMap.of("industries", industries, "functions", functions, "themes", themes);
        return maps;
    }

}
