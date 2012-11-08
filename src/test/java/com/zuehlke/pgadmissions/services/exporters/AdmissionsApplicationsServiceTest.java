package com.zuehlke.pgadmissions.services.exporters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.AdmissionsApplicationResponse;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.ApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.CourseApplicationTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.v2.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.builders.AdditionalInformationBuilder;
import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DisabilityBuilder;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.EmploymentPositionBuilder;
import com.zuehlke.pgadmissions.domain.builders.EthnicityBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageBuilder;
import com.zuehlke.pgadmissions.domain.builders.LanguageQualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.PassportInformationBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationTypeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.SourcesOfInterestBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.domain.enums.Title;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class AdmissionsApplicationsServiceTest extends AutomaticRollbackTestCase {

    private final Logger logger = Logger.getLogger(AdmissionsApplicationsServiceTest.class);
    
    @Autowired
    @Qualifier("webServiceTemplateV2")
    private WebServiceTemplate webServiceTemplate;
    
    private ProgramInstanceDAO programInstanceDAOMock = null;
    
    private ApplicationFormDAO applicationFormDAO = null;
    
    private ProgramInstanceDAO programInstanceDAO = null;
    
    private ApplicationForm applicationForm = null;
    
    @Test
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
        
        logger.info(String.format("Marshalled : %s", st.toString()));
        
        assertTrue(StringUtils.contains(st.toString(), GMonthAdapter.print(firstDayOfMonth)));
    }
    
    @Test
    @Ignore
    public void sendValidApplicationForm() {
        EasyMock.expect(
                programInstanceDAOMock.getCurrentProgramInstanceForStudyOption(applicationForm.getProgram(),
                        applicationForm.getProgrammeDetails().getStudyOption())).andReturn(
                applicationForm.getProgram().getInstances().get(0));
        EasyMock.replay(programInstanceDAOMock);
        
        SubmitAdmissionsApplicationRequest request = new SubmitAdmissionsApplicationRequestBuilderV2(programInstanceDAOMock,
                new ObjectFactory()).applicationForm(applicationForm).toSubmitAdmissionsApplicationRequest();
        
        AdmissionsApplicationResponse response = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(request);
        
        assertNotNull(response);
    }
    
    @Test
    @Ignore
    public void testConnectivity() throws IOException {
        ProgramInstance instance = new ProgramInstanceBuilder().academicYear("2013")
                .applicationDeadline(DateUtils.addMonths(new Date(), 1)).applicationStartDate(new Date()).enabled(true)
                .identifier("0009").studyOption("F+++++", "Full-time").toProgramInstance();
    
        EasyMock.expect(programInstanceDAOMock.getCurrentProgramInstanceForStudyOption(EasyMock.anyObject(Program.class), EasyMock.anyObject(String.class))).andReturn(instance);
        EasyMock.replay(programInstanceDAOMock);
    
        SubmitAdmissionsApplicationRequestBuilderV2 submitAdmissionsApplicationRequestBuilder = new SubmitAdmissionsApplicationRequestBuilderV2(programInstanceDAOMock, new ObjectFactory());
        SubmitAdmissionsApplicationRequest request = submitAdmissionsApplicationRequestBuilder.applicationForm(applicationForm).toSubmitAdmissionsApplicationRequest();
    
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        webServiceTemplate.getMarshaller().marshal(request, result);
    
        String requestAsString = writer.toString();
    
        assertNotNull(requestAsString);
        assertTrue(StringUtils.isNotBlank(requestAsString));
    
        System.out.println(requestAsString);        
        
        AdmissionsApplicationResponse response = null;
        try {
            response = (AdmissionsApplicationResponse) webServiceTemplate.marshalSendAndReceive(request);
        } catch (SoapFaultClientException e) {
            System.err.println(e.getSoapFault().getFaultStringOrReason());
            System.err.println(e.getSoapFault().getFaultDetail());
            e.printStackTrace();
        }
        
        assertNotNull(response);
        assertEquals(response.getReference().getApplicantID(), "");
        assertEquals(response.getReference().getApplicationID(), "");
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
        EasyMock.replay(programInstanceDAOMock);
        
        SubmitAdmissionsApplicationRequestBuilderV2 submitAdmissionsApplicationRequestBuilder = new SubmitAdmissionsApplicationRequestBuilderV2(programInstanceDAOMock, new ObjectFactory());
        SubmitAdmissionsApplicationRequest request = submitAdmissionsApplicationRequestBuilder.applicationForm(applicationForm).toSubmitAdmissionsApplicationRequest();
        
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        webServiceTemplate.getMarshaller().marshal(request, result);
        
        String requestAsString = writer.toString();
        
        assertNotNull(requestAsString);
        assertTrue(StringUtils.isNotBlank(requestAsString));
        
        System.out.println(requestAsString);
    }
    
    @Before
    public void setup() {
        applicationFormDAO = new ApplicationFormDAO(sessionFactory);
        programInstanceDAO = new ProgramInstanceDAO(sessionFactory);
        
        programInstanceDAOMock = EasyMock.createMock(ProgramInstanceDAO.class);
        
        String addressStr = "Zuhlke Engineering Ltd\n43 Whitfield Street\nLondon W1T 4HD\nUnited Kingdom";
        RegisteredUser user = new RegisteredUserBuilder().id(1).username("denk@zhaw.ch").toUser();
        Country country = new CountryBuilder().id(1).code("XK").name("United Kingdom").toCountry();
        Address address = new AddressBuilder().id(1).country(country).address1(addressStr.split("\n")[0]).address2(addressStr.split("\n")[1]).address3(addressStr.split("\n")[2]).address4(addressStr.split("\n")[3]).toAddress();
        EmploymentPosition employmentPosition = new EmploymentPositionBuilder()
            .current(true)
            .employerAdress1(addressStr)
            .employerCountry(country)
            .employerName("Zuhlke Ltd.")
            .toEmploymentPosition();
        Language language = new LanguageBuilder().code("XK").name("United Kingdom").toLanguage();
        Disability disability = new DisabilityBuilder().code(0).name("No Disability").toDisability();
        Ethnicity ethnicity = new EthnicityBuilder().code(10).name("White").toEthnicity();
        Domicile domicile = new DomicileBuilder().code("XK").name("United Kingdom").toDomicile();
        PersonalDetails personalDetails = new PersonalDetailsBuilder()
            .candiateNationalities(language)
            .country(country)
            .dateOfBirth(org.apache.commons.lang.time.DateUtils.addYears(new Date(), -28))
            .disability(disability)
            .email("ked@zuhlke.com")
            .englishFirstLanguage(true)
            .ethnicity(ethnicity)
            .firstName("Kevin")
            .lastName("Denver")
            .gender(Gender.MALE)
            .requiresVisa(true)
            .passportInformation(new PassportInformationBuilder().passportNumber("000").nameOnPassport("Kevin Francis Denver").passportExpiryDate(org.apache.commons.lang.time.DateUtils.addYears(new Date(), 20)).toPassportInformation())
            .languageQualificationAvailable(true)
            .languageQualifications(new LanguageQualificationBuilder().dateOfExamination(new Date()).examTakenOnline(false).languageQualification(LanguageQualificationEnum.OTHER).listeningScore("1").otherQualificationTypeName("FooBar").overallScore("1").readingScore("1").speakingScore("1").writingScore("1").toLanguageQualification())
            .phoneNumber("+44 (0) 123 123 1234")
            .requiresVisa(false)
            .residenceDomicile(domicile)
            .title(Title.EUROPEAN_ENGINEER)
            .toPersonalDetails();
        AdditionalInformation additionalInformation = new AdditionalInformationBuilder()
            .setConvictions(false)
            .toAdditionalInformation();
        ProgramInstance instance = new ProgramInstanceBuilder()
            .id(1)
            .academicYear("2013")
            .applicationDeadline(org.apache.commons.lang.time.DateUtils.addMonths(new Date(), 1))
            .applicationStartDate(org.apache.commons.lang.time.DateUtils.addMonths(new Date(), -1))
            .enabled(true)
            .studyOption("F+++++", "Full-time")
            .identifier("0009")
            .toProgramInstance();
        Program program = new ProgramBuilder()
            .id(1)
            .administrators(user)
            .approver(user)
            .code("TMRMBISING01")
            .enabled(true)
            .instances(instance)
            .interviewers(user)
            .reviewers(user)
            .supervisors(user)
            .title("MRes Medical and Biomedical Imaging")
            .toProgram();
        SourcesOfInterest interest = new SourcesOfInterestBuilder().id(1).code("BRIT_COUN").name("British Council").toSourcesOfInterest();
        ProgrammeDetails programDetails = new ProgrammeDetailsBuilder()
            .id(1)
            .programmeName("MRes Medical and Biomedical Imaging")
            .projectName("Project Title")
            .sourcesOfInterest(interest)
            .startDate(org.apache.commons.lang.time.DateUtils.addDays(new Date(), 1))
            .studyOption("F+++++", "Full-time")
            .toProgrammeDetails();
        QualificationType qualificationType = new QualificationTypeBuilder().code("DEGTRE").name("Bachelors Degree - France").toQualificationTitle();
        Qualification qualification = new QualificationBuilder()
            .awardDate(new Date())
            .grade("6")
            .institution("ZHAW")
            .institutionCountry(domicile)
            .languageOfStudy("English")
            .startDate(org.apache.commons.lang.time.DateUtils.addYears(new Date(), -1))
            .subject("Engineering")
            .type(qualificationType)
            .toQualification();
        applicationForm = new ApplicationFormBuilder()
            .id(1)
            .acceptedTerms(CheckedStatus.YES)
            .additionalInformation(additionalInformation)
            .appDate(new Date())
            .applicant(user)
            .applicationAdministrator(user)
            .applicationNumber("TMRMBISING01-2012-000001")
            .approver(user)
            .batchDeadline(org.apache.commons.lang.time.DateUtils.addMonths(new Date(), 1))
            .contactAddress(address)
            .currentAddress(address)
            .dueDate(org.apache.commons.lang.time.DateUtils.addMonths(new Date(), 1))
            .employmentPositions(employmentPosition)
            .lastUpdated(new Date())
            .personalDetails(personalDetails)
            .program(program)
            .programmeDetails(programDetails)
            .projectTitle("Project Title")
            .qualification(qualification)
            .status(ApplicationFormStatus.APPROVED)
            .submittedDate(new Date())
            .toApplicationForm();
    }
}
