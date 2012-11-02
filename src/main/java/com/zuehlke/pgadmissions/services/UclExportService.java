package com.zuehlke.pgadmissions.services;

import com.sun.glass.ui.Application;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransferError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.bind.Unmarshaller;

/**
 * This is UCL data export service.
 * User for situations where we push data to UCL system (PORTICO).
 */
@Service
public class UclExportService {

    public static interface TransferListener {
        void queued();
        void transferStarted();
        void webserviceCallCompleted();
        void attachmentsTransferStarted();
        void transferCompleted(String uclUserId, String uclBookingReferenceNumber);
        void transferFailed(ApplicationFormTransferError error);
    }

    private ThreadPoolTaskExecutor webserviceCallingQueueExecutor;

    private ThreadPoolTaskExecutor sftpCallingQueueExecutor;

    public UclExportService() {
    }

    @Resource(name = "webservice-calling-queue-executor")
    public void setWebserviceCallingQueue(ThreadPoolTaskExecutor webserviceCallingQueue) {
        this.webserviceCallingQueueExecutor = webserviceCallingQueue;
    }

    @Resource(name = "sftp-calling-queue-executor")
    public void setSftpCallingQueue(ThreadPoolTaskExecutor sftpCallingQueue) {
        this.sftpCallingQueueExecutor = sftpCallingQueue;
    }

    //ooooooooooooooooooooooooooooooo PUBLIC API oooooooooooooooooooooooooooooooo

    /**
     * I am scheduling a new application form transfer for a given application form.
     * As a consequence this application form will be sent to UCL (some time later .. this goes in background).
     * The scheduling mechanism is reliable i.e. is able to survive possible system crash.<p/>
     *
     * If the caller wants to observe the sending process - please use sendToUCL(ApplicationForm,TransferListener) method.
     *
     * @param applicationForm application form to be transferred
     * @return application transfer id - may be usable for diagnostic purposes, but usually simple ignore this
     */
    public Long sendToUCL(ApplicationForm applicationForm) {
        return this.sendToUCL(applicationForm, null);
    }

    /**
     * I am scheduling a new application form transfer for a given application form.
     * As a consequence this application form will be sent to UCL (some time later .. this goes in background).
     * The scheduling mechanism is reliable i.e. is able to survive possible system crash.
     *
     * @param applicationForm application form to be transferred
     * @param listener callback listener for observing the sending process
     * @return application transfer id - may be usable for diagnostic purposes, but usually simple ignore this
     */
    public Long sendToUCL(ApplicationForm applicationForm, TransferListener listener) {
        ApplicationFormTransfer transfer = this.createPersistentQueueItem();
        listener.queued();
        webserviceCallingQueueExecutor.execute(new Phase1Task(applicationForm.getId(),  transfer.getId(), listener));
        return transfer.getId();
    }

    /**
     * I am recreating the application form transfers executors queues contents based on what we have in the database.
     * This is supposed to be invoked at system startup and is crucial to system crash recovery.
     */
    public void systemStartupSendingQueuesRecovery() {
        //todo
    }

    //ooooooooooooooooooooooooooooooo PRIVATE oooooooooooooooooooooooooooooooo

    private static class DeafListener implements TransferListener {
        @Override
        public void queued() {
            //ignore by design
        }

        @Override
        public void transferStarted() {
            //ignore by design
        }

        @Override
        public void webserviceCallCompleted() {
            //ignore by design
        }

        @Override
        public void attachmentsTransferStarted() {
            //ignore by design
        }

        @Override
        public void transferCompleted(String uclUserId, String uclBookingReferenceNumber) {
            //ignore by design
        }

        @Override
        public void transferFailed(ApplicationFormTransferError error) {
            //ignore by design
        }
    }

    @Transactional
    private ApplicationFormTransfer createPersistentQueueItem() {
        //todo
        return null;
    }

    private class Phase1Task implements Runnable {
        private Integer applicationId;
        private Long transferId;
        private TransferListener listener;

        private Phase1Task(Integer applicationId, Long transferId, TransferListener listener) {
            this.applicationId = applicationId;
            this.transferId = transferId;
            this.listener = listener;
        }

        @Override
        public void run() {
            listener.transferStarted();

            //try to call the webservice


            //update transfer status in the database

            //schedule phase 2

            //handle possible websersice call errors
                //decide about handling strategy
                //store the error information

        }
    }

    private class Phase2Task implements Runnable {
        //knows which app transfer is servicing

        @Override
        public void run() {
            //prepare attachments as a zip file

            //connect to sftp server transfer attachments

        }
    }

}
