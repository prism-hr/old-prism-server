package com.zuehlke.pgadmissions.utils;

import java.io.StringWriter;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Template;

@Component
public class FreeMarkerHelper {
    
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    public String buildString(String templateLocation, HashMap<String, Object> model) {
        try {
            StringWriter writer = new StringWriter();
            Template resourceListSelect = freeMarkerConfigurer.getConfiguration().getTemplate(templateLocation);
            resourceListSelect.process(model, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
