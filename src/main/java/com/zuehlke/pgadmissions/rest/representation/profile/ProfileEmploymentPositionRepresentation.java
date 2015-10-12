package com.zuehlke.pgadmissions.rest.representation.profile;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRelationInvitationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSectionRepresentation;

public class ProfileEmploymentPositionRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private ResourceRelationInvitationRepresentation resource;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean current;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ResourceRelationInvitationRepresentation getResource() {
        return resource;
    }

    public void setResource(ResourceRelationInvitationRepresentation resource) {
        this.resource = resource;
    }

    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public ProfileEmploymentPositionRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public ProfileEmploymentPositionRepresentation withResource(final ResourceRelationInvitationRepresentation resource) {
        this.resource = resource;
        return this;
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

    public ProfileEmploymentPositionRepresentation withStartDate(final LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public ProfileEmploymentPositionRepresentation withEndDate(final LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public ProfileEmploymentPositionRepresentation withCurrent(Boolean current) {
        this.current = current;
        return this;
    }

    public ProfileEmploymentPositionRepresentation withLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        setLastUpdatedTimestamp(lastUpdatedTimestamp);
        return this;
    }

    public String getStartDateDisplay() {
        return startDate != null ? "" + startDate.getMonthOfYear() + '/' + startDate.getYear() : null;
    }

    public String getEndDateDisplay() {
        return endDate != null ? "" + endDate.getMonthOfYear() + '/' + endDate.getYear() : null;
    }

}
