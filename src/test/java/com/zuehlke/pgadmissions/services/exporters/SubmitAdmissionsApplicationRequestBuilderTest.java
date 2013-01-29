package com.zuehlke.pgadmissions.services.exporters;

import java.io.StringWriter;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.ApplicationForm;

public class SubmitAdmissionsApplicationRequestBuilderTest extends UclIntegrationBaseTest {

    private ApplicationForm applicationForm;

    private String requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><SubmitAdmissionsApplicationRequest xmlns:ns2=\"http://ucl.ac.uk/isd/uclbasic_v1_0/\" xmlns=\"http://ucl.ac.uk/isd/registry/studentrecordsdata_V1.0\" xmlns:ns3=\"http://ucl.ac.uk/isd/registry/basictypes_v1\"><application><source>PRISM</source><applicant><ns2:fullName><ns2:title>Mr</ns2:title><ns2:surname>Denver</ns2:surname><ns2:forename1>Kevin</ns2:forename1></ns2:fullName><ns2:sex>M</ns2:sex><dateOfBirth>1985-01-29Z</dateOfBirth><nationality><ns2:code>GB</ns2:code><ns2:name>England</ns2:name></nationality><countryOfDomicile><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></countryOfDomicile><countryOfBirth><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></countryOfBirth><passport><ns2:number>000</ns2:number><ns2:name>Kevin Francis Denver</ns2:name><ns2:issueDate>2003-01-29Z</ns2:issueDate><ns2:expiryDate>2033-01-29Z</ns2:expiryDate></passport><visaRequired>true</visaRequired><disability><ns2:code>0</ns2:code><ns2:name>No Disability</ns2:name></disability><ethnicity><ns2:code>10</ns2:code><ns2:name>White</ns2:name></ethnicity><homeAddress><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London W1T 4HD</ns2:addressLine3><ns2:addressLine4>United Kingdom</ns2:addressLine4><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></homeAddress><correspondenceAddress><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London W1T 4HD</ns2:addressLine3><ns2:addressLine4>United Kingdom</ns2:addressLine4><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></correspondenceAddress><criminalConvictions>false</criminalConvictions><qualificationList><qualificationDetail><institution><code>UK0000</code><country><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></country></institution><startDate>2012-01-29Z</startDate><endDate>2013-01-29Z</endDate><qualification><code>DEGTRE</code><name>Bachelors Degree - France</name></qualification><grade>6</grade><mainSubject>Engineering</mainSubject><languageOfInstruction>English</languageOfInstruction></qualificationDetail></qualificationList><englishIsFirstLanguage>true</englishIsFirstLanguage><englishLanguageQualificationList><englishLanguageQualification><languageExam>OTHER</languageExam><otherLanguageExam>FooBar</otherLanguageExam><dateTaken>2013-01-29Z</dateTaken><languageScore><name>OVERALL</name><score>1</score></languageScore><languageScore><name>READING</name><score>1</score></languageScore><languageScore><name>WRITING</name><score>1</score></languageScore><languageScore><name>ESSAY</name><score>1</score></languageScore><languageScore><name>SPEAKING</name><score>1</score></languageScore><languageScore><name>LISTENING</name><score>1</score></languageScore></englishLanguageQualification></englishLanguageQualificationList><employerList><employer><ns2:employer><ns2:name>Zuhlke Ltd.</ns2:name></ns2:employer><ns2:jobTitle>Software Engineer</ns2:jobTitle><ns2:startDate>2011-01-29Z</ns2:startDate><ns2:responsibilities>Developer</ns2:responsibilities></employer></employerList></applicant><courseApplication><externalApplicationID>TMRMBISING01-2012-999999</externalApplicationID><programme><code>TMRMBISING01</code><modeOfAttendance><code>F+++++</code><name>Full-time</name></modeOfAttendance><identifier>0009</identifier><academicYear>2013</academicYear><startDate>2013-06-29+01:00</startDate><endDate>2014-01-29Z</endDate></programme><startMonth>--01</startMonth><personalStatement>Refer to attached document.</personalStatement><sourcesOfInterest><code>BRIT_COUN</code><name>British Council</name></sourcesOfInterest><ipAddress>127.0.0.1</ipAddress><creationDate></creationDate><applicationStatus>ACTIVE</applicationStatus><departmentalDecision>OFFER</departmentalDecision><refereeList><referee><name><ns2:surname>Smith</ns2:surname><ns2:forename1>Bob</ns2:forename1></name><position>Software Engineer</position><contactDetails><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London W1T 4HD</ns2:addressLine3><ns2:addressLine4>United Kingdom</ns2:addressLine4><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked1@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></contactDetails></referee><referee><name><ns2:surname>Smith</ns2:surname><ns2:forename1>Bob</ns2:forename1></name><position>Software Engineer</position><contactDetails><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London W1T 4HD</ns2:addressLine3><ns2:addressLine4>United Kingdom</ns2:addressLine4><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked2@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></contactDetails></referee></refereeList></courseApplication></application></SubmitAdmissionsApplicationRequest>";

    @Before
    public void prepare() {
        applicationForm = getValidApplicationForm();
    }

    @Test
    public void shouldBuildValidWebServiceRequest() throws JAXBException, DatatypeConfigurationException {
        Date submittedDate = new Date();
        applicationForm.setSubmittedDate(submittedDate);
        
        XMLGregorianCalendar submittedDateXml = buildXmlDate(submittedDate);
        
        SubmitAdmissionsApplicationRequestBuilder requestBuilder = new SubmitAdmissionsApplicationRequestBuilder(new ObjectFactory());
        SubmitAdmissionsApplicationRequest request = requestBuilder.applicationForm(applicationForm).build();

        JAXBContext context = JAXBContext.newInstance(SubmitAdmissionsApplicationRequest.class);
        Marshaller marshaller = context.createMarshaller();

        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(request, stringWriter);

        Assert.assertEquals(requestXml.replace("<creationDate></creationDate>", "<creationDate>" + submittedDateXml.toXMLFormat() + "</creationDate>"), stringWriter.toString());
    }
    
    private XMLGregorianCalendar buildXmlDate(Date date) throws DatatypeConfigurationException {
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(date.getTime());
        return datatypeFactory.newXMLGregorianCalendar(gc);
    }
}
