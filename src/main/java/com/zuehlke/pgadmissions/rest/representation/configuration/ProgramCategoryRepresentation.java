package com.zuehlke.pgadmissions.rest.representation.configuration;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramCategory;

import java.util.List;

public class ProgramCategoryRepresentation {

    private PrismProgramCategory id;

    private String displayName;

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

    public List<ProgramTypeRepresentation> getProgramTypes() {
        return programTypes;
    }

    public void setProgramTypes(List<ProgramTypeRepresentation> programTypes) {
        this.programTypes = programTypes;
    }
}
