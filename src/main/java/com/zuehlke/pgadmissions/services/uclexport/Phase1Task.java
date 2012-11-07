package com.zuehlke.pgadmissions.services.uclexport;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.ApplicationFormTransferDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormTransfer;
import com.zuehlke.pgadmissions.services.exporters.SubmitAdmissionsApplicationRequestBuilder;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Taks for phase 1 of application form transfer: this is:
 *   1. try to call PORTICO webserwice
 *   2. handle errors
 *   3. schedule phase 2 if there were no errors
 */
class Phase1Task implements Runnable {
    private Integer applicationId;
    private Long transferId;
    private TransferListener listener;
    private UclExportServiceImpl uclExportService;
    private ApplicationFormTransferDAO applicationFormTransferDAO;
    private ProgramInstanceDAO programInstanceDAO;
    private WebServiceTemplate webServiceTemplate;

    Phase1Task(UclExportServiceImpl uclExportService, Integer applicationId, Long transferId, TransferListener listener) {
        this.uclExportService = uclExportService;
        this.applicationId = applicationId;
        this.transferId = transferId;
        this.listener = listener;
    }

    @Override
    public void run() {
        //todo: refactor to calling back into UclExportService because of Spring dependency not available here
        listener.transferStarted();

        //retrieve AppliationForm and ApplicationFormTransfer instances from the db
        ApplicationFormTransfer transfer = applicationFormTransferDAO.getById(transferId);
        ApplicationForm applicationForm  = transfer.getApplicationForm();

        //try to call the webservice
        try  {

            SubmitAdmissionsApplicationRequest request = new SubmitAdmissionsApplicationRequestBuilder(programInstanceDAO,new ObjectFactory())
                .applicationForm(applicationForm).toSubmitAdmissionsApplicationRequest();
            AdmissionsApplicationResponse response = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(request);
        }  catch (Throwable e) {
            //webservice call failed - just call

        }

        //update transfer status in the database



        //schedule phase 2

        //handle possible websersice call errors
        //choosehandling strategy
        //store the error information

    }
}
