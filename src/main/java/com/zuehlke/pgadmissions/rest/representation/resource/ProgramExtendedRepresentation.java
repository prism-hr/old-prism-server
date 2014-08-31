package com.zuehlke.pgadmissions.rest.representation.resource;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;

public class ProgramExtendedRepresentation extends AbstractResourceRepresentation {

    private InstitutionRepresentation institution;

    private PrismProgramType programType;

    private String title;

    private String description;

    private String[] studyOptions;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean requireProjectDefinition;

    private Boolean immediateStart;

    public InstitutionRepresentation getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionRepresentation institution) {
        this.institution = institution;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getRequireProjectDefinition() {
        return requireProjectDefinition;
    }

    public void setRequireProjectDefinition(Boolean requireProjectDefinition) {
        this.requireProjectDefinition = requireProjectDefinition;
    }

    public Boolean getImmediateStart() {
        return immediateStart;
    }

    public void setImmediateStart(Boolean immediateStart) {
        this.immediateStart = immediateStart;
    }
}
