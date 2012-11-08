package com.zuehlke.pgadmissions.services.uclexport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceTransportException;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v1.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferErrorDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;
import com.zuehlke.pgadmissions.services.exporters.JSchFactory;
import com.zuehlke.pgadmissions.services.exporters.SubmitAdmissionsApplicationRequestBuilderV1;
import com.zuehlke.pgadmissions.utils.PausableHibernateCompatibleSequentialTaskExecutor;
import com.zuehlke.pgadmissions.utils.StacktraceDump;

/**
 * This is UCL data export service.
 * Used for situations where we push data to UCL system (PORTICO).
 */
@Service
class UclExportServiceImpl implements UCLExportService {

    @Resource(name = "webservice-calling-queue-executor")
    private PausableHibernateCompatibleSequentialTaskExecutor webserviceCallingQueueExecutor;

    @Resource(name = "sftp-calling-queue-executor")
    private PausableHibernateCompatibleSequentialTaskExecutor sftpCallingQueueExecutor;

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Autowired
    private ProgramInstanceDAO programInstanceDAO;

    @Autowired
    private ApplicationFormTransferDAO applicationFormTransferDAO;

    @Autowired
    private ApplicationFormTransferErrorDAO applicationFormTransferErrorDAO;

    private int numberOfConsecutiveSoapFaults = 0;

    private int consecutiveSoapFaultsLimit = 5;//todo: move to the configuration

    public UclExportServiceImpl() {
    }

    @Autowired
    private JSchFactory jSchFactory;

    //oooooooooooooooooooooooooo PUBLIC API IMPLEMENTATION oooooooooooooooooooooooooooooooo

    public Long sendToUCL(ApplicationForm applicationForm) {
        return this.sendToUCL(applicationForm, null);
    }

    public Long sendToUCL(ApplicationForm applicationForm, TransferListener listener) {
        ApplicationFormTransfer transfer = this.createPersistentQueueItem(applicationForm);
        //todo: create an event in application form's timeline ("scheduled transfer to UCL-PORTICO")
        webserviceCallingQueueExecutor.execute(new Phase1Task(this, applicationForm.getId(),  transfer.getId(), listener));
        listener.queued();
        return transfer.getId();
    }

    public void systemStartupSendingQueuesRecovery() {
        //todo recreate the contents of both task queues from what we have in database
        //todo plug-in this method to application startup sequence
    }

    //ooooooooooooooooooooooooooooooo PRIVATE oooooooooooooooooooooooooooooooo

    @Transactional
    public ApplicationFormTransfer createPersistentQueueItem(ApplicationForm applicationForm) {
        ApplicationFormTransfer result = new ApplicationFormTransfer();
        result.setApplicationForm(applicationForm);
        result.setTransferStartTimepoint(new Date());
        result.setStatus(ApplicationTransferStatus.QUEUED_FOR_WEBSERVICE_CALL);
        applicationFormTransferDAO.save(result);
        return result;
    }

    @Transactional
    public void transactionallyExecuteWebserviceCallAndHandlePersistentQueue(Long transferId, TransferListener listener) {
        //retrieve AppliationForm and ApplicationFormTransfer instances from the db
        ApplicationFormTransfer transfer = applicationFormTransferDAO.getById(transferId);
        ApplicationForm applicationForm  = transfer.getApplicationForm();

        //prepare to webservice call
        SubmitAdmissionsApplicationRequest request;
        AdmissionsApplicationResponse response;
        final ByteArrayOutputStream requestMessageBuffer =  new ByteArrayOutputStream(5000);

        WebServiceMessageCallback webServiceMessageCallback = new WebServiceMessageCallback() {
            public void doWithMessage(WebServiceMessage webServiceMessage) throws IOException, TransformerException {
                webServiceMessage.writeTo(requestMessageBuffer);
            }
        };

        //build webservice request
        request = new SubmitAdmissionsApplicationRequestBuilderV1(programInstanceDAO, new ObjectFactory()).applicationForm(applicationForm).toSubmitAdmissionsApplicationRequest();

        try  {
            //call webservice
            response = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(request, webServiceMessageCallback);

        }  catch (WebServiceTransportException e) {
            //CASE 1: webservice call failed because of network failure, protocol problems etc
            //seems like we have communication problems so makes no sense to push more appliaction forms at the moment

            //save error information
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.WEBSERVICE_UNREACHABLE);
            error.setDiagnosticInfo(StacktraceDump.forException(e));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.GIVE_UP_AND_PAUSE_TRANSFERS);
            applicationFormTransferErrorDAO.save(error);

            //pause the queue for some time
            this.pauseQueueForMinutes(10); //todo: refactor - read delay value from config

            //schedudle the same transfer again
            this.sendToUCL(applicationForm, listener);

            return;

        } catch (SoapFaultClientException e) {
            //CASE 2: webservice is alive but refused to accept our request
            //usually this will be caused by validation problems - and is actually expected as side effect of PORTICO and PRISM evolution

            //save error information
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.WEBSERVICE_SOAP_FAULT);
            error.setDiagnosticInfo(StacktraceDump.forException(e));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.GIVE_UP_THIS_TRANSFER_ONLY);
            error.setRequestCopy(requestMessageBuffer.toString());
            ByteArrayOutputStream responseMessageBuffer =  new ByteArrayOutputStream(5000);
            try {
                e.getWebServiceMessage().writeTo(responseMessageBuffer);
            } catch (IOException ioex) {
                throw new RuntimeException("Line unreachable");//writing to in-memory buffer should not fail
            }
            error.setResponseCopy(responseMessageBuffer.toString());
            applicationFormTransferErrorDAO.save(error);

            //update transfer status
            transfer.setStatus(ApplicationTransferStatus.REJECTED_BY_WEBSERVICE);
            transfer.setTransferFinishTimepoint(new Date());

            //we count soap-fault situations;  if faults are repeating - we will eventually stop the queue and issue an email alert to administrators
            numberOfConsecutiveSoapFaults++;
            if (numberOfConsecutiveSoapFaults > consecutiveSoapFaultsLimit) {
                webserviceCallingQueueExecutor.pause();
                //todo: inform the admins that we have repeating webservice problems (probably by sending an email with problem description)

            }

            return;
        }

        //CASE 3; webservice answer was ok (transmission succesful, request approved)
        numberOfConsecutiveSoapFaults = 0;

        //extract precious IDs received from UCL and store them into ApplicationTransfer
        transfer.setUclUserIdReceived(response.getReference().getUserCode());
        transfer.setUclBookingReferenceReceived(response.getReference().getReferenceID());
        //todo: store these idenfitiers also into (respectively) ApplicationForm and RegisteredUser

        //schedule phase 2 (sftp)
        sftpCallingQueueExecutor.execute(new Phase2Task());



        //update transfer status in the database
        transfer.setStatus(ApplicationTransferStatus.QUEUED_FOR_ATTACHMENTS_SENDING);






        //handle possible websersice call errors
            //decide about handling strategy
            //store the error information


    }

    private void pauseQueueForMinutes(int minutes) {
        //todo
    }


    private void sftpSendFile(Document document) throws JSchException, IOException, SftpException {
        Session session = jSchFactory.getInstance();
        session.connect();
        Channel channel = session.openChannel("sftp");
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.connect();
        //todo: plug the zip package from Maciek
        OutputStream put = sftpChannel.put("test.zip");
        ZipOutputStream os = new ZipOutputStream(put);
        os.putNextEntry(new ZipEntry("test1.pdf"));
        os.write(document.getContent());
        os.closeEntry();
        IOUtils.closeQuietly(os);
        IOUtils.closeQuietly(put);
        sftpChannel.disconnect();
        session.disconnect();
    }


}
