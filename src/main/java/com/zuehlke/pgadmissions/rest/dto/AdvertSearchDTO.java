package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;

public class AdvertSearchDTO {

    @NotNull
    private PrismProgramType programType;

    private String keywords;

    private String location;

    private String institutionId;

    private String[] studyOptions;

    public final PrismProgramType getProgramType() {
        return programType;
    }

    public final void setProgramType(PrismProgramType programType) {
        this.programType = programType;
    }

    public final String getKeywords() {
        return keywords;
    }

    public final void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public final String getLocation() {
        return location;
    }

    public final void setLocation(String location) {
        this.location = location;
    }

    public final String getInstitutionId() {
        return institutionId;
    }

    public final void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public final String[] getStudyOptions() {
        return studyOptions;
    }

    public final void setStudyOptions(String[] studyOptions) {
        this.studyOptions = studyOptions;
    }

}

