package com.zuehlke.pgadmissions.controllers.prospectus;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.zuehlke.pgadmissions.exceptions.PrismException;

import freemarker.template.Template;

@Component
public class ApplyTemplateRenderer {

    private static final Logger logger = LoggerFactory.getLogger(ApplyTemplateRenderer.class);

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Value("${application.host}")
    private String host;

    private Template buttonToApplyTemplate;
    private Template linkToApplyTemplate;
    public static final String BUTTON_TO_APPLY = "/private/prospectus/button_to_apply.ftl";
    public static final String LINK_TO_APPLY = "/private/prospectus/link_to_apply.ftl";

    @PostConstruct
    private void intializeTemplates() {
        try {
            linkToApplyTemplate = freeMarkerConfigurer.getConfiguration().getTemplate(ApplyTemplateRenderer.LINK_TO_APPLY);
            buttonToApplyTemplate = freeMarkerConfigurer.getConfiguration().getTemplate(ApplyTemplateRenderer.BUTTON_TO_APPLY);
        } catch (IOException e) {
            throw new PrismException(e);
        }
    }

    public String renderButton(Map<String, Object> dataMap) {
        return processTemplate(buttonToApplyTemplate, dataMap);
    }

    public String renderLink(Map<String, Object> dataMap) {
        return processTemplate(linkToApplyTemplate, dataMap);
    }

    protected String processTemplate(Template template, Map<String, Object> dataMap) {
        dataMap.put("host", host);
        StringWriter writer = new StringWriter();
        try {
            template.process(dataMap, writer);
            return writer.toString();
        } catch (Exception e) {
            logger.error("Couldn't process template " + template.getName() + " with data: " + dataMap, e);
        }
        return null;
    }

}