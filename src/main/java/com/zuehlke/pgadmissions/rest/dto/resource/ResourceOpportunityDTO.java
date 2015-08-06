package com.zuehlke.pgadmissions.rest.dto.resource;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.ObjectUtils;

import uk.co.alumeni.prism.api.model.resource.ResourceOpportunityDefinition;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedEntityDTO;

public class ResourceOpportunityDTO extends ResourceParentDivisionDTO implements
        ResourceOpportunityDefinition<AdvertDTO, PrismOpportunityType, ResourceStudyOptionDTO> {

    private ResourceParentDivisionDTO newDepartment;

    @NotNull
    private PrismOpportunityType opportunityType;

    private Integer durationMinimum;

    private Integer durationMaximum;

    private Boolean requirePositionDefinition;

    private List<ImportedEntityDTO> studyOptions;

    private List<String> studyLocations;

    private Set<ResourceStudyOptionDTO> instanceGroups;

    public ResourceParentDivisionDTO getNewDepartment() {
        return newDepartment;
    }

    public void setNewDepartment(ResourceParentDivisionDTO newParentResource) {
        this.newDepartment = newParentResource;
    }

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

    @Override
    public Set<ResourceStudyOptionDTO> getInstanceGroups() {
        return instanceGroups;
    }

    @Override
    public void setInstanceGroups(Set<ResourceStudyOptionDTO> instanceGroups) {
        this.instanceGroups = instanceGroups;
    }

    @Override
    public ResourceParentDTO getNewParentResource() {
        return ObjectUtils.firstNonNull(newDepartment, super.getNewParentResource());
    }

}
