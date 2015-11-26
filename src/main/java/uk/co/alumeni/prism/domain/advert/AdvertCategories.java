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

}
