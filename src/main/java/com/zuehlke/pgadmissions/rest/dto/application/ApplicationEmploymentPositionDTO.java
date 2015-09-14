package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;

import uk.co.alumeni.prism.utils.validation.DateNotAfterDate;
import uk.co.alumeni.prism.utils.validation.DateNotFuture;

@DateNotAfterDate(startDate = "startDate", endDate = "endDate")
public class ApplicationEmploymentPositionDTO extends ApplicationAdvertRelationSectionDTO {

    private Integer id;

    @NotNull
    private ResourceCreationDTO resource;

    @NotNull
    @DateNotFuture
    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean current;

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

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

}
