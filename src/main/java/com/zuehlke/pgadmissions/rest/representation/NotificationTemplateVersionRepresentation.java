package com.zuehlke.pgadmissions.rest.representation;

public class NotificationTemplateVersionRepresentation {

    private String subject;

    private String content;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
