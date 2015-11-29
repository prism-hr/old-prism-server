package uk.co.alumeni.prism.rest.dto.resource;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import uk.co.alumeni.prism.api.model.resource.ResourceOpportunityDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.domain.definitions.PrismStudyOption;
import uk.co.alumeni.prism.rest.dto.advert.AdvertDTO;

public class ResourceOpportunityDTO extends ResourceParentDTO implements ResourceOpportunityDefinition<AdvertDTO, PrismOpportunityType> {

    @NotNull
    private PrismOpportunityType opportunityType;

    private LocalDate availableDate;

    @Min(1)
    private Integer durationMinimum;

    @Min(1)
    private Integer durationMaximum;

    private List<PrismStudyOption> studyOptions;

    @Override
    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    @Override
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