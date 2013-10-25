package com.zuehlke.pgadmissions.interceptors;

import java.io.Serializable;

public class AlertDefinition implements Serializable {

    private static final long serialVersionUID = 1212289406492146105L;

    private AlertType type;

    private String title;

    private String description;

    public AlertDefinition(AlertType type, String title, String description) {
        this.type = type;
        this.title = title;
        this.description = description;
    }

    public AlertType getType() {
        return type;
    }

    public void setType(AlertType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public enum AlertType {
        INFO, WARNING, ERROR;
    }

}
