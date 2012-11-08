package com.zuehlke.pgadmissions.services.uclexport;

import java.util.Date;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
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
public class UclExportServiceImplTest {

    private ApplicationForm applicationForm;

    @Autowired
    UclExportService exportService;
    
    @Test
    @Ignore
    public void shouldSendToUcl() {
        exportService.sendToUCL(applicationForm);
    }
    
    @Before
    public void setup() {
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
        Language language = new LanguageBuilder().code("GB").name("England").toLanguage();
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
            .passportInformation(new PassportInformationBuilder().passportNumber("000").nameOnPassport("Kevin Francis Denver").passportExpiryDate(org.apache.commons.lang.time.DateUtils.addYears(new Date(), 20)).passportIssueDate(org.apache.commons.lang.time.DateUtils.addYears(new Date(), -10)).toPassportInformation())
            .languageQualificationAvailable(true)
            .languageQualifications(new LanguageQualificationBuilder().dateOfExamination(new Date()).examTakenOnline(false).languageQualification(LanguageQualificationEnum.OTHER).listeningScore("1").otherQualificationTypeName("FooBar").overallScore("1").readingScore("1").speakingScore("1").writingScore("1").toLanguageQualification())
            .phoneNumber("+44 (0) 123 123 1234")
            .residenceDomicile(domicile)
            .title(Title.MR)
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
            .institution("AF0001")
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
