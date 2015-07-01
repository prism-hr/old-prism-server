package com.zuehlke.pgadmissions.domain.advert;

import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import org.hibernate.annotations.OrderBy;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.Competence;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.resource.Department;

@Embeddable
public class AdvertTargets extends AdvertAttributes {

    @OrderBy(clause = "competence")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertCompetence> competences = Sets.newHashSet();

    @OrderBy(clause = "institution")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertInstitution> institutions = Sets.newHashSet();

    @OrderBy(clause = "department")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertDepartment> departments = Sets.newHashSet();

    @OrderBy(clause = "program")
    @OneToMany(mappedBy = "advert")
    private Set<AdvertProgram> programs = Sets.newHashSet();

    @OrderBy(clause = "subjectArea")
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
    
    public Set<AdvertProgram> getPrograms() {
        return programs;
    }

    public void setPrograms(Set<AdvertProgram> programs) {
        this.programs = programs;
    }

    public Set<AdvertSubjectArea> getSubjectAreas() {
        return subjectAreas;
    }

    public void setSubjectAreas(Set<AdvertSubjectArea> subjectAreas) {
        this.subjectAreas = subjectAreas;
    }

    @Override
    public void clearAttributes(Object value) {
        Class<?> valueClass = value.getClass();
        if (valueClass.equals(Competence.class)) {
            competences.clear();
        } else if (valueClass.equals(ImportedInstitution.class)) {
            institutions.clear();
        } else if (valueClass.equals(Department.class)) {
            departments.clear();
        } else if (valueClass.equals(ImportedProgram.class)) {
            programs.clear();
        } else {
            subjectAreas.clear();
        }
    }
    
    @Override
    public void storeAttribute(AdvertAttribute<?> value) {
        Class<?> valueClass = value.getClass();
        if (valueClass.equals(Competence.class)) {
            competences.add((AdvertCompetence) value);
        } else if (valueClass.equals(ImportedInstitution.class)) {
            institutions.add((AdvertInstitution) value);
        } else if (valueClass.equals(Department.class)) {
            departments.add((AdvertDepartment) value);
        } else if (valueClass.equals(ImportedProgram.class)) {
            programs.add((AdvertProgram) value);
        } else {
            subjectAreas.add((AdvertSubjectArea) value);
        }
    }

}
