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
    private Set<AdvertInstitution> institutions = Sets.newHashSet();

    @OrderBy(clause = "value")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertDepartment> departments = Sets.newHashSet();

    @OrderBy(clause = "value")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertSubjectArea> subjectAreas = Sets.newHashSet();

    public Set<AdvertCompetence> getCompetences() {
        return competences;
    }

    public void setCompetences(Set<AdvertCompetence> competences) {
        this.competences = competences;
    }

    public Set<AdvertInstitution> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(Set<AdvertInstitution> institutions) {
        this.institutions = institutions;
    }

    public Set<AdvertDepartment> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<AdvertDepartment> departments) {
        this.departments = departments;
    }

    public Set<AdvertSubjectArea> getSubjectAreas() {
        return subjectAreas;
    }

    public void setSubjectAreas(Set<AdvertSubjectArea> subjectAreas) {
        this.subjectAreas = subjectAreas;
    }

    @Override
    public void storeAttribute(AdvertAttribute<?> value) {
        Class<?> valueClass = value.getClass();
        if (valueClass.equals(AdvertCompetence.class)) {
            competences.add((AdvertCompetence) value);
        } else if (valueClass.equals(AdvertInstitution.class)) {
            institutions.add((AdvertInstitution) value);
        } else if (valueClass.equals(AdvertDepartment.class)) {
            departments.add((AdvertDepartment) value);
        } else {
            subjectAreas.add((AdvertSubjectArea) value);
        }
    }

}
