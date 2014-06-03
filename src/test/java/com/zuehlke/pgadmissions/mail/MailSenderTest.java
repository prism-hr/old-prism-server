package com.zuehlke.pgadmissions.mail;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.unitils.easymock.EasyMockUnitils.replay;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.unitils.UnitilsBlockJUnit4ClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.services.NotificationTemplateService;

@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class MailSenderTest {

    @Mock
    @InjectIntoByType
    private JavaMailSender javaMailSenderMock;

    @Mock
    @InjectIntoByType
    private NotificationTemplateService emailTemplateServiceMock;

    @Mock
    @InjectIntoByType
    private FreeMarkerConfig freemarkerConfigMock;

    @TestedObject
    private MailSender service;

    @Test
    public void shouldResolveSubjectWithoutArguments() {
        NotificationTemplate notificationTemplate = new NotificationTemplate().withVersion(new NotificationTemplateVersion()
                .withSubject("Subject without arguments"));

        expect(emailTemplateServiceMock.getById(isA(PrismNotificationTemplate.class))).andReturn(notificationTemplate);

        replay();
        String result = service.resolveSubject(PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST, (Object[]) null);

        assertEquals("Subject without arguments", result);
    }

    @Test
    public void shouldResolveSubjectWithOneArgument() {
        NotificationTemplate notificationTemplate = new NotificationTemplate().withVersion(new NotificationTemplateVersion()
                .withSubject("Dear %s, welcome to the 105 Zoo"));

        expect(emailTemplateServiceMock.getById(isA(PrismNotificationTemplate.class))).andReturn(notificationTemplate);

        replay();
        String result = service.resolveSubject(PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST, new Object[] { "Beppe" });

        assertEquals("Dear Beppe, welcome to the 105 Zoo", result);
    }

}
