package com.zuehlke.pgadmissions.rest.dto.resource;

import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedEntityDTO;
import uk.co.alumeni.prism.api.model.resource.ResourceOpportunityDefinition;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ResourceOpportunityDTO extends ResourceParentDivisionDTO implements
        ResourceOpportunityDefinition<AdvertDTO, ImportedEntityDTO> {

    @NotNull
    private ImportedEntityDTO opportunityType;

    @Min(1)
    private Integer durationMinimum;

    @Min(1)
    private Integer durationMaximum;

    private List<ImportedEntityDTO> studyOptions;

    @Override
    public ImportedEntityDTO getOpportunityType() {
        return opportunityType;
    }

    @Override
    public void setOpportunityType(ImportedEntityDTO opportunityType) {
        this.opportunityType = opportunityType;
    }

    public Integer getDurationMinimum() {
        return durationMinimum;
    }

    public void setDurationMinimum(Integer durationMinimum) {
        this.durationMinimum = durationMinimum;
    }

    public Integer getDurationMaximum() {
        return durationMaximum;
    }

    public void setDurationMaximum(Integer durationMaximum) {
        this.durationMaximum = durationMaximum;
    }

    public List<ImportedEntityDTO> getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(List<ImportedEntityDTO> studyOptions) {
        this.studyOptions = studyOptions;
    }

}
