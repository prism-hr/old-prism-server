package com.zuehlke.pgadmissions.services.exporters;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
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

import com.zuehlke.pgadmissions.admissionsservice.jaxb.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.CourseApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWebServiceContext.xml")
public class AdmissionsApplicationsServiceTest extends UclIntegrationBaseTest {

    private final Logger logger = Logger.getLogger(AdmissionsApplicationsServiceTest.class);
    
    @Autowired
    private WebServiceTemplate webServiceTemplate;
    
    private ProgramInstanceDAO programInstanceDAOMock = null;
    
    private ApplicationForm applicationForm = null;

    private DomicileDAO domicileDAOMock;

    private QualificationInstitutionDAO qualificationInstitutionDAOMock;
    
    @Test
    public void shouldMarshallGMonthCorrectly() throws XmlMappingException, IOException, DatatypeConfigurationException {
        /*
         * http://java.net/jira/browse/JAXB-643?page=com.atlassian.jira.plugin.system.issuetabpanels%3Aworklog-tabpanel
         * Sun's DatatypeFactory#newXMLGregorianCalendar(String) and XMLGregorianCalendar 
         * which was bundled in jdk/jre6 lost backward compatibility in xsd:gMonth.
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
        
        logger.info(String.format("Marshalled : %s", st.toString()));
        
        assertTrue(StringUtils.contains(st.toString(), GMonthAdapter.print(firstDayOfMonth)));
    }
    
    @Test
    public void marshallRequest() throws XmlMappingException, IOException {
        
        ProgramInstance instance = new ProgramInstanceBuilder()
            .academicYear("2013")
            .applicationDeadline(DateUtils.addMonths(new Date(), 1))
            .applicationStartDate(new Date())
            .enabled(true)
            .identifier("0009")
            .studyOption("F+++++", "Full-time")
            .toProgramInstance();
        
        EasyMock.expect(programInstanceDAOMock.getCurrentProgramInstanceForStudyOption(EasyMock.anyObject(Program.class), EasyMock.anyObject(String.class))).andReturn(instance);
        
        EasyMock.expect(qualificationInstitutionDAOMock.getAllInstitutionByName(EasyMock.anyObject(String.class))).andReturn(new ArrayList<QualificationInstitution>());
        
        EasyMock.replay(programInstanceDAOMock, qualificationInstitutionDAOMock);
        
        SubmitAdmissionsApplicationRequestBuilder submitAdmissionsApplicationRequestBuilder = new SubmitAdmissionsApplicationRequestBuilder(programInstanceDAOMock, qualificationInstitutionDAOMock, domicileDAOMock, new ObjectFactory());
        SubmitAdmissionsApplicationRequest request = submitAdmissionsApplicationRequestBuilder.applicationForm(applicationForm).toSubmitAdmissionsApplicationRequest();
        
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        webServiceTemplate.getMarshaller().marshal(request, result);
        
        String requestAsString = writer.toString();
        
        assertNotNull(requestAsString);
        assertTrue(StringUtils.isNotBlank(requestAsString));
        
        System.out.println(requestAsString);
    }

    /**
     * Sends a valid application form to the UCL test web service configured in the environment.properties.
     * Run this test when connected to the UCL network.
     */
    @Test
    @Ignore
    public void sendValidApplicationForm() {
        EasyMock.expect(programInstanceDAOMock.getCurrentProgramInstanceForStudyOption(applicationForm.getProgram(), applicationForm.getProgrammeDetails().getStudyOption())).andReturn(applicationForm.getProgram().getInstances().get(0));
        
        EasyMock.expect(qualificationInstitutionDAOMock.getAllInstitutionByName(EasyMock.anyObject(String.class))).andReturn(new ArrayList<QualificationInstitution>());
        
        EasyMock.replay(programInstanceDAOMock, qualificationInstitutionDAOMock);
        
        SubmitAdmissionsApplicationRequest request = new SubmitAdmissionsApplicationRequestBuilder(programInstanceDAOMock, qualificationInstitutionDAOMock, domicileDAOMock, new ObjectFactory()).applicationForm(applicationForm).toSubmitAdmissionsApplicationRequest();
        
        AdmissionsApplicationResponse response = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(request);
        
        assertNotNull(response);
        
        System.out.println(String.format("ApplicantID [id=%s], ApplicationID [id=%s]", response.getReference().getApplicantID(), response.getReference().getApplicationID()));
        
        EasyMock.verify(programInstanceDAOMock, qualificationInstitutionDAOMock);
    }
    
    @Before
    public void setup() {
        qualificationInstitutionDAOMock = EasyMock.createMock(QualificationInstitutionDAO.class);
        domicileDAOMock = EasyMock.createMock(DomicileDAO.class);
        programInstanceDAOMock = EasyMock.createMock(ProgramInstanceDAO.class);
        applicationForm = getValidApplicationForm();
    }
}
