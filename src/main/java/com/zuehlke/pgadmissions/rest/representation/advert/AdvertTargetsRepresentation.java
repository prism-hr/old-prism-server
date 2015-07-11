package com.zuehlke.pgadmissions.rest.representation.advert;

import java.util.List;

public class AdvertTargetsRepresentation {

    private List<AdvertCompetenceRepresentation> competences;
    
    private List<AdvertTargetRepresentation> institutions;
    
    private List<AdvertTargetRepresentation> departments;
    
    private List<AdvertTargetRepresentation> programs;
    
    private List<AdvertTargetRepresentation> subjectAreas;

    public List<AdvertCompetenceRepresentation> getCompetences() {
        return competences;
    }

    public void setCompetences(List<AdvertCompetenceRepresentation> competences) {
        this.competences = competences;
    }

    public List<AdvertTargetRepresentation> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<AdvertTargetRepresentation> institutions) {
        this.institutions = institutions;
    }

    public List<AdvertTargetRepresentation> getDepartments() {
        return departments;
    }

    public void setDepartments(List<AdvertTargetRepresentation> departments) {
        this.departments = departments;
    }

    public List<AdvertTargetRepresentation> getPrograms() {
        return programs;
    }

    public void setPrograms(List<AdvertTargetRepresentation> programs) {
        this.programs = programs;
    }

    public List<AdvertTargetRepresentation> getSubjectAreas() {
        return subjectAreas;
    }

    public void setSubjectAreas(List<AdvertTargetRepresentation> subjectAreas) {
        this.subjectAreas = subjectAreas;
    }
    
    public AdvertTargetsRepresentation withCompetences(List<AdvertCompetenceRepresentation> competences) {
        this.competences = competences;
        return this;
    }
    
    public AdvertTargetsRepresentation withInstitutions(List<AdvertTargetRepresentation> institutions) {
        this.institutions = institutions;
        return this;
    }
    
    public AdvertTargetsRepresentation withDepartments(List<AdvertTargetRepresentation> departments) {
        this.departments = departments;
        return this;
    }
    
    public AdvertTargetsRepresentation withPrograms(List<AdvertTargetRepresentation> programs) {
        this.programs = programs;
        return this;
    }
    
    public AdvertTargetsRepresentation withSubjectAreas(List<AdvertTargetRepresentation> subjectAreas) {
        this.subjectAreas = subjectAreas;
        return this;
    }
    
}
