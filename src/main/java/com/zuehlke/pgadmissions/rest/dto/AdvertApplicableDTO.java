package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;
import org.joda.time.LocalDate;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.dto.DepartmentDTO;

public class AdvertApplicableDTO extends AdvertDTO {

    private DepartmentDTO department;
    
    @NotNull
    private PrismAdvertType advertType;

    private LocalDate endDate;

    @URL
    @Size(max = 2048)
    private String applyHomepage;

    @Min(1)
    private Integer studyDurationMinimum;

    @Min(1)
    private Integer studyDurationMaximum;

    @Size(min = 1)
    private PrismStudyOption[] studyOptions;

    private List<String> locations = Lists.newArrayList();

    public PrismStudyOption[] getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(PrismStudyOption[] studyOptions) {
        this.studyOptions = studyOptions;
    }

    public final List<String> getLocations() {
        return locations;
    }

    public final void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public DepartmentDTO getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDTO department) {
        this.department = department;
    }

    public PrismAdvertType getAdvertType() {
        return advertType;
    }

    public void setAdvertType(PrismAdvertType advertType) {
        this.advertType = advertType;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getApplyHomepage() {
        return applyHomepage;
    }

    public void setApplyHomepage(String applyHomepage) {
        this.applyHomepage = applyHomepage;
    }

    public Integer getStudyDurationMinimum() {
        return studyDurationMinimum;
    }

    public void setStudyDurationMinimum(Integer studyDurationMinimum) {
        this.studyDurationMinimum = studyDurationMinimum;
    }

    public Integer getStudyDurationMaximum() {
        return studyDurationMaximum;
    }

    public void setStudyDurationMaximum(Integer studyDurationMaximum) {
        this.studyDurationMaximum = studyDurationMaximum;
    }

}
