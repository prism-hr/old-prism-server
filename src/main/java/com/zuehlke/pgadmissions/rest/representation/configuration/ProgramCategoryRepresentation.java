package com.zuehlke.pgadmissions.rest.representation.configuration;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramCategory;

public class ProgramCategoryRepresentation {

    private PrismProgramCategory id;

    private String displayName;
    
    private boolean hasFee;
    
    private boolean hasPay;

    private List<ProgramTypeRepresentation> programTypes;

    public PrismProgramCategory getId() {
        return id;
    }

    public void setId(PrismProgramCategory id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public final boolean isHasFee() {
        return hasFee;
    }

    public final void setHasFee(boolean hasFee) {
        this.hasFee = hasFee;
    }

    public final boolean isHasPay() {
        return hasPay;
    }

    public final void setHasPay(boolean hasPay) {
        this.hasPay = hasPay;
    }

    public List<ProgramTypeRepresentation> getProgramTypes() {
        return programTypes;
    }

    public void setProgramTypes(List<ProgramTypeRepresentation> programTypes) {
        this.programTypes = programTypes;
    }
    
}
