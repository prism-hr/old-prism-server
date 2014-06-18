package com.zuehlke.pgadmissions.rest.domain.application;

import com.zuehlke.pgadmissions.domain.StudyOption;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;

public class ProgramDetailsRepresentation {

    private StudyOption studyOption;

    private DateTime startDate;

    private ImportedEntityRepresentation sourceOfInterest;

    private List<SupervisorRepresentation> suggestedSupervisors;

    public StudyOption getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(StudyOption studyOption) {
        this.studyOption = studyOption;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
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
