package com.zuehlke.pgadmissions.rest.dto.profile;

import com.zuehlke.pgadmissions.rest.dto.application.ApplicationAdvertRelationSectionDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceRelationInvitationDTO;
import org.joda.time.LocalDate;
import uk.co.alumeni.prism.utils.validation.DateNotAfterDate;
import uk.co.alumeni.prism.utils.validation.DateNotFuture;

import javax.validation.constraints.NotNull;

@DateNotAfterDate(startDate = "startDate", endDate = "endDate")
public class ProfileEmploymentPositionDTO extends ApplicationAdvertRelationSectionDTO {

    private Integer id;

    @NotNull
    private ResourceRelationInvitationDTO resource;

    @NotNull
    @DateNotFuture
    private LocalDate startDate;

    private LocalDate endDate;

    private String description;

    private Boolean current;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public ResourceRelationInvitationDTO getResource() {
        return resource;
    }

    @Override
    public void setResource(ResourceRelationInvitationDTO resource) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

}
