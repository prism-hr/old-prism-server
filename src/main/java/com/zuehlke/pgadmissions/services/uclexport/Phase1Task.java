package com.zuehlke.pgadmissions.services.uclexport;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.services.exporters.SubmitAdmissionsApplicationRequestBuilder;

/**
 * Taks for phase 1 of application form transfer: this is:
 *   1. try to call PORTICO webserwice
 *   2. handle errors
 *   3. schedule phase 2 if there were no errors
 *
 */
class Phase1Task implements Runnable {
    private Integer applicationId;
    private Long transferId;
    private TransferListener listener;
    private UclExportServiceImpl uclExportService;

    Phase1Task(UclExportServiceImpl uclExportService, Integer applicationId, Long transferId, TransferListener listener) {
        this.uclExportService = uclExportService;
        this.applicationId = applicationId;
        this.transferId = transferId;
        this.listener = listener;
    }

    @Override
    public void run() {
        listener.transferStarted();

        //retrieve AppliationForm and ApplicationFormTransfer instances from the db
        ApplicationFormTransfer transfer = uclExportService.applicationFormTransferDAO.getById(transferId);
        ApplicationForm applicationForm  = transfer.getApplicationForm();

        //try to call the webservice
        try  {
            SubmitAdmissionsApplicationRequest request = new SubmitAdmissionsApplicationRequestBuilder(uclExportService.programInstanceDAO,new ObjectFactory())
                .applicationForm(applicationForm).toSubmitAdmissionsApplicationRequest();
            AdmissionsApplicationResponse response = (AdmissionsApplicationResponse) uclExportService.webServiceTemplate.marshalSendAndReceive(request);
        }  catch (Throwable e) {
            //webservice call failed - we

        }

        //update transfer status in the database




        //schedule phase 2

        //handle possible websersice call errors
            //decide about handling strategy
            //store the error information

    }
}
