package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import org.joda.time.DateTime;

public class ProgramDTO {
    
    @NotNull
    private Integer institutionId;

    @NotNull
    private PrismProgramType programType;

    @NotNull
    private Boolean requireProjectDefinition;

    @NotEmpty
    private String title;

    @NotEmpty
    private String description;

    @NotNull
    private DateTime startDate;

    @NotNull
    private DateTime endDate;

    @NotNull
    private Boolean immediateStart;

    @Size(min = 1)
    private Integer[] studyOptions;

    public final Integer getInstitutionId() {
        return institutionId;
    }

    public final void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public PrismProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(PrismProgramType programType) {
        this.programType = programType;
    }

    public Boolean getRequireProjectDefinition() {
        return requireProjectDefinition;
    }

    public void setRequireProjectDefinition(Boolean requireProjectDefinition) {
        this.requireProjectDefinition = requireProjectDefinition;
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

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean getImmediateStart() {
        return immediateStart;
    }

    public void setImmediateStart(Boolean immediateStart) {
        this.immediateStart = immediateStart;
    }

    public Integer[] getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(Integer[] studyOptions) {
        this.studyOptions = studyOptions;
    }
}
