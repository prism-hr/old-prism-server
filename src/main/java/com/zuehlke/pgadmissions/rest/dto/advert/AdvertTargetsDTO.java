package com.zuehlke.pgadmissions.rest.dto.advert;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class AdvertTargetsDTO {

    private List<AdvertTargetDTO> institutions;

    private List<AdvertTargetDTO> departments;

    private List<AdvertTargetDTO> programs;

    private List<AdvertTargetDTO> subjectAreas;

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

    public Map<String, List<? extends AdvertTargetDTO>> getTargets() {
        Map<String, List<? extends AdvertTargetDTO>> maps = ImmutableMap.of("institutions", institutions,
                "departments", departments, "programs", programs, "subjectAreas", subjectAreas);
        return maps;
    }

}
