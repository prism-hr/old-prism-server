package uk.co.alumeni.prism.domain.advert;

import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import org.hibernate.annotations.OrderBy;

import com.google.common.collect.Sets;

@Embeddable
public class AdvertCategories {

    @OneToMany(mappedBy = "advert")
    @OrderBy(clause = "industry")
    private Set<AdvertIndustry> industries = Sets.newHashSet();

    @OneToMany(mappedBy = "advert")
    @OrderBy(clause = "function")
    private Set<AdvertFunction> functions = Sets.newHashSet();

    @OneToMany(mappedBy = "advert")
    @OrderBy(clause = "theme_id")
    private Set<AdvertTheme> themes = Sets.newHashSet();

    @OneToMany(mappedBy = "advert")
    @OrderBy(clause = "location_advert_id")
    private Set<AdvertLocation> locations = Sets.newHashSet();

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

    public Set<AdvertLocation> getLocations() {
        return locations;
    }

    public void setLocations(Set<AdvertLocation> locations) {
        this.locations = locations;
    }

}
