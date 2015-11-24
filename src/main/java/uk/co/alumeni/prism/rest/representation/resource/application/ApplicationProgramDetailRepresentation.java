package uk.co.alumeni.prism.rest.representation.resource.application;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;

public class ApplicationProgramDetailRepresentation extends ApplicationSectionRepresentation {

    private PrismStudyOption studyOption;

    private LocalDate startDate;

    public PrismStudyOption getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(PrismStudyOption studyOption) {
        this.studyOption = studyOption;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public ApplicationProgramDetailRepresentation withStudyOption(PrismStudyOption studyOption) {
        this.studyOption = studyOption;
        return this;
    }

    public ApplicationProgramDetailRepresentation withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public ApplicationProgramDetailRepresentation withLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        setLastUpdatedTimestamp(lastUpdatedTimestamp);
        return this;
    }

}
