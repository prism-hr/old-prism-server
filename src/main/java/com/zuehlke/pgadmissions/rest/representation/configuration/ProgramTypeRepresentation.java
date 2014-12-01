package com.zuehlke.pgadmissions.rest.representation.configuration;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;

public class ProgramTypeRepresentation {

    private PrismProgramType id;

    private String displayName;

    public PrismProgramType getId() {
        return id;
    }

    public void setId(PrismProgramType id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
