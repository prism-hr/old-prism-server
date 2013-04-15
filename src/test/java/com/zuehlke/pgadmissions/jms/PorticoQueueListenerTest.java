package com.zuehlke.pgadmissions.jms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
import com.zuehlke.pgadmissions.domain.builders.ValidApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.exceptions.UclExportServiceException;
import com.zuehlke.pgadmissions.mail.DataExportMailSender;
import com.zuehlke.pgadmissions.services.ThrottleService;
import com.zuehlke.pgadmissions.services.exporters.ApplicationFormTransferErrorBuilder;
import com.zuehlke.pgadmissions.services.exporters.UclExportService;

public class PorticoQueueListenerTest {

    private UclExportService uclExportServiceMock;
    
    private ApplicationFormDAO formDAOMock;
    
    private ApplicationFormTransferDAO formTransferDAOMock;
    
    private DataExportMailSender exportMailSenderMock;
    
    private ThrottleService throttleServiceMock;
    
    private TextMessage messageMock;
    
    @Before
    public void prepare() {
        uclExportServiceMock = EasyMock.createMock(UclExportService.class);
        formDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
        formTransferDAOMock = EasyMock.createMock(ApplicationFormTransferDAO.class);
        exportMailSenderMock = EasyMock.createMock(DataExportMailSender.class);
        messageMock = EasyMock.createMock(TextMessage.class);
        throttleServiceMock = EasyMock.createMock(ThrottleService.class);
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
        
        EasyMock.replay(uclExportServiceMock, formDAOMock, formTransferDAOMock, exportMailSenderMock, messageMock, throttleServiceMock);
        
        PorticoQueueListener listener = new PorticoQueueListener(uclExportServiceMock, formDAOMock, formTransferDAOMock, exportMailSenderMock, throttleServiceMock);
        listener.onMessage(messageMock);
        
        EasyMock.verify(uclExportServiceMock, formDAOMock, formTransferDAOMock, exportMailSenderMock, messageMock, throttleServiceMock);
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
        EasyMock.expect(messageMock.getStringProperty("Added")).andReturn("xx");
        
        UclExportServiceException uclExportServiceException = new UclExportServiceException("error",
                new ApplicationFormTransferErrorBuilder().errorHandlingStrategy(
                        ApplicationFormTransferErrorHandlingDecision.RETRY).build());
        
        uclExportServiceMock.sendToPortico(form, formTransferMock);
        EasyMock.expectLastCall().andThrow(uclExportServiceException);
        
        exportMailSenderMock.sendErrorMessage(uclExportServiceException.getMessage(), uclExportServiceException);
        
        EasyMock.replay(uclExportServiceMock, formDAOMock, formTransferDAOMock, exportMailSenderMock, messageMock, throttleServiceMock);
        
        try {
            PorticoQueueListener listener = new PorticoQueueListener(uclExportServiceMock, formDAOMock, formTransferDAOMock, exportMailSenderMock, throttleServiceMock);
            listener.onMessage(messageMock);
            Assert.fail("A TriggerJmsRetryException should have been thrown");
        }  catch (PorticoQueueListener.TriggerJmsRetryException e) {
            assertEquals(uclExportServiceException.getMessage(), e.getMessage());
        }
        
        EasyMock.verify(uclExportServiceMock, formDAOMock, formTransferDAOMock, exportMailSenderMock, messageMock, throttleServiceMock);
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
        
        exportMailSenderMock.sendErrorMessage(uclExportServiceException.getMessage(), uclExportServiceException);
        exportMailSenderMock.sendErrorMessage("There was an issue with the PORTICO interfaces which needs attention by an administrator. PRISM is now not sending any more applications to PORTICO until this issue has been resolved");
        
        throttleServiceMock.disablePorticoInterface();
        
        EasyMock.replay(uclExportServiceMock, formDAOMock, formTransferDAOMock, exportMailSenderMock, messageMock, throttleServiceMock);
        
        PorticoQueueListener listener = new PorticoQueueListener(uclExportServiceMock, formDAOMock, formTransferDAOMock, exportMailSenderMock, throttleServiceMock);        
        listener.onMessage(messageMock);
        
        EasyMock.verify(uclExportServiceMock, formDAOMock, formTransferDAOMock, exportMailSenderMock, messageMock, throttleServiceMock);
    }
}
