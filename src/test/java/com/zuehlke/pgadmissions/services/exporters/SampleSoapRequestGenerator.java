package com.zuehlke.pgadmissions.services.exporters;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.stream.StreamResult;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testUclIntegrationContext.xml")
public class SampleSoapRequestGenerator extends UclIntegrationBaseTest {

    @Autowired
    WebServiceTemplate webServiceTemplate;
    
    private ApplicationFormDAO applicationFormDAO;
    
    private QualificationInstitutionDAO qualificationInstitutionDAO;
    
    private SubmitAdmissionsApplicationRequestBuilder requestBuilder;
    
    @Test
    @Ignore
    public void generateSampleSoapRequestsFromProductionData() throws XmlMappingException, IOException {
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
                SubmitAdmissionsApplicationRequest request = requestBuilder.applicationForm(form).toSubmitAdmissionsApplicationRequest();
                marshaller.marshal(request, new StreamResult(new File("request_" + ++idx + ".txt")));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
    
    @Before
    public void initialise() {
        applicationFormDAO = new ApplicationFormDAO(sessionFactory);
        qualificationInstitutionDAO = new QualificationInstitutionDAO(sessionFactory);
        requestBuilder = new SubmitAdmissionsApplicationRequestBuilder(qualificationInstitutionDAO, new ObjectFactory());
    }
}
