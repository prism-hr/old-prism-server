package com.zuehlke.pgadmissions.services;

import static java.util.Arrays.asList;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.easymock.EasyMockUnitils.replay;

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
import com.zuehlke.pgadmissions.domain.ApplicationTransfer;
import com.zuehlke.pgadmissions.domain.PrismScope;
import com.zuehlke.pgadmissions.domain.PrismSystem;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ValidApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ExportServiceException;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.services.exporters.ApplicationTransferErrorBuilder;
import com.zuehlke.pgadmissions.services.exporters.ApplicationTransferService;
import com.zuehlke.pgadmissions.services.exporters.ExportService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ExportQueueListenerTest {

    @Mock @InjectIntoByType
    private ExportService porticoExportServiceMock;
    
    @Mock @InjectIntoByType
    private ApplicationFormDAO formDAOMock;
    
    @Mock @InjectIntoByType
    private ApplicationTransferService applicationFormTransferServiceMock;
    
    @Mock @InjectIntoByType
    private ApplicationExportConfigurationService throttleServiceMock;
    
    @Mock @InjectIntoByType
    private TextMessage messageMock;
    
    @Mock @InjectIntoByType
    private MailSendingService mailServiceMock;
    
    @Mock @InjectIntoByType
    private RoleService roleServiceMock;
    
    @TestedObject
    private ExportQueueListener listener;
    
    @Test
    public void shouldReceiveJmsMessageAndCallSendToPortico() throws JMSException {
        ApplicationTransfer formTransferMock = EasyMock.createMock(ApplicationTransfer.class);
        
        ApplicationForm form = new ValidApplicationFormBuilder().build();
        
        EasyMock.expect(messageMock.getText()).andReturn("XX");
        EasyMock.expect(formDAOMock.getByApplicationNumber("XX")).andReturn(form);
        EasyMock.expect(applicationFormTransferServiceMock.createOrReturnExistingApplicationFormTransfer(form)).andReturn(formTransferMock);
        
        EasyMock.expect(messageMock.getJMSMessageID()).andReturn("1");
        EasyMock.expect(messageMock.getStringProperty("Status")).andReturn(form.getState().toString());
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
        ApplicationTransfer formTransferMock = EasyMock.createMock(ApplicationTransfer.class);
        
        ApplicationForm form = new ValidApplicationFormBuilder().build();
        ExportServiceException uclExportServiceException = new ExportServiceException("error",
                new ApplicationTransferErrorBuilder().errorHandlingStrategy(
                        ApplicationTransferErrorHandlingDecision.RETRY).build());
        User admin1 = new UserBuilder().id(1).build();
        User admin2 = new UserBuilder().id(2).build();
        List<User> admins = asList(admin1, admin2);
        PrismScope systemScope = new PrismSystem();
        
        EasyMock.expect(messageMock.getText()).andReturn("XX");
        EasyMock.expect(formDAOMock.getByApplicationNumber("XX")).andReturn(form);
        EasyMock.expect(applicationFormTransferServiceMock.createOrReturnExistingApplicationFormTransfer(form)).andReturn(formTransferMock);
        
        EasyMock.expect(messageMock.getJMSMessageID()).andReturn("1");
        EasyMock.expect(messageMock.getStringProperty("Status")).andReturn(form.getState().toString());
        expect(messageMock.getStringProperty("Added")).andReturn("xx");
        
        
        porticoExportServiceMock.sendToPortico(form, formTransferMock);
        EasyMock.expectLastCall().andThrow(uclExportServiceException);
        
        expect(roleServiceMock.getUsersInRole(systemScope, Authority.SYSTEM_ADMINISTRATOR))
        	.andReturn(admins);
        
        replay();
        
        try {
            listener.onMessage(messageMock);
            Assert.fail("A TriggerJmsRetryException should have been thrown");
        }  catch (ExportQueueListener.TriggerJmsRetryException e) {
            assertEquals(uclExportServiceException.getMessage(), e.getMessage());
        }
        
    }
    
}
