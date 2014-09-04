package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import org.joda.time.LocalDate;

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
    private LocalDate startDate;

    @NotNull
    private LocalDate closeDate;

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }

    public Integer[] getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(Integer[] studyOptions) {
        this.studyOptions = studyOptions;
    }
}
