package com.zuehlke.pgadmissions.mail;

import static org.easymock.EasyMock.createMock;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.EmailTemplateService;

public class MailSenderTest {

    private ReloadableResourceBundleMessageSource messageSource;

    @Before
    public void prepare() {
        messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setBasename("email_subjects");
    }
    
    @Test
    public void shouldRetrieveEmailSubjectCorrectly() {
        RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
        ApplicationForm applicationFormMock = EasyMock.createMock(ApplicationForm.class);
        Program programMock = EasyMock.createMock(Program.class);
        EmailTemplateService templateServiceMock = createMock(EmailTemplateService.class);
        
        EasyMock.expect(applicationFormMock.getApplicant()).andReturn(userMock);
        EasyMock.expect(applicationFormMock.getProgram()).andReturn(programMock);
        EasyMock.expect(userMock.getFirstName()).andReturn("Patricio Rodrigo");
        EasyMock.expect(userMock.getLastName()).andReturn("Est\u00E9vez Soto");
        EasyMock.expect(applicationFormMock.getApplicationNumber()).andReturn("ABC-2013-00001");
        EasyMock.expect(programMock.getTitle()).andReturn("MRES");
        MailSender sender = new MailSender(null, null, messageSource, templateServiceMock) {
        };
        
        EasyMock.replay(userMock, applicationFormMock, programMock);
        
        Assert.assertEquals(
                "Patricio Rodrigo Est\u00E9vez Soto Application ABC-2013-00001 for UCL MRES - Update Notification",
                sender.resolveMessage("application.update", applicationFormMock));
        
        EasyMock.verify(userMock, applicationFormMock, programMock);
    }
}
