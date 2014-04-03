package com.zuehlke.pgadmissions.services;

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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ValidApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ExportServiceException;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.services.exporters.ApplicationFormTransferErrorBuilder;
import com.zuehlke.pgadmissions.services.exporters.ApplicationFormTransferService;
import com.zuehlke.pgadmissions.services.exporters.ExportService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ExportQueueListenerTest {

    @Mock @InjectIntoByType
    private ExportService porticoExportServiceMock;
    
    @Mock @InjectIntoByType
    private ApplicationFormDAO formDAOMock;
    
    @Mock @InjectIntoByType
    private ApplicationFormTransferService applicationFormTransferServiceMock;
    
    @Mock @InjectIntoByType
    private ThrottleService throttleServiceMock;
    
    @Mock @InjectIntoByType
    private TextMessage messageMock;
    
    @Mock @InjectIntoByType
    private MailSendingService mailServiceMock;
    
    @Mock @InjectIntoByType
    private UserService userServiceMock;
    
    @TestedObject
    private ExportQueueListener listener;
    
    @Test
    public void shouldReceiveJmsMessageAndCallSendToPortico() throws JMSException {
        ApplicationFormTransfer formTransferMock = EasyMock.createMock(ApplicationFormTransfer.class);
        
        ApplicationForm form = new ValidApplicationFormBuilder().build();
        
        EasyMock.expect(messageMock.getText()).andReturn("XX");
        EasyMock.expect(formDAOMock.getByApplicationNumber("XX")).andReturn(form);
        EasyMock.expect(applicationFormTransferServiceMock.createOrReturnExistingApplicationFormTransfer(form)).andReturn(formTransferMock);
        
        EasyMock.expect(messageMock.getJMSMessageID()).andReturn("1");
        EasyMock.expect(messageMock.getStringProperty("Status")).andReturn(form.getStatus().toString());
        EasyMock.expect(messageMock.getStringProperty("Added")).andReturn("xx");
        
        try {
            porticoExportServiceMock.sendToPortico(form, formTransferMock);
        } catch (ExportServiceException e) {
            fail("The exception should not have been thrown");
        }
        
        EasyMock.replay(porticoExportServiceMock, formDAOMock, applicationFormTransferServiceMock, messageMock, throttleServiceMock, mailServiceMock);
        
        listener.onMessage(messageMock);
        
        EasyMock.verify(porticoExportServiceMock, formDAOMock, applicationFormTransferServiceMock, messageMock, throttleServiceMock, mailServiceMock);
    }
    
    @Test
    public void shouldTriggerARetryIfThereWasAnIssueWithTheNetwork() throws JMSException, ExportServiceException {
        ApplicationFormTransfer formTransferMock = EasyMock.createMock(ApplicationFormTransfer.class);
        
        ApplicationForm form = new ValidApplicationFormBuilder().build();
        
        EasyMock.expect(messageMock.getText()).andReturn("XX");
        EasyMock.expect(formDAOMock.getByApplicationNumber("XX")).andReturn(form);
        EasyMock.expect(applicationFormTransferServiceMock.createOrReturnExistingApplicationFormTransfer(form)).andReturn(formTransferMock);
        
        EasyMock.expect(messageMock.getJMSMessageID()).andReturn("1");
        EasyMock.expect(messageMock.getStringProperty("Status")).andReturn(form.getStatus().toString());
        expect(messageMock.getStringProperty("Added")).andReturn("xx");
        
        ExportServiceException uclExportServiceException = new ExportServiceException("error",
                new ApplicationFormTransferErrorBuilder().errorHandlingStrategy(
                        ApplicationFormTransferErrorHandlingDecision.RETRY).build());
        
        porticoExportServiceMock.sendToPortico(form, formTransferMock);
        EasyMock.expectLastCall().andThrow(uclExportServiceException);
        
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).build();
        RegisteredUser admin2 = new RegisteredUserBuilder().id(2).build();
        List<RegisteredUser> admins = asList(admin1, admin2);
        expect(userServiceMock.getUsersInRole(Authority.SUPERADMINISTRATOR))
        	.andReturn(admins);
        
        mailServiceMock.sendExportErrorMessage(eq(admins), eq(uclExportServiceException.getMessage()), isA(Date.class), form);
        
        EasyMock.replay(userServiceMock, porticoExportServiceMock, mailServiceMock, formDAOMock, applicationFormTransferServiceMock, messageMock, throttleServiceMock);
        
        try {
            listener.onMessage(messageMock);
            Assert.fail("A TriggerJmsRetryException should have been thrown");
        }  catch (ExportQueueListener.TriggerJmsRetryException e) {
            assertEquals(uclExportServiceException.getMessage(), e.getMessage());
        }
        
        EasyMock.verify(userServiceMock, porticoExportServiceMock, mailServiceMock, formDAOMock, applicationFormTransferServiceMock, messageMock, throttleServiceMock);
    }
    
    @Test
    public void shouldStopThePorticoInterfaceIfThereWasAConfigurationIssue() throws JMSException, ExportServiceException {
        ApplicationFormTransfer formTransferMock = EasyMock.createMock(ApplicationFormTransfer.class);
        
        ApplicationForm form = new ValidApplicationFormBuilder().build();
        
        EasyMock.expect(messageMock.getText()).andReturn("XX");
        EasyMock.expect(formDAOMock.getByApplicationNumber("XX")).andReturn(form);
        EasyMock.expect(applicationFormTransferServiceMock.createOrReturnExistingApplicationFormTransfer(form)).andReturn(formTransferMock);
        
        EasyMock.expect(messageMock.getJMSMessageID()).andReturn("1");
        EasyMock.expect(messageMock.getStringProperty("Status")).andReturn(form.getStatus().toString());
        EasyMock.expect(messageMock.getStringProperty("Added")).andReturn("xx");
        
        ExportServiceException uclExportServiceException = new ExportServiceException("error",
                new ApplicationFormTransferErrorBuilder().errorHandlingStrategy(
                        ApplicationFormTransferErrorHandlingDecision.STOP_TRANSFERS_AND_WAIT_FOR_ADMIN_ACTION).build());
        
        porticoExportServiceMock.sendToPortico(form, formTransferMock);
        EasyMock.expectLastCall().andThrow(uclExportServiceException);
        
        RegisteredUser admin1 = new RegisteredUserBuilder().id(1).build();
        RegisteredUser admin2 = new RegisteredUserBuilder().id(2).build();
        List<RegisteredUser> admins = asList(admin1, admin2);
        expect(userServiceMock.getUsersInRole(Authority.SUPERADMINISTRATOR))
        	.andReturn(admins).times(2);
        
        mailServiceMock.sendExportErrorMessage(eq(admins), eq(uclExportServiceException.getMessage()), isA(Date.class), form);
        mailServiceMock.sendExportErrorMessage(eq(admins), eq("There was an issue with the PORTICO interfaces which needs attention by an administrator. PRISM is now not sending any more applications to PORTICO until this issue has been resolved"), isA(Date.class), null);
        
        throttleServiceMock.disablePorticoInterface();
        
        EasyMock.replay(userServiceMock, porticoExportServiceMock, formDAOMock, mailServiceMock, applicationFormTransferServiceMock,  messageMock, throttleServiceMock);
        
        listener.onMessage(messageMock);
        
        EasyMock.verify(userServiceMock, porticoExportServiceMock, formDAOMock, mailServiceMock, applicationFormTransferServiceMock, messageMock, throttleServiceMock);
    }
}
