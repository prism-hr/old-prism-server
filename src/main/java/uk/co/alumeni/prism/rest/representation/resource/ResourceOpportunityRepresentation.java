package uk.co.alumeni.prism.rest.representation.resource;

import org.joda.time.LocalDate;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;

import java.util.List;

public class ResourceOpportunityRepresentation extends ResourceParentRepresentation {

    private PrismOpportunityCategory opportunityCategory;

    private PrismOpportunityType opportunityType;

    private LocalDate availableDate;

    private Integer durationMinimum;

    private Integer durationMaximum;

    private List<PrismStudyOption> studyOptions;

    public PrismOpportunityCategory getOpportunityCategory() {
        return opportunityCategory;
    }

    public void setOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
    }

    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public LocalDate getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(LocalDate availableDate) {
        this.availableDate = availableDate;
    }

    public Integer getDurationMinimum() {
        return durationMinimum;
    }

    public void setDurationMinimum(Integer durationMinimum) {
        this.durationMinimum = durationMinimum;
    }

    public Integer getDurationMaximum() {
        return durationMaximum;
    }

    public void setDurationMaximum(Integer durationMaximum) {
        this.durationMaximum = durationMaximum;
    }

    public List<PrismStudyOption> getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(List<PrismStudyOption> studyOptions) {
        this.studyOptions = studyOptions;
    }

}
