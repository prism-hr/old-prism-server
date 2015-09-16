package com.zuehlke.pgadmissions.rest.representation.profile;

import static com.zuehlke.pgadmissions.PrismConstants.BACK_SLASH;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationActivity;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSectionRepresentation;

public class ProfileEmploymentPositionRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private ResourceRepresentationActivity resource;

    private Integer startYear;

    private Integer startMonth;

    private Integer endYear;

    private Integer endMonth;

    private Boolean current;

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

    public Integer getEndYear() {
        return endYear;
    }

    public void setEndYear(Integer endYear) {
        this.endYear = endYear;
    }

    public Integer getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(Integer endMonth) {
        this.endMonth = endMonth;
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

    public ProfileEmploymentPositionRepresentation withResource(ResourceRepresentationActivity resource) {
        this.resource = resource;
        return this;
    }

    public ProfileEmploymentPositionRepresentation withStartYear(Integer startYear) {
        this.startYear = startYear;
        return this;
    }

    public ProfileEmploymentPositionRepresentation withStartMonth(Integer startMonth) {
        this.startMonth = startMonth;
        return this;
    }

    public ProfileEmploymentPositionRepresentation withEndYear(Integer endYear) {
        this.endYear = endYear;
        return this;
    }

    public ProfileEmploymentPositionRepresentation withEndMonth(Integer endMonth) {
        this.endMonth = endMonth;
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
        return startYear == null ? null : startMonth.toString() + BACK_SLASH + startYear.toString();
    }

    public String getEndDateDisplay() {
        return endYear == null ? null : endMonth.toString() + BACK_SLASH + endYear.toString();
    }

}
