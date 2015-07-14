package com.zuehlke.pgadmissions.domain.advert;

import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import org.hibernate.annotations.OrderBy;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;

@Embeddable
public class AdvertCategories extends AdvertAttributes {

    @OneToMany(mappedBy = "advert")
    @OrderBy(clause = "value")
    private Set<AdvertIndustry> industries = Sets.newHashSet();

    @OneToMany(mappedBy = "advert")
    @OrderBy(clause = "value")
    private Set<AdvertFunction> functions = Sets.newHashSet();

    @OrderBy(clause = "value")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertTheme> themes = Sets.newHashSet();

    public Set<AdvertIndustry> getIndustries() {
        return industries;
    }

    public void setIndustries(Set<AdvertIndustry> industries) {
        this.industries = industries;
    }

    public Set<AdvertFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(Set<AdvertFunction> functions) {
        this.functions = functions;
    }

    public Set<AdvertTheme> getThemes() {
        return themes;
    }

    public void setThemes(Set<AdvertTheme> themes) {
        this.themes = themes;
    }

    @Override
    public void clearAttributes(Object value) {
        Class<?> valueClass = value.getClass();
        if (valueClass.equals(PrismAdvertIndustry.class)) {
            industries.clear();
        } else if (valueClass.equals(PrismAdvertFunction.class)) {
            functions.clear();
        } else {
            themes.clear();
        }
    }

    @Override
    public void storeAttribute(AdvertAttribute<?> attribute) {
        Class<?> valueClass = attribute.getValue().getClass();
        if (valueClass.equals(PrismAdvertIndustry.class)) {
            industries.add((AdvertIndustry) attribute);
        } else if (valueClass.equals(PrismAdvertFunction.class)) {
            functions.add((AdvertFunction) attribute);
        } else {
            themes.add((AdvertTheme) attribute);
        }
    }

}
