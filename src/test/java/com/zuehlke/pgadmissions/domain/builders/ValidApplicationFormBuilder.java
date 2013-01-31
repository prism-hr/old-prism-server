package com.zuehlke.pgadmissions.domain.builders;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.SessionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.domain.enums.Title;

public class ValidApplicationFormBuilder {

    protected RegisteredUser user;
    protected Document cvDocument;
    protected Document referenceDocument;
    protected Document personalStatement;
    protected Document proofOfAwardDocument;
    protected Document languageQualificationDocument;
    protected Document fundingDocument;
    protected RegisteredUser approverUser;
    protected Country country;
    protected Address address;
    protected ReferenceComment referenceComment1;
    protected ReferenceComment referenceComment2;
    protected Referee refereeOne;
    protected Referee refereeTwo;
    protected EmploymentPosition employmentPosition;
    protected Language language;
    protected Disability disability;
    protected Ethnicity ethnicity;
    protected Domicile domicile;
    protected PersonalDetails personalDetails;
    protected AdditionalInformation additionalInformation;
    protected ProgramInstance instance;
    protected Program program;
    protected SourcesOfInterest interest;
    protected ProgrammeDetails programDetails;
    protected QualificationType qualificationType;
    protected Qualification qualification;
    protected Funding funding;
    protected ApplicationForm applicationForm;
    private ApplicationFormBuilder applicationFormBuilder;
    
    public ValidApplicationFormBuilder() {
    }

    protected Document getRandomDocument(DocumentType docType, String filename, RegisteredUser user) {
        try {
            Resource testFileAsResurce = new ClassPathResource("/pdf/valid.pdf");
            Document document = new DocumentBuilder()
            .dateUploaded(new Date())
            .contentType("application/pdf")
            .fileName(filename)
            .content(FileUtils.readFileToByteArray(testFileAsResurce.getFile()))
            .uploadedBy(user)
            .type(docType)
            .build();
            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ApplicationForm build(SessionFactory sessionFactory) {
        ApplicationForm applicationForm = build();
        save(sessionFactory, user, cvDocument, proofOfAwardDocument, referenceDocument, personalStatement, languageQualificationDocument, approverUser, language, country, domicile, address, program, employmentPosition, applicationForm);
        program.setCode("TMRMBISING001");
        return applicationForm;
    }
    
    protected void save(SessionFactory sessionFactory, Object... domainObjects) {
        for (Object domainObject : domainObjects) {
            sessionFactory.getCurrentSession().save(domainObject);
        }
    }
    
    public ApplicationForm build() {
        String addressStr = "Zuhlke Engineering Ltd\n43 Whitfield Street\nLondon W1T 4HD\nUnited Kingdom";
        user = new RegisteredUserBuilder().id(Integer.MAX_VALUE).firstName("Kevin").lastName("Denver").username("denk@zhaw.ch").enabled(true).build();
        cvDocument = getRandomDocument(DocumentType.CV, "My CV.pdf", user);
        referenceDocument = getRandomDocument(DocumentType.REFERENCE, "My Reference.pdf", user);
        personalStatement = getRandomDocument(DocumentType.PERSONAL_STATEMENT, "My Personal Statement (v1.0).pdf", user);
        proofOfAwardDocument = getRandomDocument(DocumentType.PROOF_OF_AWARD, "My Proof of Award.pdf", user);
        languageQualificationDocument = getRandomDocument(DocumentType.LANGUAGE_QUALIFICATION, "Language Qualification - My Name.pdf", user);
        fundingDocument = getRandomDocument(DocumentType.SUPPORTING_FUNDING, "Supporting Funding - My Name.pdf", user);
        approverUser = new RegisteredUserBuilder().id(Integer.MAX_VALUE-1).username("approver@zhaw.ch").enabled(true).build();
        country = new CountryBuilder().id(Integer.MAX_VALUE).code("XK").name("United Kingdom").enabled(true).build();
        address = new AddressBuilder().id(Integer.MAX_VALUE).country(country).address1(addressStr.split("\n")[0]).address2(addressStr.split("\n")[1]).address3(addressStr.split("\n")[2]).address4(addressStr.split("\n")[3]).build();
        referenceComment1 = new ReferenceCommentBuilder().comment("Hello World").document(referenceDocument).build();
        referenceComment2 = new ReferenceCommentBuilder().comment("Hello World").document(referenceDocument).build();
        refereeOne = new RefereeBuilder().user(approverUser).email("ked1@zuhlke.com").firstname("Bob").lastname("Smith").addressCountry(country).address1(addressStr.split("\n")[0]).address2(addressStr.split("\n")[1]).address3(addressStr.split("\n")[2]).address4(addressStr.split("\n")[3]).jobEmployer("Zuhlke Engineering Ltd.").jobTitle("Software Engineer").messenger("skypeAddress").phoneNumber("+44 (0) 123 123 1234").sendToUCL(true).reference(referenceComment1).toReferee();
        refereeTwo = new RefereeBuilder().user(approverUser).email("ked2@zuhlke.com").firstname("Bob").lastname("Smith").addressCountry(country).address1(addressStr.split("\n")[0]).address2(addressStr.split("\n")[1]).address3(addressStr.split("\n")[2]).address4(addressStr.split("\n")[3]).jobEmployer("Zuhlke Engineering Ltd.").jobTitle("Software Engineer").messenger("skypeAddress").phoneNumber("+44 (0) 123 123 1234").sendToUCL(true).reference(referenceComment2).toReferee();
        employmentPosition = new EmploymentPositionBuilder()
            .current(true)
            .address1(addressStr.split("\n")[0]).address2(addressStr.split("\n")[1]).address3(addressStr.split("\n")[2]).address4(addressStr.split("\n")[3])
            .country(country)
            .position("Software Engineer")
            .current(true)
            .startDate(DateUtils.addYears(new Date(), -2))
            .remit("Developer")
            .employerName("Zuhlke Ltd.")
            .toEmploymentPosition();
        language = new LanguageBuilder().id(Integer.MAX_VALUE).code("GB").name("England").enabled(true).build();
        disability = new DisabilityBuilder().id(Integer.MAX_VALUE).code(0).name("No Disability").enabled(true).build();
        ethnicity = new EthnicityBuilder().id(Integer.MAX_VALUE).code(10).name("White").enabled(true).build();
        domicile = new DomicileBuilder().id(Integer.MAX_VALUE).code("XK").name("United Kingdom").enabled(true).build();
        personalDetails = new PersonalDetailsBuilder()
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
            .passportInformation(new PassportInformationBuilder().passportNumber("000").nameOnPassport("Kevin Francis Denver").passportExpiryDate(org.apache.commons.lang.time.DateUtils.addYears(new Date(), 20)).passportIssueDate(org.apache.commons.lang.time.DateUtils.addYears(new Date(), -10)).build())
            .languageQualificationAvailable(true)
            .languageQualifications(new LanguageQualificationBuilder().dateOfExamination(new Date()).examTakenOnline(false).languageQualification(LanguageQualificationEnum.OTHER).listeningScore("1").otherQualificationTypeName("FooBar").overallScore("1").readingScore("1").speakingScore("1").writingScore("1").sendToUCL(true).languageQualificationDocument(languageQualificationDocument).sendToUCL(true).build())
            .phoneNumber("+44 (0) 123 123 1234")
            .residenceDomicile(domicile)
            .title(Title.MR)
            .build();
        additionalInformation = new AdditionalInformationBuilder()
            .id(Integer.MAX_VALUE)
            .setConvictions(false)
            .build();
        instance = new ProgramInstanceBuilder()
            .id(Integer.MAX_VALUE)
            .academicYear("2013")
            .applicationDeadline(org.apache.commons.lang.time.DateUtils.addYears(new Date(), 1))
            .applicationStartDate(org.apache.commons.lang.time.DateUtils.addMonths(new Date(), 5))
            .enabled(true)
            .studyOption("F+++++", "Full-time")
            .identifier("0009")
            .build();
        program = new ProgramBuilder()
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
            .build();
        interest = new SourcesOfInterestBuilder().id(Integer.MAX_VALUE).code("BRIT_COUN").name("British Council").build();
        programDetails = new ProgrammeDetailsBuilder()
            .id(Integer.MAX_VALUE)
            .programmeName("MRes Medical and Biomedical Imaging")
            .projectName("Project Title")
            .sourcesOfInterest(interest)
            .startDate(org.apache.commons.lang.time.DateUtils.addDays(new Date(), 1))
            .studyOption("F+++++", "Full-time")
            .build();
        qualificationType = new QualificationTypeBuilder().id(Integer.MAX_VALUE).code("DEGTRE").name("Bachelors Degree - France").enabled(true).build();
        qualification = new QualificationBuilder()
            .id(Integer.MAX_VALUE)
            .awardDate(new Date())
            .grade("6")
            .institutionCode("UK0000")
            .institution("University of London")
            .country(domicile)
            .languageOfStudy("English")
            .startDate(org.apache.commons.lang.time.DateUtils.addYears(new Date(), -1))
            .subject("Engineering")
            .type(qualificationType)
            .isCompleted(CheckedStatus.YES)
            .proofOfAward(proofOfAwardDocument)
            .sendToUCL(true)
            .build();
        funding = new FundingBuilder().id(Integer.MAX_VALUE).awardDate(DateUtils.addYears(new Date(), -1)).description("Received a funding").document(fundingDocument).type(FundingType.SCHOLARSHIP).value("5").build();
        applicationFormBuilder = new ApplicationFormBuilder()
            .id(Integer.MAX_VALUE)
            .applicant(user)
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
            .fundings(funding)
            .lastUpdated(new Date())
            .personalDetails(personalDetails)
            .program(program)
            .programmeDetails(programDetails)
            .projectTitle("Project Title")
            .qualification(qualification)
            .status(ApplicationFormStatus.APPROVED)
            .submittedDate(new Date())
            .cv(cvDocument)
            .personalStatement(personalStatement)
            .referees(refereeOne, refereeTwo)
            .ipAddress("127.0.0.1");
        applicationForm = getApplicationFormBuilder().build();
        return applicationForm;
    }

    public ApplicationFormBuilder getApplicationFormBuilder() {
        return applicationFormBuilder;
    }
}
