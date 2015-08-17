package com.zuehlke.pgadmissions.rest.dto.advert;

import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AdvertTargetsDTO {

    private List<AdvertTargetDTO> institutions;

    private List<AdvertTargetDTO> departments;

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

    public List<AdvertTargetDTO> getSubjectAreas() {
        return subjectAreas;
    }

    public void setSubjectAreas(List<AdvertTargetDTO> subjectAreas) {
        this.subjectAreas = subjectAreas;
    }

    public Map<String, List<? extends AdvertTargetDTO>> getTargets() {
        Map<String, List<? extends AdvertTargetDTO>> targets = Maps.newLinkedHashMap();
        targets.put("institutions", institutions != null ? institutions : Collections.emptyList());
        targets.put("departments", departments != null ? departments : Collections.emptyList());
        targets.put("subjectAreas", subjectAreas != null ? subjectAreas : Collections.emptyList());
        return targets;
    }

}
