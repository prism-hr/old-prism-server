package uk.co.alumeni.prism.rest.representation.resource.application;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;
import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileRepresentationSummary;

public class ApplicationRepresentationSummary extends ProfileRepresentationSummary {

    private DateTime submittedTimestamp;

    private LocalDate closingDate;

    private PrismStudyOption studyOption;

    private LocalDate startDate;

    private DocumentRepresentation coveringLetter;

    public DateTime getSubmittedTimestamp() {
        return submittedTimestamp;
    }

    public void setSubmittedTimestamp(DateTime submittedTimestamp) {
        this.submittedTimestamp = submittedTimestamp;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

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

    public DocumentRepresentation getCoveringLetter() {
        return coveringLetter;
    }

    public void setCoveringLetter(DocumentRepresentation coveringLetter) {
        this.coveringLetter = coveringLetter;
    }

}
