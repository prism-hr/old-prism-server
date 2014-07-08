package com.zuehlke.pgadmissions.rest.domain.application;

import java.util.List;

import org.joda.time.LocalDate;

public class ProgramDetailsRepresentation {

    private ImportedEntityRepresentation studyOption;

    private LocalDate startDate;

    private ImportedEntityRepresentation sourceOfInterest;

    private List<SupervisorRepresentation> suggestedSupervisors;

    public ImportedEntityRepresentation getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(ImportedEntityRepresentation studyOption) {
        this.studyOption = studyOption;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public ImportedEntityRepresentation getSourceOfInterest() {
        return sourceOfInterest;
    }

    public void setSourceOfInterest(ImportedEntityRepresentation sourceOfInterest) {
        this.sourceOfInterest = sourceOfInterest;
    }

    public List<SupervisorRepresentation> getSuggestedSupervisors() {
        return suggestedSupervisors;
    }

    public void setSuggestedSupervisors(List<SupervisorRepresentation> suggestedSupervisors) {
        this.suggestedSupervisors = suggestedSupervisors;
    }
}
