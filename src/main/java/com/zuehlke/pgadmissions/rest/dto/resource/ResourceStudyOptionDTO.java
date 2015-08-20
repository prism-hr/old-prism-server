package com.zuehlke.pgadmissions.rest.dto.resource;

import java.util.Set;

import org.joda.time.LocalDate;

import uk.co.alumeni.prism.api.model.resource.ResourceInstanceGroupDefinition;

public class ResourceStudyOptionDTO implements ResourceInstanceGroupDefinition<Integer, ResourceStudyOptionDTO> {

    private Integer studyOption;
    
    private LocalDate applicationStartDate;
    
    private LocalDate applicationCloseDate;
    
    private Set<ResourceStudyOptionDTO> instances;

    public Integer getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(Integer studyOption) {
        this.studyOption = studyOption;
    }

    public LocalDate getApplicationStartDate() {
        return applicationStartDate;
    }

    public void setApplicationStartDate(LocalDate applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
    }

    public LocalDate getApplicationCloseDate() {
        return applicationCloseDate;
    }

    public void setApplicationCloseDate(LocalDate applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
    }

    public Set<ResourceStudyOptionDTO> getInstances() {
        return instances;
    }

    public void setInstances(Set<ResourceStudyOptionDTO> instances) {
        this.instances = instances;
    }
    
}
