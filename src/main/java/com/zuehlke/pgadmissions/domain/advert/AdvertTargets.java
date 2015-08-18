package com.zuehlke.pgadmissions.domain.advert;

import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import org.hibernate.annotations.OrderBy;

import com.google.common.collect.Sets;

@Embeddable
public class AdvertTargets implements AdvertAttributes {

    @OrderBy(clause = "value")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertCompetence> competences = Sets.newHashSet();

    @OrderBy(clause = "value")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertSubjectArea> subjectAreas = Sets.newHashSet();

    @OrderBy(clause = "institution, department")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertResource> resources = Sets.newHashSet();

    @OrderBy(clause = "institution, department")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertResourceSelected> selectedResources = Sets.newHashSet();

    public Set<AdvertCompetence> getCompetences() {
        return competences;
    }

    public void setCompetences(Set<AdvertCompetence> competences) {
        this.competences = competences;
    }

    public Set<AdvertSubjectArea> getSubjectAreas() {
        return subjectAreas;
    }

    public void setSubjectAreas(Set<AdvertSubjectArea> subjectAreas) {
        this.subjectAreas = subjectAreas;
    }

    public Set<AdvertResource> getResources() {
        return resources;
    }

    public void setResources(Set<AdvertResource> resources) {
        this.resources = resources;
    }

    public Set<AdvertResourceSelected> getSelectedResources() {
        return selectedResources;
    }

    public void setSelectedResources(Set<AdvertResourceSelected> selectedResources) {
        this.selectedResources = selectedResources;
    }

    @Override
    public void storeAttribute(AdvertAttribute<?> value) {
        Class<?> valueClass = value.getClass();
        if (valueClass.equals(AdvertCompetence.class)) {
            competences.add((AdvertCompetence) value);
        } else if (valueClass.equals(AdvertResource.class)) {
            resources.add((AdvertResource) value);
        } else if (valueClass.equals(AdvertResourceSelected.class)) {
            selectedResources.add((AdvertResourceSelected) value);
        } else {
            subjectAreas.add((AdvertSubjectArea) value);
        }
    }

}
