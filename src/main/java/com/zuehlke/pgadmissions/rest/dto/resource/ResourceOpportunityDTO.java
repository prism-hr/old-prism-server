package com.zuehlke.pgadmissions.rest.dto.resource;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;

import uk.co.alumeni.prism.api.model.resource.ResourceOpportunityDefinition;

public class ResourceOpportunityDTO extends ResourceParentDTO implements ResourceOpportunityDefinition<AdvertDTO, PrismOpportunityType> {

    @NotNull
    private PrismOpportunityType opportunityType;

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
