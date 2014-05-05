package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId;

public class EmailTemplateBuilder {

    private Long id;


    private NotificationTemplateId name;

    private String content;
    
    private Date version;
    
    private Boolean active;
    
    private String subject;
    
    public EmailTemplateBuilder subject(String subject) {
        this.subject = subject;
        return this;
    }
    
    public EmailTemplateBuilder active(Boolean active) {
    	this.active=active;
    	return this;
    }

    public EmailTemplateBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public EmailTemplateBuilder name(NotificationTemplateId name) {
        this.name = name;
        return this;
    }
    
    public EmailTemplateBuilder version(Date version) {
    	this.version= version;
    	return this;
    }

    public EmailTemplateBuilder content(String content) {
        this.content = content;
        return this;
    }

    public NotificationTemplate build() {
        NotificationTemplate template = new NotificationTemplate();
        template.setId(id);
        template.setName(name);
        template.setContent(content);
        template.setVersion(version);
        template.setActive(active);
        template.setSubject(subject);
        return template;
    }

}
