package uk.co.alumeni.prism.rest.dto.profile;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;
import uk.co.alumeni.prism.rest.dto.DocumentDTO;
import uk.co.alumeni.prism.rest.dto.application.ApplicationAdvertRelationSectionDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceRelationCreationDTO;
import uk.co.alumeni.prism.utils.validation.DateNotAfterDate;
import uk.co.alumeni.prism.utils.validation.DateNotFuture;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@DateNotAfterDate(startDate = "startDate", endDate = "awardDate")
public class ProfileQualificationDTO extends ApplicationAdvertRelationSectionDTO {

    private Integer id;

    @Valid
    @NotNull
    private ResourceRelationCreationDTO resource;

    @NotNull
    @DateNotFuture
    private LocalDate startDate;

    private LocalDate awardDate;

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
    public ResourceRelationCreationDTO getResource() {
        return resource;
    }

    @Override
    public void setResource(ResourceRelationCreationDTO resource) {
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
