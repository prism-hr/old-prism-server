package com.zuehlke.pgadmissions.services.uclexport;

import com.zuehlke.pgadmissions.domain.ApplicationForm;

/**
 * This is UCL data export service contract.
 * The service handles pushing business data from PRISM to UCL-PORTICO.
 */
public interface UCLExportService {

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
    Long sendToUCL(ApplicationForm applicationForm);

    /**
     * I am scheduling a new application form transfer for a given application form.
     * As a consequence this application form will be sent to UCL (some time later .. this goes in background).
     * The scheduling mechanism is reliable i.e. is able to survive possible system crash.
     *
     * @param applicationForm application form to be transferred
     * @param listener callback listener for observing the sending process
     * @return application transfer id - may be usable for diagnostic purposes, but usually simple ignore this
     */
    Long sendToUCL(ApplicationForm applicationForm, TransferListener listener);

    /**
     * I am recreating the application form transfers executors queues contents based on what we have in the database.
     * This is supposed to be invoked at system startup and is crucial to system crash recovery.
     */
    void systemStartupSendingQueuesRecovery();

}
