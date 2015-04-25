package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.DepartmentDTO;

public class ProjectDTO {

    @NotNull
    private PrismScope resourceScope;

    @NotNull
    private Integer resourceId;

    private DepartmentDTO department;

    private AdvertDTO advert;
    
    private Integer durationMinimum;
    
    private Integer durationMaximum;
    
    @Size(min = 1)
    private PrismStudyOption[] studyOptions;
    
    public PrismScope getResourceScope() {
        return resourceScope;
    }

    public void setResourceScope(PrismScope resourceScope) {
        this.resourceScope = resourceScope;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public DepartmentDTO getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDTO department) {
        this.department = department;
    }

    public AdvertDTO getAdvert() {
        return advert;
    }

    public void setAdvert(AdvertDTO advert) {
        this.advert = advert;
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

    public PrismStudyOption[] getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(PrismStudyOption[] studyOptions) {
        this.studyOptions = studyOptions;
    }

}
