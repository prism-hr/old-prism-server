package com.zuehlke.pgadmissions.rest.domain.application;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.util.Set;

public class ProgramRepresentation {

    private String code;

    private String title;

    private Boolean requireProjectDefinition;

    private LocalDate dueDate;

    private DateTime createdTimestamp;

    private DateTime updatedTimestamp;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
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
