package com.zuehlke.pgadmissions.rest.dto.resource;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import uk.co.alumeni.prism.api.model.resource.ResourceOpportunityDefinition;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;

public class ResourceOpportunityDTO extends ResourceParentDivisionDTO implements
        ResourceOpportunityDefinition<AdvertDTO, PrismOpportunityType, ResourceStudyOptionDTO> {

    @NotNull
    private PrismOpportunityType opportunityType;

    private Integer durationMinimum;

    private Integer durationMaximum;

    private Boolean requirePositionDefinition;

    private List<PrismStudyOption> studyOptions;

    private List<String> studyLocations;

    private Set<ResourceStudyOptionDTO> instanceGroups;

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

    @Override
    public Boolean getRequirePositionDefinition() {
        return requirePositionDefinition;
    }

    @Override
    public void setRequirePositionDefinition(Boolean requirePositionDefinition) {
        this.requirePositionDefinition = requirePositionDefinition;
    }

    public List<PrismStudyOption> getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(List<PrismStudyOption> studyOptions) {
        this.studyOptions = studyOptions;
    }

    public List<String> getStudyLocations() {
        return studyLocations;
    }

    public void setStudyLocations(List<String> studyLocations) {
        this.studyLocations = studyLocations;
    }

    @Override
    public Set<ResourceStudyOptionDTO> getInstanceGroups() {
        return instanceGroups;
    }

    @Override
    public void setInstanceGroups(Set<ResourceStudyOptionDTO> instanceGroups) {
        this.instanceGroups = instanceGroups;
    }

}
