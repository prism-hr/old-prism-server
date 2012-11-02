package com.zuehlke.pgadmissions.services.exporters;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class AdmissionsApplicationsServiceTest {

    @Autowired
    private WebServiceTemplate webServiceTemplate;
    
    @Autowired
    private ApplicationFormDAO applicationFormDAO;
    
    @Test
    @Ignore
    @DirtiesContext
    public void testConnectivity() throws IOException {
        
        ApplicationForm applicationForm2 = applicationFormDAO.get(1);
        
//        ApplicationForm applicationForm = new ApplicationFormBuilder().personalDetails(
//                new PersonalDetailsBuilder().firstName("Kevin").lastName("Denver").title(Title.MR).toPersonalDetails())
//                .toApplicationForm();
//        
        SubmitAdmissionsApplicationRequest request = new SubmitAdmissionsApplicationRequestBuilder().applicationForm(applicationForm2).toSubmitAdmissionsApplicationRequest();
        AdmissionsApplicationResponse response = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(request);
        assertNotNull(response);
    }
}
