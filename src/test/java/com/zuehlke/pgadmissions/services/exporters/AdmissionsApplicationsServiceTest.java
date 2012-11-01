package com.zuehlke.pgadmissions.services.exporters;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.CourseApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class AdmissionsApplicationsServiceTest extends AutomaticRollbackTestCase {

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Test
    @DirtiesContext
    public void shouldMarshallGMonthCorrectly() throws XmlMappingException, IOException, DatatypeConfigurationException {
        /*
         * http://java.net/jira/browse/JAXB-643?page=com.atlassian.jira.plugin.system.issuetabpanels%3Aworklog-tabpanel
         * Sun's DatatypeFactory#newXMLGregorianCalendar(String) and XMLGregorianCalendar 
         * which was buldled in jdk/jre6 lost backward compatibility in xsd:gMonth.
        */
        
        DateTime firstDayOfMonth = new DateTime().dayOfMonth().withMinimumValue();
        
        CourseApplicationTp courseApplicationTp = new CourseApplicationTp();
        courseApplicationTp.setStartMonth(firstDayOfMonth);        
        
        ApplicationTp applicationTp = new ApplicationTp();
        applicationTp.setCourseApplication(courseApplicationTp);
        
        SubmitAdmissionsApplicationRequest admissionsApplicationRequest = new SubmitAdmissionsApplicationRequest();
        admissionsApplicationRequest.setApplication(applicationTp);
        
        StringWriter st = new StringWriter(); 
        Marshaller marshaller = webServiceTemplate.getMarshaller();
        marshaller.marshal(admissionsApplicationRequest, new StreamResult(st));
        
        assertTrue(StringUtils.contains(st.toString(), "<ns2:startMonth>" + GMonthAdapter.print(firstDayOfMonth) + "</ns2:startMonth>"));
    }
    
    @Test
    @Ignore
    @DirtiesContext
    public void testConnectivity() throws IOException {

        ApplicationFormDAO applicationFormDAO = new ApplicationFormDAO(sessionFactory);
        ProgramInstanceDAO programInstanceDAO = new ProgramInstanceDAO(sessionFactory);

        SubmitAdmissionsApplicationRequestBuilder submitAdmissionsApplicationRequestBuilder = new SubmitAdmissionsApplicationRequestBuilder(programInstanceDAO, new ObjectFactory());

        ApplicationForm applicationForm2 = applicationFormDAO.get(2682);

        SubmitAdmissionsApplicationRequest request = submitAdmissionsApplicationRequestBuilder.applicationForm(applicationForm2).toSubmitAdmissionsApplicationRequest();
        
        try {
            AdmissionsApplicationResponse response = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(request);
        } catch (SoapFaultClientException e) {
            System.out.println("FaultCode: " + e.getFaultCode());
            System.out.println("Reason: " + e.getFaultStringOrReason());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            e.getWebServiceMessage().writeTo(os);
            System.out.println("Request failed [" + os.toString("UTF-8") + "]");
        }
    }
}
