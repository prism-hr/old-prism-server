package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class ProjectExtendedRepresentation extends AbstractResourceRepresentation {

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
