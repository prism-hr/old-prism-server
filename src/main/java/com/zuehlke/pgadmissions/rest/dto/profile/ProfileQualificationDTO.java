package com.zuehlke.pgadmissions.rest.dto.profile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.zuehlke.pgadmissions.rest.dto.DocumentDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAdvertRelationSectionDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;

// FIXME - adjust the date validation
public class ProfileQualificationDTO extends ApplicationAdvertRelationSectionDTO {

    private Integer id;

    @Valid
    @NotNull
    private ResourceCreationDTO resource;

    @NotNull
    private Integer startYear;

    @NotNull
    private Integer startMonth;

    private Integer awardYear;

    private Integer awardMonth;

    @NotEmpty
    @Size(max = 200)
    private String grade;

    private Boolean completed;

    private DocumentDTO document;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public ResourceCreationDTO getResource() {
        return resource;
    }

    @Override
    public void setResource(ResourceCreationDTO resource) {
        this.resource = resource;
    }

    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    public Integer getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(Integer startMonth) {
        this.startMonth = startMonth;
    }

    public Integer getAwardYear() {
        return awardYear;
    }

    public void setAwardYear(Integer awardYear) {
        this.awardYear = awardYear;
    }

    public Integer getAwardMonth() {
        return awardMonth;
    }

    public void setAwardMonth(Integer awardMonth) {
        this.awardMonth = awardMonth;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public DocumentDTO getDocument() {
        return document;
    }

    public void setDocument(DocumentDTO document) {
        this.document = document;
    }

}
