package com.zuehlke.pgadmissions.services.exporters;

import java.util.Date;

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

public class UclIntegrationBaseTest extends AutomaticRollbackTestCase {

    protected String uclUserId = "ucl-user-AX78101";
    
    protected String uclBookingReferenceNumber = "b-ref-123456";
    
    protected String sftpHost = "localhost";

    protected String sftpPort = "22";

    protected String sftpUsername = "foo";

    protected String sftpPassword = "bar";

    protected String targetFolder = "/home/prism";
    
    protected int consecutiveSoapFaultsLimit = 5;

    protected int queuePausingDelayInCaseOfNetworkProblemsDiscovered = 15;
    
    
    public ApplicationForm getValidApplicationForm() {
        String addressStr = "Zuhlke Engineering Ltd\n43 Whitfield Street\nLondon W1T 4HD\nUnited Kingdom";
        RegisteredUser user = new RegisteredUserBuilder().id(Integer.MAX_VALUE).username("denk@zhaw.ch").enabled(true).toUser();
        RegisteredUser approverUser = new RegisteredUserBuilder().id(Integer.MAX_VALUE-1).username("approver@zhaw.ch").enabled(true).toUser();
        Country country = new CountryBuilder().id(Integer.MAX_VALUE).code("XK").name("United Kingdom").enabled(true).toCountry();
        Address address = new AddressBuilder().id(Integer.MAX_VALUE).country(country).address1(addressStr.split("\n")[0]).address2(addressStr.split("\n")[1]).address3(addressStr.split("\n")[2]).address4(addressStr.split("\n")[3]).toAddress();
        EmploymentPosition employmentPosition = new EmploymentPositionBuilder()
            .current(true)
            .employerAdress1(addressStr)
            .employerCountry(country)
            .employerName("Zuhlke Ltd.")
            .toEmploymentPosition();
        Language language = new LanguageBuilder().id(Integer.MAX_VALUE).code("GB").name("England").enabled(true).toLanguage();
        Disability disability = new DisabilityBuilder().id(Integer.MAX_VALUE).code(0).name("No Disability").enabled(true).toDisability();
        Ethnicity ethnicity = new EthnicityBuilder().id(Integer.MAX_VALUE).code(10).name("White").enabled(true).toEthnicity();
        Domicile domicile = new DomicileBuilder().id(Integer.MAX_VALUE).code("XK").name("United Kingdom").enabled(true).toDomicile();
        PersonalDetails personalDetails = new PersonalDetailsBuilder()
            .id(Integer.MAX_VALUE)
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
            .id(Integer.MAX_VALUE)
            .setConvictions(false)
            .toAdditionalInformation();
        ProgramInstance instance = new ProgramInstanceBuilder()
            .id(Integer.MAX_VALUE)
            .academicYear("2013")
            .applicationDeadline(org.apache.commons.lang.time.DateUtils.addMonths(new Date(), 1))
            .applicationStartDate(org.apache.commons.lang.time.DateUtils.addMonths(new Date(), -1))
            .enabled(true)
            .studyOption("F+++++", "Full-time")
            .identifier("0009")
            .toProgramInstance();
        Program program = new ProgramBuilder()
            .id(Integer.MAX_VALUE)
            .administrators(user)
            .approver(user)
            .code("TMRMBISING99")
            .enabled(true)
            .instances(instance)
            .interviewers(user)
            .reviewers(user)
            .supervisors(user)
            .title("MRes Medical and Biomedical Imaging")
            .toProgram();
        SourcesOfInterest interest = new SourcesOfInterestBuilder().id(Integer.MAX_VALUE).code("BRIT_COUN").name("British Council").toSourcesOfInterest();
        ProgrammeDetails programDetails = new ProgrammeDetailsBuilder()
            .id(Integer.MAX_VALUE)
            .programmeName("MRes Medical and Biomedical Imaging")
            .projectName("Project Title")
            .sourcesOfInterest(interest)
            .startDate(org.apache.commons.lang.time.DateUtils.addDays(new Date(), 1))
            .studyOption("F+++++", "Full-time")
            .toProgrammeDetails();
        QualificationType qualificationType = new QualificationTypeBuilder().id(Integer.MAX_VALUE).code("DEGTRE").name("Bachelors Degree - France").enabled(true).toQualificationTitle();
        Qualification qualification = new QualificationBuilder()
            .id(Integer.MAX_VALUE)
            .awardDate(new Date())
            .grade("6")
            .institution("University of London")
            .institutionCountry(domicile)
            .languageOfStudy("English")
            .startDate(org.apache.commons.lang.time.DateUtils.addYears(new Date(), -1))
            .subject("Engineering")
            .type(qualificationType)
            .isCompleted(CheckedStatus.YES)
            .toQualification();
        ApplicationForm applicationForm = new ApplicationFormBuilder()
            .id(Integer.MAX_VALUE)
            .acceptedTerms(CheckedStatus.YES)
            .additionalInformation(additionalInformation)
            .appDate(new Date())
            .applicant(user)
            .applicationAdministrator(user)
            .applicationNumber("TMRMBISING01-2012-999999")
            .approver(approverUser)
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
        
        save(user, approverUser, language, country, domicile, address, program, applicationForm);
        
        return applicationForm;
    }
}
