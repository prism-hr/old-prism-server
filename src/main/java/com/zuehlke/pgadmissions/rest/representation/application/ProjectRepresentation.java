package com.zuehlke.pgadmissions.rest.representation.application;

import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class ProjectRepresentation extends AbstractResourceRepresentation {

    private String code;

    private LocalDate dueDate;

    private String title;

    private DateTime createdTimestamp;

    private DateTime updatedTimestamp;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }
}
