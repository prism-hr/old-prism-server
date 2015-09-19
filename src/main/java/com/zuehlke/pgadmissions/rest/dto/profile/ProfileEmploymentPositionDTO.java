package com.zuehlke.pgadmissions.rest.dto.profile;

import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAdvertRelationSectionDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceFamilyCreationDTO;

// FIXME - adjust the date validation
public class ProfileEmploymentPositionDTO extends ApplicationAdvertRelationSectionDTO {

    private Integer id;

    @NotNull
    private ResourceFamilyCreationDTO resource;

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

    @Override
    public ResourceFamilyCreationDTO getResource() {
        return resource;
    }

    @Override
    public void setResource(ResourceFamilyCreationDTO resource) {
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

}
