package com.zuehlke.pgadmissions.controllers.prospectus;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.zuehlke.pgadmissions.exceptions.PgadmissionsException;

import freemarker.template.Template;
import freemarker.template.TemplateException;

@Component
public class ApplyTemplateRenderer {
	
    @Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
    
    @Value("${application.host}")
    private String host;
    
	private Template buttonToApplyTemplate;
	private Template linkToApplyTemplate;
	public static final String BUTTON_TO_APPLY = "/private/prospectus/button_to_apply.ftl";
	public static final String LINK_TO_APPLY = "/private/prospectus/link_to_apply.ftl";

	@SuppressWarnings("serial")
	@PostConstruct
	private void intializeTemplates() {
		try {
			linkToApplyTemplate = freeMarkerConfigurer.getConfiguration().getTemplate(ApplyTemplateRenderer.LINK_TO_APPLY);
			buttonToApplyTemplate = freeMarkerConfigurer.getConfiguration().getTemplate(ApplyTemplateRenderer.BUTTON_TO_APPLY);
		} catch (IOException e) {
			throw new PgadmissionsException(e){/*PostConstruct MUST NOT throw checked exception*/ };
		}
	}
	
	public String renderButton(Map<String, Object> dataMap) throws TemplateException, IOException {
		return processTemplate(buttonToApplyTemplate, dataMap);
	}

	public String renderLink(Map<String, Object> dataMap) throws TemplateException, IOException {
		return processTemplate(linkToApplyTemplate, dataMap);
	}

	protected String processTemplate(Template template, Map<String, Object> dataMap) throws TemplateException, IOException {
	    dataMap.put("host", host);
	    StringWriter writer = new StringWriter();
	    template.process(dataMap, writer);
	    String result = writer.toString();
	    writer.close();
	    return result;
	}
	
}