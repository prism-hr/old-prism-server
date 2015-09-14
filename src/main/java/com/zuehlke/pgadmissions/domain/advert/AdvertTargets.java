package com.zuehlke.pgadmissions.domain.advert;

import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import org.hibernate.annotations.OrderBy;

import com.google.common.collect.Sets;

@Embeddable
public class AdvertTargets implements AdvertAttributes {

    @OrderBy(clause = "id")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertCompetence> competences = Sets.newHashSet();

    @OrderBy(clause = "id")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertTargetAdvert> adverts = Sets.newHashSet();

    public Set<AdvertCompetence> getCompetences() {
        return competences;
    }

    public void setCompetences(Set<AdvertCompetence> competences) {
        this.competences = competences;
    }

    public Set<AdvertTargetAdvert> getAdverts() {
        return adverts;
    }

    public void setAdverts(Set<AdvertTargetAdvert> adverts) {
        this.adverts = adverts;
    }

    @Override
    public void storeAttribute(AdvertAttribute<?> value) {
        Class<?> valueClass = value.getClass();
        if (valueClass.equals(AdvertCompetence.class)) {
            competences.add((AdvertCompetence) value);
        } else if (valueClass.equals(AdvertTargetAdvert.class)) {
            adverts.add((AdvertTargetAdvert) value);
        }
    }

}
