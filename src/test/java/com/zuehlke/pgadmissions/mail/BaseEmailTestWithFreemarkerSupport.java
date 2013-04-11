package com.zuehlke.pgadmissions.mail;

import java.io.File;
import java.io.IOException;

import org.easymock.EasyMock;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.zuehlke.pgadmissions.services.EmailTemplateService;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.TemplateException;

public abstract class BaseEmailTestWithFreemarkerSupport {

    protected FakeLoggingMailSender fakeLoggingMailSender = new FakeLoggingMailSender();
    
    protected FreeMarkerConfigurer freeMarkerConfigurer;
    
    protected TemplateLoader templateLoader;

    protected MimeMessagePreparatorFactory mimeMessagePreparatorFactory;

    @Autowired
    protected MessageSource messageSource;
    
    protected EmailTemplateService templateServiceMock;
    
    @Before
    @SuppressWarnings("deprecation")
    public void prepareFreemarkerConfig() throws IOException, TemplateException {
    		templateServiceMock = EasyMock.createMock(EmailTemplateService.class);
    		
            freeMarkerConfigurer = new FreeMarkerConfigurer();
            templateLoader = new FileTemplateLoader(new File("src/main/webapp/WEB-INF/freemarker/"));
            TemplateLoader[] templateLoaders = new TemplateLoader[] { templateLoader };
            freeMarkerConfigurer.setTemplateLoaders(templateLoaders);
            freeMarkerConfigurer.afterPropertiesSet();
            mimeMessagePreparatorFactory = new MimeMessagePreparatorFactory(freeMarkerConfigurer, false);
            fakeLoggingMailSender = new FakeLoggingMailSender();
    }
}
