package com.zuehlke.pgadmissions.services.exporters;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.xml.transform.TransformerException;
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
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.utils.StacktraceDump;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testUclIntegrationContext.xml")
public class SampleSoapRequestGenerator extends UclIntegrationBaseTest {

    @Autowired
    private WebServiceTemplate webServiceTemplate;
    
    private ApplicationFormDAO applicationFormDAO;
    
    private QualificationInstitutionDAO qualificationInstitutionDAO;
    
    private SubmitAdmissionsApplicationRequestBuilder requestBuilder;
    
    private Random r = new Random();
    
    private ApplicationForm validApplicationForm;
    
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
    @Ignore
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
                
                if (idx % 2 == 0) {
                    form.setStatus(ApplicationFormStatus.APPROVED);
                } else {
                    form.setStatus(ApplicationFormStatus.REJECTED);
                }
                
                if (idx % 3 == 0) {
                    form.setStatus(ApplicationFormStatus.WITHDRAWN);
                }
                
                form.setIpAddressAsString(generateRandomIpAddress());
                
                if (form.getFundings().isEmpty()) {
                    form.getFundings().addAll(validApplicationForm.getFundings());
                }
                
                if (form.getPersonalDetails().getLanguageQualifications().isEmpty()) {
                    form.getPersonalDetails().getLanguageQualifications().addAll(validApplicationForm.getPersonalDetails().getLanguageQualifications());
                }
                
                if (form.getPersonalDetails().isRequiresVisaSet()) {
                    form.getPersonalDetails().setPassportInformation(validApplicationForm.getPersonalDetails().getPassportInformation());
                }
                
                if (form.getQualifications().isEmpty()) {
                    form.getQualifications().addAll(validApplicationForm.getQualifications());
                }
                
                // Generate request
                SubmitAdmissionsApplicationRequest request = requestBuilder.applicationForm(form).build();
                
                final ByteArrayOutputStream requestMessageBuffer = new ByteArrayOutputStream(5000);

                WebServiceMessageCallback webServiceMessageCallback = new WebServiceMessageCallback() {
                    public void doWithMessage(WebServiceMessage webServiceMessage) throws IOException, TransformerException {
                        webServiceMessage.writeTo(requestMessageBuffer);
                    }
                };
                
                // Send request and record response
                try {
                    AdmissionsApplicationResponse response = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(request, webServiceMessageCallback);
                    // Save request to file
                    requestMessageBuffer.writeTo(new FileOutputStream(new File("request_" + ++idx + ".txt")));
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
                StacktraceDump.printRootCauseStackTrace(e);
            }
        }
    }

    private boolean isTestProgram(ApplicationForm form) {
        return form.getProgram().getTitle().equalsIgnoreCase("ABC") || form.getProgram().getTitle().equalsIgnoreCase("Test Programme");
    }
    
    private String generateRandomIpAddress() {
        return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
    }
    
    @Before
    public void initialise() {
        validApplicationForm = getValidApplicationForm();
        applicationFormDAO = new ApplicationFormDAO(sessionFactory);
        qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);
        requestBuilder = new SubmitAdmissionsApplicationRequestBuilder(qualificationInstitutionDAO, new ObjectFactory());
    }
}
