package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;

public class SystemExtendedRepresentation extends AbstractResourceRepresentation {

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
