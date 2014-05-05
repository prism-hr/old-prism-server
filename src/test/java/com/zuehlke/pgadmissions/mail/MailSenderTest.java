package com.zuehlke.pgadmissions.mail;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId;
import com.zuehlke.pgadmissions.services.NotificationTemplateService;

public class MailSenderTest {

    private JavaMailSender javaMailSenderMock;
    
    private NotificationTemplateService emailTemplateServiceMock;
    
    private FreeMarkerConfig freemarkerConfigMock;
    
    private MailSender service;
    
    @Before
    public void setup() {
        javaMailSenderMock = createMock(JavaMailSender.class);
        emailTemplateServiceMock = createMock(NotificationTemplateService.class);
        freemarkerConfigMock = createMock(FreeMarkerConfig.class);
    }
    
    @Test
    public void shouldResolveSubjectWithoutArguments() {
        service = new MailSender(javaMailSenderMock, null, null, null, emailTemplateServiceMock, freemarkerConfigMock);
        
        
        expect(emailTemplateServiceMock.getSubjectForTemplate(isA(NotificationTemplateId.class)))
        .andReturn("Subject without arguments");
        
        replay(emailTemplateServiceMock);
        String result = service.resolveSubject(NotificationTemplateId.REGISTRATION_CONFIRMATION, (Object[])null);
        verify(emailTemplateServiceMock);
        
        assertEquals("Subject without arguments", result);
    }
    
    @Test
    public void shouldResolveSubjectWithOneArgument() {
        service = new MailSender(javaMailSenderMock, null, null, null, emailTemplateServiceMock, freemarkerConfigMock);
        
        
        expect(emailTemplateServiceMock.getSubjectForTemplate(isA(NotificationTemplateId.class)))
        .andReturn("Dear %s, welcome to the 105 Zoo");
        
        replay(emailTemplateServiceMock);
        String result = service.resolveSubject(NotificationTemplateId.REGISTRATION_CONFIRMATION, new Object[] {"Beppe"});
        verify(emailTemplateServiceMock);
        
        assertEquals("Dear Beppe, welcome to the 105 Zoo", result);
    }
    
    @Test
    public void shouldResolveSubjectWithTwoArguments() {
        service = new MailSender(javaMailSenderMock, null, null, null, emailTemplateServiceMock, freemarkerConfigMock);
        
        
        expect(emailTemplateServiceMock.getSubjectForTemplate(isA(NotificationTemplateId.class)))
        .andReturn("Dear %s, you have been assigned the tole of %s");
        
        replay(emailTemplateServiceMock);
        String result = service.resolveSubject(NotificationTemplateId.REGISTRATION_CONFIRMATION, new Object[] {"Beppe", "\"Gran Maestro\""});
        verify(emailTemplateServiceMock);
        
        assertEquals("Dear Beppe, you have been assigned the tole of \"Gran Maestro\"", result);
    }
    
    @Test
    public void shouldResolveSubjectWithTwoStringArgumentsAndOneNumeric() {
        service = new MailSender(javaMailSenderMock, null, null, null, emailTemplateServiceMock, freemarkerConfigMock);
        
        
        expect(emailTemplateServiceMock.getSubjectForTemplate(isA(NotificationTemplateId.class)))
        .andReturn("Dear %s, you have been reminded %d times that you have been assigned the tole of %s");
        
        replay(emailTemplateServiceMock);
        String result = service.resolveSubject(NotificationTemplateId.REGISTRATION_CONFIRMATION, new Object[] {"Beppe", new Integer(3), "\"Gran Maestro\""});
        verify(emailTemplateServiceMock);
        assertEquals("Dear Beppe, you have been reminded 3 times that you have been assigned the tole of \"Gran Maestro\"", result);
    }
    
    @Test
    public void shouldResolveSubjectWithTwoStringArgumentsAndOneNumericWithcustomOrder() {
        service = new MailSender(javaMailSenderMock, null, null, null, emailTemplateServiceMock, freemarkerConfigMock);
        
        
        expect(emailTemplateServiceMock.getSubjectForTemplate(isA(NotificationTemplateId.class)))
        .andReturn("Dear %2$s, you have been reminded %3$d times that you have been assigned the tole of %1$s");
        
        replay(emailTemplateServiceMock);
        String result = service.resolveSubject(NotificationTemplateId.REGISTRATION_CONFIRMATION, new Object[] {"\"Gran Maestro\"",  "Beppe", new Integer(3)});
        verify(emailTemplateServiceMock);
        assertEquals("Dear Beppe, you have been reminded 3 times that you have been assigned the tole of \"Gran Maestro\"", result);
    }
    
}
