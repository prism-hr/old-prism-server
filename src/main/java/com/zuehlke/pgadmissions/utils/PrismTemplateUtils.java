package com.zuehlke.pgadmissions.utils;

import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import freemarker.template.Template;

@Component
public class PrismTemplateUtils {

    private static final Logger logger = LoggerFactory.getLogger(PrismTemplateUtils.class);

    @Inject
    private FreeMarkerConfig freemarkerConfig;

    public String getContent(String templateName, String templateSource, Map<String, Object> model) {
        try {
            Template template = new Template(templateName, templateSource, freemarkerConfig.getConfiguration());
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (Exception e) {
            logger.error("Unable to process template", e);
            return null;
        }
    }

    public String getContentFromLocation(String templateName, String templateLocation, Map<String, Object> model) {
        try {
            String templateSource = Resources.toString(Resources.getResource(templateLocation), Charsets.UTF_8);
            Template template = new Template(templateName, templateSource, freemarkerConfig.getConfiguration());
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (Exception e) {
            logger.error("Unable to process template", e);
            return null;
        }
    }

}
