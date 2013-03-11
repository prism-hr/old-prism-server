package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;

public class EmailTemplateBuilder {

    private Long id;


    private EmailTemplateName name;

    private String content;

    public EmailTemplateBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public EmailTemplateBuilder name(EmailTemplateName name) {
        this.name = name;
        return this;
    }

    public EmailTemplateBuilder content(String content) {
        this.content = content;
        return this;
    }

    public EmailTemplate build() {
        EmailTemplate template = new EmailTemplate();
        template.setId(id);
        template.setName(name);
        template.setContent(content);
        return template;
    }

}
