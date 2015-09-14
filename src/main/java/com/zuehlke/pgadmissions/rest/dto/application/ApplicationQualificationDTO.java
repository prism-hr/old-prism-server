package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;

import uk.co.alumeni.prism.utils.validation.DateNotAfterDate;
import uk.co.alumeni.prism.utils.validation.DateNotFuture;

@DateNotAfterDate(startDate = "startDate", endDate = "awardDate")
public class ApplicationQualificationDTO extends ApplicationAdvertRelationSectionDTO {

    private Integer id;

    @Valid
    @NotNull
    private ResourceCreationDTO resource;

    @NotNull
    @DateNotFuture
    private LocalDate startDate;

    @NotNull
    private LocalDate awardDate;

    @NotEmpty
    @Size(max = 200)
    private String grade;

    private FileDTO document;

    private Boolean completed;

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public FileDTO getDocument() {
        return document;
    }

    public void setDocument(FileDTO document) {
        this.document = document;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

}
