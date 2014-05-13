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

import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
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
        NotificationTemplate notificationTemplate = new NotificationTemplate().withVersion(new NotificationTemplateVersion()
                .withSubject("Subject without arguments"));

        service = new MailSender(javaMailSenderMock, null, null, null, emailTemplateServiceMock, freemarkerConfigMock);

        expect(emailTemplateServiceMock.getById(isA(NotificationTemplateId.class))).andReturn(notificationTemplate);

        replay(emailTemplateServiceMock);
        String result = service.resolveSubject(NotificationTemplateId.SYSTEM_COMPLETE_REGISTRATION_REQUEST, (Object[]) null);
        verify(emailTemplateServiceMock);

        assertEquals("Subject without arguments", result);
    }

    @Test
    public void shouldResolveSubjectWithOneArgument() {
        NotificationTemplate notificationTemplate = new NotificationTemplate().withVersion(new NotificationTemplateVersion()
                .withSubject("Dear %s, welcome to the 105 Zoo"));

        service = new MailSender(javaMailSenderMock, null, null, null, emailTemplateServiceMock, freemarkerConfigMock);

        expect(emailTemplateServiceMock.getById(isA(NotificationTemplateId.class))).andReturn(notificationTemplate);

        replay(emailTemplateServiceMock);
        String result = service.resolveSubject(NotificationTemplateId.SYSTEM_COMPLETE_REGISTRATION_REQUEST, new Object[] { "Beppe" });
        verify(emailTemplateServiceMock);

        assertEquals("Dear Beppe, welcome to the 105 Zoo", result);
    }

}
