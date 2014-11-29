package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;

import java.math.BigDecimal;

public class OpportunitiesQueryDTO {

    private String locationId;

    private PrismProgramCategory programCategory;

    private PrismProgramType programType;

    private String keyword;

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public PrismProgramCategory getProgramCategory() {
        return programCategory;
    }

    public void setProgramCategory(PrismProgramCategory programCategory) {
        this.programCategory = programCategory;
    }

    public PrismProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(PrismProgramType programType) {
        this.programType = programType;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
