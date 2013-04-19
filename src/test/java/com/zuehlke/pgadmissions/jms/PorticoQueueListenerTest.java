package com.zuehlke.pgadmissions.jms;

import static java.util.Arrays.asList;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ValidApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.UclExportServiceException;
import com.zuehlke.pgadmissions.mail.refactor.MailSendingService;
import com.zuehlke.pgadmissions.services.ThrottleService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.exporters.ApplicationFormTransferErrorBuilder;
import com.zuehlke.pgadmissions.services.exporters.UclExportService;

public class PorticoQueueListenerTest {

    private UclExportService uclExportServiceMock;
    
    private ApplicationFormDAO formDAOMock;
    
    private ApplicationFormTransferDAO formTransferDAOMock;
    
    
    private ThrottleService throttleServiceMock;
    
    private TextMessage messageMock;
    
    private MailSendingService mailServiceMock;
    
    private UserService userServiceMock;
    
    private PorticoQueueListener listener;
    
    @Before
    public void prepare() {
        uclExportServiceMock = EasyMock.createMock(UclExportService.class);
        formDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
        formTransferDAOMock = EasyMock.createMock(ApplicationFormTransferDAO.class);
        messageMock = EasyMock.createMock(TextMessage.class);
        throttleServiceMock = EasyMock.createMock(ThrottleService.class);
        mailServiceMock = EasyMock.createMock(MailSendingService.class);
        userServiceMock = EasyMock.createMock(UserService.class);
        listener = new PorticoQueueListener(uclExportServiceMock, formDAOMock, formTransferDAOMock, throttleServiceMock, mailServiceMock, userServiceMock);
    }
    
    @Test
    public void shouldReceiveJmsMessageAndCallSendToPortico() throws JMSException {
        ApplicationFormTransfer formTransferMock = EasyMock.createMock(ApplicationFormTransfer.class);
        
        ApplicationForm form = new ValidApplicationFormBuilder().build();
        
        EasyMock.expect(messageMock.getText()).andReturn("XX");
        EasyMock.expect(formDAOMock.getApplicationByApplicationNumber("XX")).andReturn(form);
        EasyMock.expect(formTransferDAOMock.getByApplicationForm(form)).andReturn(formTransferMock);
        
        EasyMock.expect(messageMock.getJMSMessageID()).andReturn("1");
        EasyMock.expect(messageMock.getStringProperty("Status")).andReturn(form.getStatus().toString());
        EasyMock.expect(messageMock.getStringProperty("Added")).andReturn("xx");
        
        try {
            uclExportServiceMock.sendToPortico(form, formTransferMock);
        } catch (UclExportServiceException e) {
            fail("The exception should not have been thrown");
        }
        
        EasyMock.replay(uclExportServiceMock, formDAOMock, formTransferDAOMock, messageMock, throttleServiceMock, mailServiceMock);
        
        listener.onMessage(messageMock);
        
        EasyMock.verify(uclExportServiceMock, formDAOMock, formTransferDAOMock, messageMock, throttleServiceMock, mailServiceMock);
    }
    
    @Test
    public void shouldTriggerARetryIfThereWasAnIssueWithTheNetwork() throws JMSException, UclExportServiceException {
        ApplicationFormTransfer formTransferMock = EasyMock.createMock(ApplicationFormTransfer.class);
        
        ApplicationForm form = new ValidApplicationFormBuilder().build();
        
        EasyMock.expect(messageMock.getText()).andReturn("XX");
        EasyMock.expect(formDAOMock.getApplicationByApplicationNumber("XX")).andReturn(form);
        EasyMock.expect(formTransferDAOMock.getByApplicationForm(form)).andReturn(formTransferMock);
        
        EasyMock.expect(messageMock.getJMSMessageID()).andReturn("1");
        EasyMock.expect(messageMock.getStringProperty("Status")).andReturn(form.getStatus().toString());
        expect(messageMock.getStringProperty("Added")).andReturn("xx");
        
        UclExportServiceException uclExportServiceException = new UclExportServiceException("error",
                new ApplicationFormTransferErrorBuilder().errorHandlingStrategy(
                        ApplicationFormTransferErrorHandlingDecision.RETRY).build());
        
        uclExportServiceMock.sendToPortico(form, formTransferMock);
        EasyMock.expectLastCall().andThrow(uclExportServiceException);
        
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).build();
        RegisteredUser admin2 = new RegisteredUserBuilder().id(2).build();
        List<RegisteredUser> admins = asList(admin1, admin2);
        expect(userServiceMock.getUsersInRole(Authority.SUPERADMINISTRATOR))
        	.andReturn(admins);
        
        mailServiceMock.sendExportErrorMessage(eq(admins), eq(uclExportServiceException.getMessage()), isA(Date.class));
        
        EasyMock.replay(userServiceMock, uclExportServiceMock, mailServiceMock, formDAOMock, formTransferDAOMock, messageMock, throttleServiceMock);
        
        try {
            listener.onMessage(messageMock);
            Assert.fail("A TriggerJmsRetryException should have been thrown");
        }  catch (PorticoQueueListener.TriggerJmsRetryException e) {
            assertEquals(uclExportServiceException.getMessage(), e.getMessage());
        }
        
        EasyMock.verify(userServiceMock, uclExportServiceMock, mailServiceMock, formDAOMock, formTransferDAOMock, messageMock, throttleServiceMock);
    }
    
    @Test
    public void shouldStopThePorticoInterfaceIfThereWasAConfigurationIssue() throws JMSException, UclExportServiceException {
        ApplicationFormTransfer formTransferMock = EasyMock.createMock(ApplicationFormTransfer.class);
        
        ApplicationForm form = new ValidApplicationFormBuilder().build();
        
        EasyMock.expect(messageMock.getText()).andReturn("XX");
        EasyMock.expect(formDAOMock.getApplicationByApplicationNumber("XX")).andReturn(form);
        EasyMock.expect(formTransferDAOMock.getByApplicationForm(form)).andReturn(formTransferMock);
        
        EasyMock.expect(messageMock.getJMSMessageID()).andReturn("1");
        EasyMock.expect(messageMock.getStringProperty("Status")).andReturn(form.getStatus().toString());
        EasyMock.expect(messageMock.getStringProperty("Added")).andReturn("xx");
        
        UclExportServiceException uclExportServiceException = new UclExportServiceException("error",
                new ApplicationFormTransferErrorBuilder().errorHandlingStrategy(
                        ApplicationFormTransferErrorHandlingDecision.STOP_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION).build());
        
        uclExportServiceMock.sendToPortico(form, formTransferMock);
        EasyMock.expectLastCall().andThrow(uclExportServiceException);
        
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).build();
        RegisteredUser admin2 = new RegisteredUserBuilder().id(2).build();
        List<RegisteredUser> admins = asList(admin1, admin2);
        expect(userServiceMock.getUsersInRole(Authority.SUPERADMINISTRATOR))
        	.andReturn(admins).times(2);
        
        mailServiceMock.sendExportErrorMessage(eq(admins), eq(uclExportServiceException.getMessage()), isA(Date.class));
        mailServiceMock.sendExportErrorMessage(eq(admins), eq("There was an issue with the PORTICO interfaces which needs attention by an administrator. PRISM is now not sending any more applications to PORTICO until this issue has been resolved"), isA(Date.class));
        
        throttleServiceMock.disablePorticoInterface();
        
        EasyMock.replay(userServiceMock, uclExportServiceMock, formDAOMock, mailServiceMock, formTransferDAOMock,  messageMock, throttleServiceMock);
        
        listener.onMessage(messageMock);
        
        EasyMock.verify(userServiceMock, uclExportServiceMock, formDAOMock, mailServiceMock, formTransferDAOMock, messageMock, throttleServiceMock);
    }
}
