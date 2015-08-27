package com.zuehlke.pgadmissions.rest.dto.resource;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedEntityDTO;

import uk.co.alumeni.prism.api.model.resource.ResourceOpportunityDefinition;

public class ResourceOpportunityDTO extends ResourceParentDivisionDTO implements
        ResourceOpportunityDefinition<AdvertDTO, PrismOpportunityType, ResourceStudyOptionDTO> {

    @NotNull
    private PrismOpportunityType opportunityType;

    @Min(1)
    private Integer durationMinimum;

    @Min(1)
    private Integer durationMaximum;

    private Boolean requirePositionDefinition;

    private List<ImportedEntityDTO> studyOptions;

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

    public List<ImportedEntityDTO> getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(List<ImportedEntityDTO> studyOptions) {
        this.studyOptions = studyOptions;
    }

    public List<String> getStudyLocations() {
        return studyLocations;
    }

    public void setStudyLocations(List<String> studyLocations) {
        this.studyLocations = studyLocations;
    }

    public Set<ResourceStudyOptionDTO> getInstanceGroups() {
        return instanceGroups;
    }

    public void setInstanceGroups(Set<ResourceStudyOptionDTO> instanceGroups) {
        this.instanceGroups = instanceGroups;
    }

}
