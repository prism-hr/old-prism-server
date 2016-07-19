package uk.co.alumeni.prism.utils;

import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.google.common.base.Charsets;

import freemarker.template.Template;

@Component
public class PrismTemplateUtils {

    private static final Logger logger = LoggerFactory.getLogger(PrismTemplateUtils.class);

    @Inject
    private FreeMarkerConfig freemarkerConfig;

    public String getContent(String templateName, String templateSource, Map<String, Object> model) {
        try {
            Template template = new Template(templateName, templateSource, freemarkerConfig.getConfiguration());
            return processTemplateIntoString(template, model);
        } catch (Exception e) {
            logger.error("Unable to process template", e);
            return null;
        }
    }

    public String getContentFromLocation(String templateLocation, Map<String, Object> model) {
        try {
            Template template = freemarkerConfig.getConfiguration().getTemplate(templateLocation, Charsets.UTF_8.name());
            return processTemplateIntoString(template, model);
        } catch (Exception e) {
            logger.error("Unable to process template", e);
            return null;
        }
    }

}
