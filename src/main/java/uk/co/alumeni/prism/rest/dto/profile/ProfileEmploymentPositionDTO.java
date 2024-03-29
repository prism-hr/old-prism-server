package uk.co.alumeni.prism.rest.dto.profile;

import org.joda.time.LocalDate;
import uk.co.alumeni.prism.rest.dto.application.ApplicationAdvertRelationSectionDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceRelationCreationDTO;
import uk.co.alumeni.prism.utils.validation.DateNotAfterDate;
import uk.co.alumeni.prism.utils.validation.DateNotFuture;

import javax.validation.constraints.NotNull;

@DateNotAfterDate(startDate = "startDate", endDate = "endDate")
public class ProfileEmploymentPositionDTO extends ApplicationAdvertRelationSectionDTO {

    private Integer id;

    @NotNull
    private ResourceRelationCreationDTO resource;

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
