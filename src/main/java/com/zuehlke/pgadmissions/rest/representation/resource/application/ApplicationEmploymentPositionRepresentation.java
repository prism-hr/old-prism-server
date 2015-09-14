package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationActivity;

public class ApplicationEmploymentPositionRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private ResourceRepresentationActivity resource;

    private LocalDate startDate;

    private Boolean current;

    private LocalDate endDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ResourceRepresentationActivity getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationActivity resource) {
        this.resource = resource;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public ApplicationEmploymentPositionRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationEmploymentPositionRepresentation withResource(ResourceRepresentationActivity resource) {
        this.resource = resource;
        return this;
    }

    public ApplicationEmploymentPositionRepresentation withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public ApplicationEmploymentPositionRepresentation withCurrent(Boolean current) {
        this.current = current;
        return this;
    }

    public ApplicationEmploymentPositionRepresentation withEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public ApplicationEmploymentPositionRepresentation withLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        setLastUpdatedTimestamp(lastUpdatedTimestamp);
        return this;
    }

}
