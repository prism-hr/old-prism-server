package com.zuehlke.pgadmissions.rest.representation.configuration;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;

public class ProgramCategoryRepresentation {

    private PrismProgramCategory id;

    private boolean hasFee;

    private boolean hasPay;

    private List<PrismProgramType> programTypes;

    public PrismProgramCategory getId() {
        return id;
    }

    public void setId(PrismProgramCategory id) {
        this.id = id;
    }

    public boolean isHasFee() {
        return hasFee;
    }

    public void setHasFee(boolean hasFee) {
        this.hasFee = hasFee;
    }

    public boolean isHasPay() {
        return hasPay;
    }

    public void setHasPay(boolean hasPay) {
        this.hasPay = hasPay;
    }

    public List<PrismProgramType> getProgramTypes() {
        return programTypes;
    }

    public void setProgramTypes(List<PrismProgramType> programTypes) {
        this.programTypes = programTypes;
    }

}
