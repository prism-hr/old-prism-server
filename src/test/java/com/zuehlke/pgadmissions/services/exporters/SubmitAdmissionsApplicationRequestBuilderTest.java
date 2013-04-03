package com.zuehlke.pgadmissions.services.exporters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.Assert;

import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.zuehlke.pgadmissions.admissionsservice.jaxb.EnglishLanguageScoreTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.EnglishLanguageTp;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.ObjectFactory;
import com.zuehlke.pgadmissions.admissionsservice.jaxb.SubmitAdmissionsApplicationRequest;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ValidApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;

public class SubmitAdmissionsApplicationRequestBuilderTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    private ApplicationForm applicationForm;

    private String requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><SubmitAdmissionsApplicationRequest xmlns:ns2=\"http://ucl.ac.uk/isd/uclbasic_v1_0/\" xmlns=\"http://ucl.ac.uk/isd/registry/studentrecordsdata_V1.0\" xmlns:ns3=\"http://ucl.ac.uk/isd/registry/basictypes_v1\"><application><source>PRISM</source><applicant><ns2:fullName><ns2:title>Mr</ns2:title><ns2:surname>Denver</ns2:surname><ns2:forename1>Kevin</ns2:forename1><ns2:forename2>Franciszek</ns2:forename2><ns2:forename3>Duncan</ns2:forename3></ns2:fullName><ns2:sex>M</ns2:sex><dateOfBirth>2013-01-01Z</dateOfBirth><nationality><ns2:code>GB</ns2:code><ns2:name>England</ns2:name></nationality><countryOfDomicile><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></countryOfDomicile><countryOfBirth><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></countryOfBirth><passport><ns2:number>000</ns2:number><ns2:name>Kevin Francis Denver</ns2:name><ns2:issueDate>2013-01-01Z</ns2:issueDate><ns2:expiryDate>2013-01-01Z</ns2:expiryDate></passport><visaRequired>true</visaRequired><disability><ns2:code>0</ns2:code><ns2:name>No Disability</ns2:name></disability><ethnicity><ns2:code>10</ns2:code><ns2:name>White</ns2:name></ethnicity><homeAddress><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London</ns2:addressLine3><ns2:addressLine4></ns2:addressLine4><ns2:postCode>W1T 4HD</ns2:postCode><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></homeAddress><correspondenceAddress><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London</ns2:addressLine3><ns2:addressLine4></ns2:addressLine4><ns2:postCode>W1T 4HD</ns2:postCode><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></correspondenceAddress><criminalConvictions>false</criminalConvictions><qualificationList><qualificationDetail><institution><code>UK0000</code><name>University of London</name><country><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></country></institution><startDate>2013-01-01Z</startDate><endDate>2013-01-01Z</endDate><qualification><code>DEGTRE</code><name>Bachelors Degree - France</name></qualification><grade>6</grade><mainSubject>Engineering</mainSubject><languageOfInstruction>English</languageOfInstruction></qualificationDetail><qualificationDetail><institution><code>UK0000</code><name>University of London</name><country><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></country></institution><startDate>2013-01-01Z</startDate><endDate>2013-01-01Z</endDate><qualification><code>DEGTRE</code><name>Bachelors Degree - France</name></qualification><grade>6</grade><mainSubject>Engineering</mainSubject><languageOfInstruction>English</languageOfInstruction></qualificationDetail></qualificationList><englishIsFirstLanguage>true</englishIsFirstLanguage><englishLanguageQualificationList><englishLanguageQualification><languageExam>OTHER</languageExam><otherLanguageExam>FooBar</otherLanguageExam><dateTaken>2013-01-01Z</dateTaken><languageScore><name>OVERALL</name><score>1</score></languageScore><languageScore><name>READING</name><score>1</score></languageScore><languageScore><name>WRITING</name><score>1</score></languageScore><languageScore><name>SPEAKING</name><score>1</score></languageScore><languageScore><name>LISTENING</name><score>1</score></languageScore></englishLanguageQualification></englishLanguageQualificationList><employerList><employer><ns2:employer><ns2:name>Zuhlke Ltd.</ns2:name></ns2:employer><ns2:jobTitle>Software Engineer</ns2:jobTitle><ns2:startDate>2013-01-01Z</ns2:startDate><ns2:responsibilities>Developer</ns2:responsibilities></employer></employerList></applicant><courseApplication><externalApplicationID>TMRMBISING01-2012-999999</externalApplicationID><programme><code>TMRMBISING99</code><modeOfAttendance><code>F+++++</code><name>Full-time</name></modeOfAttendance><identifier>0009</identifier><academicYear>2013</academicYear><startDate>2013-01-01Z</startDate><endDate>2013-01-01Z</endDate></programme><startMonth>--01</startMonth><personalStatement>Refer to attached document.</personalStatement><sourcesOfInterest><code>BRIT_COUN</code><name>British Council</name></sourcesOfInterest><ipAddress>127.0.0.1</ipAddress><creationDate>2013-01-01T08:00:00.000Z</creationDate><applicationStatus>ACTIVE</applicationStatus><departmentalDecision>OFFER</departmentalDecision><refereeList><referee><name><ns2:surname>Smith</ns2:surname><ns2:forename1>Bob</ns2:forename1></name><position>Software Engineer</position><contactDetails><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London</ns2:addressLine3><ns2:addressLine4></ns2:addressLine4><ns2:postCode>W1T 4HD</ns2:postCode><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked1@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></contactDetails></referee><referee><name><ns2:surname>Austen</ns2:surname><ns2:forename1>Jane</ns2:forename1></name><position>Software Engineer</position><contactDetails><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London</ns2:addressLine3><ns2:addressLine4></ns2:addressLine4><ns2:postCode>W1T 4HD</ns2:postCode><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked2@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></contactDetails></referee></refereeList></courseApplication></application></SubmitAdmissionsApplicationRequest>";
    
    private String requestXmlWithAtasAndConditionalOffer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><SubmitAdmissionsApplicationRequest xmlns:ns2=\"http://ucl.ac.uk/isd/uclbasic_v1_0/\" xmlns=\"http://ucl.ac.uk/isd/registry/studentrecordsdata_V1.0\" xmlns:ns3=\"http://ucl.ac.uk/isd/registry/basictypes_v1\"><application><source>PRISM</source><applicant><ns2:fullName><ns2:title>Mr</ns2:title><ns2:surname>Denver</ns2:surname><ns2:forename1>Kevin</ns2:forename1><ns2:forename2>Franciszek</ns2:forename2><ns2:forename3>Duncan</ns2:forename3></ns2:fullName><ns2:sex>M</ns2:sex><dateOfBirth>2013-01-01Z</dateOfBirth><nationality><ns2:code>GB</ns2:code><ns2:name>England</ns2:name></nationality><countryOfDomicile><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></countryOfDomicile><countryOfBirth><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></countryOfBirth><passport><ns2:number>000</ns2:number><ns2:name>Kevin Francis Denver</ns2:name><ns2:issueDate>2013-01-01Z</ns2:issueDate><ns2:expiryDate>2013-01-01Z</ns2:expiryDate></passport><visaRequired>true</visaRequired><disability><ns2:code>0</ns2:code><ns2:name>No Disability</ns2:name></disability><ethnicity><ns2:code>10</ns2:code><ns2:name>White</ns2:name></ethnicity><homeAddress><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London</ns2:addressLine3><ns2:addressLine4></ns2:addressLine4><ns2:postCode>W1T 4HD</ns2:postCode><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></homeAddress><correspondenceAddress><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London</ns2:addressLine3><ns2:addressLine4></ns2:addressLine4><ns2:postCode>W1T 4HD</ns2:postCode><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></correspondenceAddress><criminalConvictions>false</criminalConvictions><qualificationList><qualificationDetail><institution><code>UK0000</code><name>University of London</name><country><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></country></institution><startDate>2013-01-01Z</startDate><endDate>2013-01-01Z</endDate><qualification><code>DEGTRE</code><name>Bachelors Degree - France</name></qualification><grade>6</grade><mainSubject>Engineering</mainSubject><languageOfInstruction>English</languageOfInstruction></qualificationDetail><qualificationDetail><institution><code>UK0000</code><name>University of London</name><country><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></country></institution><startDate>2013-01-01Z</startDate><endDate>2013-01-01Z</endDate><qualification><code>DEGTRE</code><name>Bachelors Degree - France</name></qualification><grade>6</grade><mainSubject>Engineering</mainSubject><languageOfInstruction>English</languageOfInstruction></qualificationDetail></qualificationList><englishIsFirstLanguage>true</englishIsFirstLanguage><englishLanguageQualificationList><englishLanguageQualification><languageExam>OTHER</languageExam><otherLanguageExam>FooBar</otherLanguageExam><dateTaken>2013-01-01Z</dateTaken><languageScore><name>OVERALL</name><score>1</score></languageScore><languageScore><name>READING</name><score>1</score></languageScore><languageScore><name>WRITING</name><score>1</score></languageScore><languageScore><name>SPEAKING</name><score>1</score></languageScore><languageScore><name>LISTENING</name><score>1</score></languageScore></englishLanguageQualification></englishLanguageQualificationList><employerList><employer><ns2:employer><ns2:name>Zuhlke Ltd.</ns2:name></ns2:employer><ns2:jobTitle>Software Engineer</ns2:jobTitle><ns2:startDate>2013-01-01Z</ns2:startDate><ns2:responsibilities>Developer</ns2:responsibilities></employer></employerList></applicant><courseApplication><externalApplicationID>TMRMBISING01-2012-999999</externalApplicationID><programme><code>TMRMBISING99</code><modeOfAttendance><code>F+++++</code><name>Full-time</name></modeOfAttendance><identifier>0009</identifier><academicYear>2013</academicYear><startDate>2013-01-01Z</startDate><endDate>2013-01-01Z</endDate></programme><startMonth>--01</startMonth><personalStatement>Refer to attached document.</personalStatement><sourcesOfInterest><code>BRIT_COUN</code><name>British Council</name></sourcesOfInterest><atasStatement>abstract</atasStatement><ipAddress>127.0.0.1</ipAddress><creationDate>2013-01-01T08:00:00.000Z</creationDate><applicationStatus>ACTIVE</applicationStatus><departmentalDecision>OFFER</departmentalDecision><departmentalOfferConditions>Conditional Offer: conditions</departmentalOfferConditions><refereeList><referee><name><ns2:surname>Smith</ns2:surname><ns2:forename1>Bob</ns2:forename1></name><position>Software Engineer</position><contactDetails><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London</ns2:addressLine3><ns2:addressLine4></ns2:addressLine4><ns2:postCode>W1T 4HD</ns2:postCode><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked1@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></contactDetails></referee><referee><name><ns2:surname>Austen</ns2:surname><ns2:forename1>Jane</ns2:forename1></name><position>Software Engineer</position><contactDetails><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London</ns2:addressLine3><ns2:addressLine4></ns2:addressLine4><ns2:postCode>W1T 4HD</ns2:postCode><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked2@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></contactDetails></referee></refereeList></courseApplication></application></SubmitAdmissionsApplicationRequest>";
    
    private String requestXmlWithoutAtasAndUnconditionalOffer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><SubmitAdmissionsApplicationRequest xmlns:ns2=\"http://ucl.ac.uk/isd/uclbasic_v1_0/\" xmlns=\"http://ucl.ac.uk/isd/registry/studentrecordsdata_V1.0\" xmlns:ns3=\"http://ucl.ac.uk/isd/registry/basictypes_v1\"><application><source>PRISM</source><applicant><ns2:fullName><ns2:title>Mr</ns2:title><ns2:surname>Denver</ns2:surname><ns2:forename1>Kevin</ns2:forename1><ns2:forename2>Franciszek</ns2:forename2><ns2:forename3>Duncan</ns2:forename3></ns2:fullName><ns2:sex>M</ns2:sex><dateOfBirth>2013-01-01Z</dateOfBirth><nationality><ns2:code>GB</ns2:code><ns2:name>England</ns2:name></nationality><countryOfDomicile><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></countryOfDomicile><countryOfBirth><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></countryOfBirth><passport><ns2:number>000</ns2:number><ns2:name>Kevin Francis Denver</ns2:name><ns2:issueDate>2013-01-01Z</ns2:issueDate><ns2:expiryDate>2013-01-01Z</ns2:expiryDate></passport><visaRequired>true</visaRequired><disability><ns2:code>0</ns2:code><ns2:name>No Disability</ns2:name></disability><ethnicity><ns2:code>10</ns2:code><ns2:name>White</ns2:name></ethnicity><homeAddress><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London</ns2:addressLine3><ns2:addressLine4></ns2:addressLine4><ns2:postCode>W1T 4HD</ns2:postCode><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></homeAddress><correspondenceAddress><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London</ns2:addressLine3><ns2:addressLine4></ns2:addressLine4><ns2:postCode>W1T 4HD</ns2:postCode><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></correspondenceAddress><criminalConvictions>false</criminalConvictions><qualificationList><qualificationDetail><institution><code>UK0000</code><name>University of London</name><country><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></country></institution><startDate>2013-01-01Z</startDate><endDate>2013-01-01Z</endDate><qualification><code>DEGTRE</code><name>Bachelors Degree - France</name></qualification><grade>6</grade><mainSubject>Engineering</mainSubject><languageOfInstruction>English</languageOfInstruction></qualificationDetail><qualificationDetail><institution><code>UK0000</code><name>University of London</name><country><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></country></institution><startDate>2013-01-01Z</startDate><endDate>2013-01-01Z</endDate><qualification><code>DEGTRE</code><name>Bachelors Degree - France</name></qualification><grade>6</grade><mainSubject>Engineering</mainSubject><languageOfInstruction>English</languageOfInstruction></qualificationDetail></qualificationList><englishIsFirstLanguage>true</englishIsFirstLanguage><englishLanguageQualificationList><englishLanguageQualification><languageExam>OTHER</languageExam><otherLanguageExam>FooBar</otherLanguageExam><dateTaken>2013-01-01Z</dateTaken><languageScore><name>OVERALL</name><score>1</score></languageScore><languageScore><name>READING</name><score>1</score></languageScore><languageScore><name>WRITING</name><score>1</score></languageScore><languageScore><name>SPEAKING</name><score>1</score></languageScore><languageScore><name>LISTENING</name><score>1</score></languageScore></englishLanguageQualification></englishLanguageQualificationList><employerList><employer><ns2:employer><ns2:name>Zuhlke Ltd.</ns2:name></ns2:employer><ns2:jobTitle>Software Engineer</ns2:jobTitle><ns2:startDate>2013-01-01Z</ns2:startDate><ns2:responsibilities>Developer</ns2:responsibilities></employer></employerList></applicant><courseApplication><externalApplicationID>TMRMBISING01-2012-999999</externalApplicationID><programme><code>TMRMBISING99</code><modeOfAttendance><code>F+++++</code><name>Full-time</name></modeOfAttendance><identifier>0009</identifier><academicYear>2013</academicYear><startDate>2013-01-01Z</startDate><endDate>2013-01-01Z</endDate></programme><startMonth>--01</startMonth><personalStatement>Refer to attached document.</personalStatement><sourcesOfInterest><code>BRIT_COUN</code><name>British Council</name></sourcesOfInterest><ipAddress>127.0.0.1</ipAddress><creationDate>2013-01-01T08:00:00.000Z</creationDate><applicationStatus>ACTIVE</applicationStatus><departmentalDecision>OFFER</departmentalDecision><departmentalOfferConditions>Unconditional Offer</departmentalOfferConditions><refereeList><referee><name><ns2:surname>Smith</ns2:surname><ns2:forename1>Bob</ns2:forename1></name><position>Software Engineer</position><contactDetails><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London</ns2:addressLine3><ns2:addressLine4></ns2:addressLine4><ns2:postCode>W1T 4HD</ns2:postCode><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked1@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></contactDetails></referee><referee><name><ns2:surname>Austen</ns2:surname><ns2:forename1>Jane</ns2:forename1></name><position>Software Engineer</position><contactDetails><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London</ns2:addressLine3><ns2:addressLine4></ns2:addressLine4><ns2:postCode>W1T 4HD</ns2:postCode><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked2@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></contactDetails></referee></refereeList></courseApplication></application></SubmitAdmissionsApplicationRequest>";

    private String requestXmlToefl = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><SubmitAdmissionsApplicationRequest xmlns:ns2=\"http://ucl.ac.uk/isd/uclbasic_v1_0/\" xmlns=\"http://ucl.ac.uk/isd/registry/studentrecordsdata_V1.0\" xmlns:ns3=\"http://ucl.ac.uk/isd/registry/basictypes_v1\"><application><source>PRISM</source><applicant><ns2:fullName><ns2:title>Mr</ns2:title><ns2:surname>Denver</ns2:surname><ns2:forename1>Kevin</ns2:forename1><ns2:forename2>Franciszek</ns2:forename2><ns2:forename3>Duncan</ns2:forename3></ns2:fullName><ns2:sex>M</ns2:sex><dateOfBirth>2013-01-01Z</dateOfBirth><nationality><ns2:code>GB</ns2:code><ns2:name>England</ns2:name></nationality><countryOfDomicile><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></countryOfDomicile><countryOfBirth><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></countryOfBirth><passport><ns2:number>000</ns2:number><ns2:name>Kevin Francis Denver</ns2:name><ns2:issueDate>2013-01-01Z</ns2:issueDate><ns2:expiryDate>2013-01-01Z</ns2:expiryDate></passport><visaRequired>true</visaRequired><disability><ns2:code>0</ns2:code><ns2:name>No Disability</ns2:name></disability><ethnicity><ns2:code>10</ns2:code><ns2:name>White</ns2:name></ethnicity><homeAddress><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London</ns2:addressLine3><ns2:addressLine4></ns2:addressLine4><ns2:postCode>W1T 4HD</ns2:postCode><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></homeAddress><correspondenceAddress><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London</ns2:addressLine3><ns2:addressLine4></ns2:addressLine4><ns2:postCode>W1T 4HD</ns2:postCode><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></correspondenceAddress><criminalConvictions>false</criminalConvictions><qualificationList><qualificationDetail><institution><code>UK0000</code><name>University of London</name><country><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></country></institution><startDate>2013-01-01Z</startDate><endDate>2013-01-01Z</endDate><qualification><code>DEGTRE</code><name>Bachelors Degree - France</name></qualification><grade>6</grade><mainSubject>Engineering</mainSubject><languageOfInstruction>English</languageOfInstruction></qualificationDetail><qualificationDetail><institution><code>UK0000</code><name>University of London</name><country><ns2:code>XK</ns2:code><ns2:name>United Kingdom</ns2:name></country></institution><startDate>2013-01-01Z</startDate><endDate>2013-01-01Z</endDate><qualification><code>DEGTRE</code><name>Bachelors Degree - France</name></qualification><grade>6</grade><mainSubject>Engineering</mainSubject><languageOfInstruction>English</languageOfInstruction></qualificationDetail></qualificationList><englishIsFirstLanguage>true</englishIsFirstLanguage><englishLanguageQualificationList><englishLanguageQualification><languageExam>TOEFL</languageExam><method>TOEFL_PAPER</method><dateTaken>2013-01-01Z</dateTaken><languageScore><name>OVERALL</name><score>1</score></languageScore><languageScore><name>READING</name><score>1</score></languageScore><languageScore><name>WRITING</name><score>1</score></languageScore><languageScore><name>ESSAY</name><score>1</score></languageScore><languageScore><name>LISTENING</name><score>1</score></languageScore></englishLanguageQualification></englishLanguageQualificationList><employerList><employer><ns2:employer><ns2:name>Zuhlke Ltd.</ns2:name></ns2:employer><ns2:jobTitle>Software Engineer</ns2:jobTitle><ns2:startDate>2013-01-01Z</ns2:startDate><ns2:responsibilities>Developer</ns2:responsibilities></employer></employerList></applicant><courseApplication><externalApplicationID>TMRMBISING01-2012-999999</externalApplicationID><programme><code>TMRMBISING99</code><modeOfAttendance><code>F+++++</code><name>Full-time</name></modeOfAttendance><identifier>0009</identifier><academicYear>2013</academicYear><startDate>2013-01-01Z</startDate><endDate>2013-01-01Z</endDate></programme><startMonth>--01</startMonth><personalStatement>Refer to attached document.</personalStatement><sourcesOfInterest><code>BRIT_COUN</code><name>British Council</name></sourcesOfInterest><ipAddress>127.0.0.1</ipAddress><creationDate>2013-01-01T08:00:00.000Z</creationDate><applicationStatus>ACTIVE</applicationStatus><departmentalDecision>OFFER</departmentalDecision><refereeList><referee><name><ns2:surname>Smith</ns2:surname><ns2:forename1>Bob</ns2:forename1></name><position>Software Engineer</position><contactDetails><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London</ns2:addressLine3><ns2:addressLine4></ns2:addressLine4><ns2:postCode>W1T 4HD</ns2:postCode><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked1@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></contactDetails></referee><referee><name><ns2:surname>Austen</ns2:surname><ns2:forename1>Jane</ns2:forename1></name><position>Software Engineer</position><contactDetails><ns3:addressDtls><ns2:addressLine1>Zuhlke Engineering Ltd</ns2:addressLine1><ns2:addressLine2>43 Whitfield Street</ns2:addressLine2><ns2:addressLine3>London</ns2:addressLine3><ns2:addressLine4></ns2:addressLine4><ns2:postCode>W1T 4HD</ns2:postCode><ns2:country>XK</ns2:country></ns3:addressDtls><ns3:email>ked2@zuhlke.com</ns3:email><ns3:landline>+44 (0) 123 123 1234</ns3:landline></contactDetails></referee></refereeList></courseApplication></application></SubmitAdmissionsApplicationRequest>";

    private SubmitAdmissionsApplicationRequestBuilder requestBuilder;
    
    @Before
    public void prepare() {
        applicationForm = new ValidApplicationFormBuilder().build();
        requestBuilder = new SubmitAdmissionsApplicationRequestBuilder(new ObjectFactory());
    }
    
    @Test
    public void shouldThrowExceptionIfApplicationIsInReviewState() {
        applicationForm.setStatus(ApplicationFormStatus.REVIEW);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(CoreMatchers.equalTo("Application is in wrong state " + ApplicationFormStatus.REVIEW.displayValue()));
        requestBuilder.applicationForm(applicationForm).build();
    }
    
    @Test
    public void shouldThrowExceptionIfApplicationIsInUnsubmittedState() {
        applicationForm.setStatus(ApplicationFormStatus.UNSUBMITTED);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(CoreMatchers.equalTo("Application is in wrong state " + ApplicationFormStatus.UNSUBMITTED.displayValue()));
        requestBuilder.applicationForm(applicationForm).build();
    }
    
    @Test
    public void shouldThrowExceptionIfApplicationIsInApprovalState() {
        applicationForm.setStatus(ApplicationFormStatus.APPROVAL);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(CoreMatchers.equalTo("Application is in wrong state " + ApplicationFormStatus.APPROVAL.displayValue()));
        requestBuilder.applicationForm(applicationForm).build();
    }
    
    @Test
    public void shouldThrowExceptionIfApplicationIsInValidationState() {
        applicationForm.setStatus(ApplicationFormStatus.VALIDATION);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(CoreMatchers.equalTo("Application is in wrong state " + ApplicationFormStatus.VALIDATION.displayValue()));
        requestBuilder.applicationForm(applicationForm).build();
    }
    
    @Test
    public void shouldThrowExceptionIfApplicationIsInRequestRestartApprovalState() {
        applicationForm.setStatus(ApplicationFormStatus.REQUEST_RESTART_APPROVAL);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(CoreMatchers.equalTo("Application is in wrong state " + ApplicationFormStatus.REQUEST_RESTART_APPROVAL.displayValue()));
        requestBuilder.applicationForm(applicationForm).build();
    }
    
    @Test
    public void shouldThrowExceptionIfApplicationIsInRequestRestartInterviewState() {
        applicationForm.setStatus(ApplicationFormStatus.INTERVIEW);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(CoreMatchers.equalTo("Application is in wrong state " + ApplicationFormStatus.INTERVIEW.displayValue()));
        requestBuilder.applicationForm(applicationForm).build();
    }
    
    @Test
    public void shouldThrowExceptionIfNoActiveProgramFound () {
        applicationForm.getProgrammeDetails().setStudyOption("AAA");
        exception.expect(IllegalArgumentException.class);
        requestBuilder.applicationForm(applicationForm).build();
    }
    
    @Test
    public void shouldSetAdditionalTextInOfferConditionsIfLanguageQualificationIsNull() {
        applicationForm.getPersonalDetails().setLanguageQualificationAvailable(null);
        applicationForm.getPersonalDetails().setEnglishFirstLanguage(null);
        SubmitAdmissionsApplicationRequest request = requestBuilder.applicationForm(applicationForm).build();
        assertEquals("Application predates mandatory language qualification. Please check qualifications for potential language certificates.", request.getApplication().getCourseApplication().getDepartmentalOfferConditions());
    }
    
    @Test
    public void shouldSetDepartmentalDecisionToOffer() {
        applicationForm.setStatus(ApplicationFormStatus.APPROVED);
        SubmitAdmissionsApplicationRequest request = requestBuilder.applicationForm(applicationForm).build();
        assertEquals("ACTIVE", request.getApplication().getCourseApplication().getApplicationStatus());
        assertEquals("OFFER", request.getApplication().getCourseApplication().getDepartmentalDecision());
    }
    
    @Test
    public void shouldSetDepartmentalDecisionToReject() {
        applicationForm.setStatus(ApplicationFormStatus.REJECTED);
        SubmitAdmissionsApplicationRequest request = requestBuilder.applicationForm(applicationForm).build();
        assertEquals("ACTIVE", request.getApplication().getCourseApplication().getApplicationStatus());
        assertEquals("REJECT", request.getApplication().getCourseApplication().getDepartmentalDecision());
    }
    
    @Test
    public void shouldNotSetDepartmentalDecisionWhenWithdrawn() {
        applicationForm.setStatus(ApplicationFormStatus.WITHDRAWN);
        SubmitAdmissionsApplicationRequest request = requestBuilder.applicationForm(applicationForm).build();
        assertEquals("WITHDRAWN", request.getApplication().getCourseApplication().getApplicationStatus());
        assertNull(request.getApplication().getCourseApplication().getDepartmentalDecision());
    }
    
    @Test
    public void shouldNotSendDecimalValueForLanguageScores() {
        applicationForm.setStatus(ApplicationFormStatus.WITHDRAWN);
        for (LanguageQualification qualification : applicationForm.getPersonalDetails().getLanguageQualifications()) {
            qualification.setListeningScore("4.0");
            qualification.setOverallScore("4.5");
            qualification.setReadingScore("4.0");
            qualification.setSpeakingScore("5.0");
            qualification.setWritingScore("6.5");
        }
        SubmitAdmissionsApplicationRequest request = requestBuilder.applicationForm(applicationForm).build();
        EnglishLanguageTp englishLanguageTp = request.getApplication().getApplicant().getEnglishLanguageQualificationList().getEnglishLanguageQualification().get(0);
        for (EnglishLanguageScoreTp score : englishLanguageTp.getLanguageScore()) {
            switch(score.getName()) {
            case ESSAY:
                fail("ESSAY should not be present in this case. Only for TOEFL_PAPER which then replaces SPEAKING.");
                break;
            case LISTENING:
                assertEquals("4", score.getScore());
                break;
            case OTHER:
                break;
            case OVERALL:
                assertEquals("4.5", score.getScore());
                break;
            case READING:
                assertEquals("4", score.getScore());
                break;
            case SPEAKING:
                assertEquals("5", score.getScore());
                break;
            case WRITING:
                assertEquals("6.5", score.getScore());
                break;
            default:
                break;
            }
        }
    }
    
    @Test
    public void shouldBuildValidWebServiceRequestWithTOEFLEssaySection() throws JAXBException, DatatypeConfigurationException {
        final DateTime dateInThePast = new DateTime(2013, 1, 1, 8, 0);
        applicationForm.getProgrammeDetails().setStartDate(dateInThePast.toDate());
        
        SubmitAdmissionsApplicationRequestBuilder requestBuilder = new SubmitAdmissionsApplicationRequestBuilder(new ObjectFactory()) {
            @Override
            protected XMLGregorianCalendar buildXmlDate(Date date) {
                if (date != null) {
                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTimeInMillis(dateInThePast.toDate().getTime());
                    return datatypeFactory.newXMLGregorianCalendar(gc);
                }
                return null;
            }
            
            @Override
            protected XMLGregorianCalendar buildXmlDateYearOnly(String date) {
                if (date != null) {
                    XMLGregorianCalendar xmlCalendar = datatypeFactory.newXMLGregorianCalendar();
                    xmlCalendar.setYear(dateInThePast.getYear());
                    return xmlCalendar;
                }
                return null;
            }
            
            @Override
            protected XMLGregorianCalendar buildXmlDateYearOnly(Date date) {
                if (date != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateInThePast.toDate());
                    XMLGregorianCalendar xmlCalendar = datatypeFactory.newXMLGregorianCalendar();
                    xmlCalendar.setYear(cal.get(Calendar.YEAR));
                    return xmlCalendar;
                }
                return null;
            }
        };
        
        for (LanguageQualification qualification : applicationForm.getPersonalDetails().getLanguageQualifications()) {
            qualification.setQualificationType(LanguageQualificationEnum.TOEFL);
        }
        
        SubmitAdmissionsApplicationRequest request = requestBuilder.applicationForm(applicationForm).build();
        
        JAXBContext context = JAXBContext.newInstance(SubmitAdmissionsApplicationRequest.class);
        Marshaller marshaller = context.createMarshaller();

        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(request, stringWriter);

        Assert.assertEquals(requestXmlToefl, stringWriter.toString());
    }

    @Test
    public void shouldBuildValidWebServiceRequest() throws JAXBException, DatatypeConfigurationException {
        final DateTime dateInThePast = new DateTime(2013, 1, 1, 8, 0);
        applicationForm.getProgrammeDetails().setStartDate(dateInThePast.toDate());
        
        SubmitAdmissionsApplicationRequestBuilder requestBuilder = new SubmitAdmissionsApplicationRequestBuilder(new ObjectFactory()) {
            @Override
            protected XMLGregorianCalendar buildXmlDate(Date date) {
                if (date != null) {
                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTimeInMillis(dateInThePast.toDate().getTime());
                    return datatypeFactory.newXMLGregorianCalendar(gc);
                }
                return null;
            }
            
            @Override
            protected XMLGregorianCalendar buildXmlDateYearOnly(String date) {
                if (date != null) {
                    XMLGregorianCalendar xmlCalendar = datatypeFactory.newXMLGregorianCalendar();
                    xmlCalendar.setYear(dateInThePast.getYear());
                    return xmlCalendar;
                }
                return null;
            }
            
            @Override
            protected XMLGregorianCalendar buildXmlDateYearOnly(Date date) {
                if (date != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateInThePast.toDate());
                    XMLGregorianCalendar xmlCalendar = datatypeFactory.newXMLGregorianCalendar();
                    xmlCalendar.setYear(cal.get(Calendar.YEAR));
                    return xmlCalendar;
                }
                return null;
            }
        };
        
        SubmitAdmissionsApplicationRequest request = requestBuilder.applicationForm(applicationForm).build();
        
        JAXBContext context = JAXBContext.newInstance(SubmitAdmissionsApplicationRequest.class);
        Marshaller marshaller = context.createMarshaller();

        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(request, stringWriter);

        Assert.assertEquals(requestXml, stringWriter.toString());
    }
    
    @Test
    public void shouldBuildValidWebServiceRequestContainingAtasStatement() throws JAXBException, DatatypeConfigurationException {
        final DateTime dateInThePast = new DateTime(2013, 1, 1, 8, 0);
        applicationForm.getProgrammeDetails().setStartDate(dateInThePast.toDate());
        ApprovalRound appRound = new ApprovalRoundBuilder().id(15).projectAbstract("abstract").recommendedConditionsAvailable(true).recommendedConditions("conditions").build();
        applicationForm.setLatestApprovalRound(appRound);
        applicationForm.getProgram().setAtasRequired(true);
        
        
        SubmitAdmissionsApplicationRequestBuilder requestBuilder = new SubmitAdmissionsApplicationRequestBuilder(new ObjectFactory()) {
            @Override
            protected XMLGregorianCalendar buildXmlDate(Date date) {
                if (date != null) {
                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTimeInMillis(dateInThePast.toDate().getTime());
                    return datatypeFactory.newXMLGregorianCalendar(gc);
                }
                return null;
            }
            
            @Override
            protected XMLGregorianCalendar buildXmlDateYearOnly(String date) {
                if (date != null) {
                    XMLGregorianCalendar xmlCalendar = datatypeFactory.newXMLGregorianCalendar();
                    xmlCalendar.setYear(dateInThePast.getYear());
                    return xmlCalendar;
                }
                return null;
            }
            
            @Override
            protected XMLGregorianCalendar buildXmlDateYearOnly(Date date) {
                if (date != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateInThePast.toDate());
                    XMLGregorianCalendar xmlCalendar = datatypeFactory.newXMLGregorianCalendar();
                    xmlCalendar.setYear(cal.get(Calendar.YEAR));
                    return xmlCalendar;
                }
                return null;
            }
        };
        requestBuilder.isOverseasStudent(true);
        
        SubmitAdmissionsApplicationRequest request = requestBuilder.applicationForm(applicationForm).build();
        
        JAXBContext context = JAXBContext.newInstance(SubmitAdmissionsApplicationRequest.class);
        Marshaller marshaller = context.createMarshaller();
        
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(request, stringWriter);
        
        System.out.println(stringWriter.toString());
        Assert.assertEquals(requestXmlWithAtasAndConditionalOffer, stringWriter.toString());
    }
    
    @Test
    public void shouldBuildValidWebServiceRequestNotContainingAtasStatement() throws JAXBException, DatatypeConfigurationException {
        final DateTime dateInThePast = new DateTime(2013, 1, 1, 8, 0);
        applicationForm.getProgrammeDetails().setStartDate(dateInThePast.toDate());
        ApprovalRound appRound = new ApprovalRoundBuilder().id(15).projectAbstract("abstract").recommendedConditionsAvailable(false).build();
        applicationForm.setLatestApprovalRound(appRound);
        applicationForm.getProgram().setAtasRequired(false);
        
        
        SubmitAdmissionsApplicationRequestBuilder requestBuilder = new SubmitAdmissionsApplicationRequestBuilder(new ObjectFactory()) {
            @Override
            protected XMLGregorianCalendar buildXmlDate(Date date) {
                if (date != null) {
                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTimeInMillis(dateInThePast.toDate().getTime());
                    return datatypeFactory.newXMLGregorianCalendar(gc);
                }
                return null;
            }
            
            @Override
            protected XMLGregorianCalendar buildXmlDateYearOnly(String date) {
                if (date != null) {
                    XMLGregorianCalendar xmlCalendar = datatypeFactory.newXMLGregorianCalendar();
                    xmlCalendar.setYear(dateInThePast.getYear());
                    return xmlCalendar;
                }
                return null;
            }
            
            @Override
            protected XMLGregorianCalendar buildXmlDateYearOnly(Date date) {
                if (date != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateInThePast.toDate());
                    XMLGregorianCalendar xmlCalendar = datatypeFactory.newXMLGregorianCalendar();
                    xmlCalendar.setYear(cal.get(Calendar.YEAR));
                    return xmlCalendar;
                }
                return null;
            }
        };
        requestBuilder.isOverseasStudent(true);
        
        SubmitAdmissionsApplicationRequest request = requestBuilder.applicationForm(applicationForm).build();
        
        JAXBContext context = JAXBContext.newInstance(SubmitAdmissionsApplicationRequest.class);
        Marshaller marshaller = context.createMarshaller();
        
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(request, stringWriter);
        
        System.out.println(stringWriter.toString());
        Assert.assertEquals(requestXmlWithoutAtasAndUnconditionalOffer, stringWriter.toString());
    }
    
    
}
