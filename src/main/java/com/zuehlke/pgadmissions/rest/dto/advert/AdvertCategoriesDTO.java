package com.zuehlke.pgadmissions.rest.dto.advert;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;

public class AdvertCategoriesDTO extends AdvertAttributesDTO<Object> {

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
    
    @Override
    public List<Object> getAttributes() {
        List<Object> attributes = Lists.newLinkedList();
        attributes.addAll(industries);
        attributes.addAll(functions);
        attributes.addAll(themes);
        return attributes;
    }

}
