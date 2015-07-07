package com.zuehlke.pgadmissions.rest.dto.advert;

import java.util.List;

import com.google.common.collect.Lists;

public class AdvertTargetsDTO {

    private List<AdvertCompetenceDTO> competences;

    private List<AdvertTargetDTO> institutions;

    private List<AdvertTargetDTO> departments;

    private List<AdvertTargetDTO> programs;

    private List<AdvertTargetDTO> subjectAreas;

    public List<AdvertCompetenceDTO> getCompetences() {
        return competences;
    }

    public void setCompetences(List<AdvertCompetenceDTO> competences) {
        this.competences = competences;
    }

    public List<AdvertTargetDTO> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<AdvertTargetDTO> institutions) {
        this.institutions = institutions;
    }

    public List<AdvertTargetDTO> getDepartments() {
        return departments;
    }

    public void setDepartments(List<AdvertTargetDTO> departments) {
        this.departments = departments;
    }

    public List<AdvertTargetDTO> getPrograms() {
        return programs;
    }

    public void setPrograms(List<AdvertTargetDTO> programs) {
        this.programs = programs;
    }

    public List<AdvertTargetDTO> getSubjectAreas() {
        return subjectAreas;
    }

    public void setSubjectAreas(List<AdvertTargetDTO> subjectAreas) {
        this.subjectAreas = subjectAreas;
    }

    public List<AdvertTargetDTO> getTargets() {
        List<AdvertTargetDTO> attributes = Lists.newLinkedList();
        attributes.addAll(competences);
        attributes.addAll(institutions);
        attributes.addAll(departments);
        attributes.addAll(programs);
        attributes.addAll(subjectAreas);
        return attributes;
    }

}
