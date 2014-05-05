package com.zuehlke.pgadmissions.services.exporters.porticoit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.v2.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.exporters.SubmitAdmissionsApplicationRequestBuilderV2;
import com.zuehlke.pgadmissions.utils.DiagnosticInfoPrintUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testUclIntegrationContext.xml")
@Ignore
public class SampleSoapRequestGenerator extends AutomaticRollbackTestCase {

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Autowired
    private StateService stateService;

    private ApplicationFormDAO applicationFormDAO;

    private SubmitAdmissionsApplicationRequestBuilderV2 requestBuilder;

    /**
     * This test collects all the completed application forms (except test applications) from the database and sends them to the configured web service. It
     * enhances the data with PassportInformation, LanguageQualifications if they do not exist. This is because the current production data does not include
     * such information whereas the web service declares some of that information mandatory. Additionally, the application form statuses are changed randomly to
     * be either APPROVED, REJECTED or WITHDRAWN.
     * <p>
     * Run this test when connected to the UCL network.
     */
    @Test
    public void generateSampleSoapRequestsFromProductionData() {
        long idx = 0;
        Marshaller marshaller = webServiceTemplate.getMarshaller();

        List<ApplicationForm> applications = new LinkedList<ApplicationForm>();
        applications.addAll(applicationFormDAO.getAllApplicationsByStatus(PrismState.APPLICATION_APPROVED));
        applications.addAll(applicationFormDAO.getAllApplicationsByStatus(PrismState.APPLICATION_APPROVAL));
        applications.addAll(applicationFormDAO.getAllApplicationsByStatus(PrismState.APPLICATION_INTERVIEW));
        applications.addAll(applicationFormDAO.getAllApplicationsByStatus(PrismState.APPLICATION_REJECTED));
        applications.addAll(applicationFormDAO.getAllApplicationsByStatus(PrismState.APPLICATION_REVIEW));
        applications.addAll(applicationFormDAO.getAllApplicationsByStatus(PrismState.APPLICATION_VALIDATION));
        applications.addAll(applicationFormDAO.getAllApplicationsByStatus(PrismState.APPLICATION_WITHDRAWN));

        State approvedState = stateService.getById(PrismState.APPLICATION_APPROVED);
        State rejectedState = stateService.getById(PrismState.APPLICATION_REJECTED);
        State withdrawnState = stateService.getById(PrismState.APPLICATION_WITHDRAWN);

        for (ApplicationForm form : applications) {
            try {
                if (isTestProgram(form)) {
                    continue;
                }

                idx++;

                if (idx % 2 == 0) {
                    form.setState(approvedState);
                } else {
                    form.setState(rejectedState);
                }

                if (idx % 3 == 0) {
                    form.setState(withdrawnState);
                }

                // Generate request
                SubmitAdmissionsApplicationRequest request = requestBuilder.applicationForm(form).build();

                // Save request to file
                marshaller.marshal(request, new StreamResult(new File("request_" + idx + ".txt")));

                // Send request and record response
                try {
                    AdmissionsApplicationResponse response = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(request);
                    marshaller.marshal(response, new StreamResult(new File("response_success_" + idx + ".txt")));
                } catch (WebServiceIOException e) {
                    String forException = DiagnosticInfoPrintUtils.printRootCauseStackTrace(e);
                    IOUtils.write(forException, new FileOutputStream(new File("response_error_" + idx + ".txt")));
                } catch (SoapFaultClientException e) {
                    ByteArrayOutputStream responseMessageBuffer = new ByteArrayOutputStream(5000);
                    try {
                        e.getWebServiceMessage().writeTo(responseMessageBuffer);
                    } catch (IOException ioex) {
                        throw new RuntimeException();
                    }
                    responseMessageBuffer.writeTo(new FileOutputStream(new File("response_error_" + idx + ".txt")));
                }
            } catch (Throwable e) {
                System.err.println(DiagnosticInfoPrintUtils.printRootCauseStackTrace(e));
            }
        }

        System.out.println(idx);
    }

    private boolean isTestProgram(ApplicationForm form) {
        return form.getAdvert().getTitle().equalsIgnoreCase("ABC") || form.getAdvert().getTitle().equalsIgnoreCase("Test Programme");
    }

    @Before
    public void initialise() {
        applicationFormDAO = new ApplicationFormDAO(sessionFactory);
        requestBuilder = new SubmitAdmissionsApplicationRequestBuilderV2(new ObjectFactory());
    }
}
