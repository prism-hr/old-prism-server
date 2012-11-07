package com.zuehlke.pgadmissions.services.uclexport;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferErrorDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorHandlingDecision;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormTransferErrorType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationTransferStatus;
import com.zuehlke.pgadmissions.services.exporters.JSchFactory;
import com.zuehlke.pgadmissions.services.exporters.SubmitAdmissionsApplicationRequestBuilder;
import com.zuehlke.pgadmissions.utils.PausableHibernateCompatibleSequentialTaskExecutor;
import com.zuehlke.pgadmissions.utils.StacktraceDump;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.client.WebServiceTransportException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
        //todo: create an event in application form timeline ("scheduled")
        listener.queued();
        webserviceCallingQueueExecutor.execute(new Phase1Task(this, applicationForm.getId(),  transfer.getId(), listener));
        return transfer.getId();
    }

    public void systemStartupSendingQueuesRecovery() {
        //todo
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
    public void transactionallyExecuteWebserviceCallAndHandlePersistentQueue(Integer applicationId, Long transferId, TransferListener listener) {
        listener.transferStarted();

        //retrieve AppliationForm and ApplicationFormTransfer instances from the db
        ApplicationFormTransfer transfer = applicationFormTransferDAO.getById(transferId);
        ApplicationForm applicationForm  = transfer.getApplicationForm();

        //try to call the webservice
        SubmitAdmissionsApplicationRequest request;
        AdmissionsApplicationResponse response;

        try  {
            request = new SubmitAdmissionsApplicationRequestBuilder(programInstanceDAO,new ObjectFactory()).applicationForm(applicationForm).toSubmitAdmissionsApplicationRequest();
            response = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(request);

        }  catch (WebServiceTransportException e) {
            //webservice call failed because of network failure, protocol problems etc
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
            this.pauseQueueForMinutes(10);

            //schedudle the same transfer again
            this.sendToUCL(applicationForm, listener);

            return;

        } catch (SoapFaultClientException e) {
            //webservice is alive but refused to accept our request
            //usually this will be caused by validation problems - and is actually expected as side effect of PORTICO and PRISM evolution

            //save error information
            ApplicationFormTransferError error = new ApplicationFormTransferError();
            error.setTransfer(transfer);
            error.setTimepoint(new Date());
            error.setProblemClassification(ApplicationFormTransferErrorType.WEBSERVICE_SOAP_FAULT);
            error.setDiagnosticInfo(StacktraceDump.forException(e));
            error.setErrorHandlingStrategy(ApplicationFormTransferErrorHandlingDecision.GIVE_UP_THIS_TRANSFER_ONLY);
            applicationFormTransferErrorDAO.save(error);

            //we count this situations and if thay are repeating - we eventually will stop the queue and call for administrator's support
            numberOfConsecutiveSoapFaults++;
            if (numberOfConsecutiveSoapFaults > consecutiveSoapFaultsLimit) {
                this.stopQueue();

            }


            return;
        }

        //we are here so webservice answer was ok (transmission succesful, request approved)
        numberOfConsecutiveSoapFaults = 0;




        //update transfer status in the database




        //schedule phase 2

        //handle possible websersice call errors
            //decide about handling strategy
            //store the error information


    }

    @Transactional
    public void moveFromQueue1ToQueue2(ApplicationForm applicationForm) {

    }


    private void sftpSendFile() throws JSchException, IOException, SftpException {
        Session session = jSchFactory.getInstance();

        session.connect();

        Channel channel = session.openChannel("sftp");
        ChannelSftp sftpChannel = (ChannelSftp) channel;

        sftpChannel.connect();

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
