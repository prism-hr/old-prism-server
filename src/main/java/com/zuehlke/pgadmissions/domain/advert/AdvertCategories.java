package com.zuehlke.pgadmissions.domain.advert;

import com.google.common.collect.Sets;
import org.hibernate.annotations.OrderBy;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.Set;

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
