package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;

public class ProgramDTO {
    
    @NotNull
    private Integer institutionID;

    @NotNull
    private PrismProgramType programType;

    @NotEmpty
    private String title;

    @NotEmpty
    private String description;

    @Size(min = 1)
    private String[] studyOptions;

    public final Integer getInstitutionID() {
        return institutionID;
    }

    public final void setInstitutionID(Integer institutionID) {
        this.institutionID = institutionID;
    }

    public PrismProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(PrismProgramType programType) {
        this.programType = programType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(String[] studyOptions) {
        this.studyOptions = studyOptions;
    }
}
