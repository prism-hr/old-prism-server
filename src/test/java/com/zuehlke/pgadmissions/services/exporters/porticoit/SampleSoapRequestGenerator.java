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

import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.v1.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.services.exporters.SubmitAdmissionsApplicationRequestBuilder;
import com.zuehlke.pgadmissions.utils.StacktraceDump;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testUclIntegrationContext.xml")
@Ignore
public class SampleSoapRequestGenerator extends AutomaticRollbackTestCase {

    @Autowired
    private WebServiceTemplate webServiceTemplate;
    
    private ApplicationFormDAO applicationFormDAO;
    
    private SubmitAdmissionsApplicationRequestBuilder requestBuilder;
    
    /**
     * This test collects all the completed application forms (except test applications) from the database 
     * and sends them to the configured web service. It enhances the data with 
     * PassportInformation, LanguageQualifications if they do not exist. This 
     * is because the current production data does not include such information 
     * whereas the web service declares some of that information mandatory. Additionally,
     * the application form statuses are changed randomly to be either APPROVED, REJECTED or 
     * WITHDRAWN. 
     * <p>
     * Run this test when connected to the UCL network.
     */
    @Test
    public void generateSampleSoapRequestsFromProductionData() {
        long idx = 0;
        Marshaller marshaller = webServiceTemplate.getMarshaller();
        
        List<ApplicationForm> applications = new LinkedList<ApplicationForm>();
        applications.addAll(applicationFormDAO.getAllApplicationsByStatus(ApplicationFormStatus.APPROVED));
        applications.addAll(applicationFormDAO.getAllApplicationsByStatus(ApplicationFormStatus.APPROVAL));
        applications.addAll(applicationFormDAO.getAllApplicationsByStatus(ApplicationFormStatus.INTERVIEW));
        applications.addAll(applicationFormDAO.getAllApplicationsByStatus(ApplicationFormStatus.REJECTED));
        applications.addAll(applicationFormDAO.getAllApplicationsByStatus(ApplicationFormStatus.REQUEST_RESTART_APPROVAL));
        applications.addAll(applicationFormDAO.getAllApplicationsByStatus(ApplicationFormStatus.REVIEW));
        applications.addAll(applicationFormDAO.getAllApplicationsByStatus(ApplicationFormStatus.VALIDATION));
        applications.addAll(applicationFormDAO.getAllApplicationsByStatus(ApplicationFormStatus.WITHDRAWN));
        
        for (ApplicationForm form : applications) {
            try {
                if (isTestProgram(form)) {
                    continue;
                }
                
                idx++;
                
                if (idx % 2 == 0) {
                    form.setStatus(ApplicationFormStatus.APPROVED);
                } else {
                    form.setStatus(ApplicationFormStatus.REJECTED);
                }
                
                if (idx % 3 == 0) {
                    form.setStatus(ApplicationFormStatus.WITHDRAWN);
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
                    String forException = StacktraceDump.printRootCauseStackTrace(e);
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
                System.err.println(StacktraceDump.printRootCauseStackTrace(e));
            }
        }
        
        System.out.println(idx);
    }

    private boolean isTestProgram(ApplicationForm form) {
        return form.getProgram().getTitle().equalsIgnoreCase("ABC") || form.getProgram().getTitle().equalsIgnoreCase("Test Programme");
    }
    
    @Before
    public void initialise() {
        applicationFormDAO = new ApplicationFormDAO(sessionFactory);
        requestBuilder = new SubmitAdmissionsApplicationRequestBuilder(new ObjectFactory());
    }
}
