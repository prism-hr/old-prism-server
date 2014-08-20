package com.zuehlke.pgadmissions.rest.representation.resource;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;

public class ProgramExtendedRepresentation extends AbstractResourceRepresentation {

    private String title;

    private Boolean requireProjectDefinition;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getRequireProjectDefinition() {
        return requireProjectDefinition;
    }

    public void setRequireProjectDefinition(Boolean requireProjectDefinition) {
        this.requireProjectDefinition = requireProjectDefinition;
    }

}
