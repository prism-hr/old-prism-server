package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;

public class OpportunitiesQueryDTO {

    private String locationId;

    private PrismProgramCategory programCategory;

    private PrismProgramType programType;

    private String keyword;

    private Integer[] programs;

    private Integer[] projects;

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public PrismProgramCategory getProgramCategory() {
        return programCategory;
    }

    public void setProgramCategory(PrismProgramCategory programCategory) {
        this.programCategory = programCategory;
    }

    public PrismProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(PrismProgramType programType) {
        this.programType = programType;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public final Integer[] getPrograms() {
        return programs;
    }

    public final void setPrograms(Integer[] programs) {
        this.programs = programs;
    }

    public final Integer[] getProjects() {
        return projects;
    }

    public final void setProjects(Integer[] projects) {
        this.projects = projects;
    }

}
