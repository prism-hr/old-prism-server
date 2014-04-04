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
import com.zuehlke.pgadmissions.domain.ApplicationFormAddress;
import com.zuehlke.pgadmissions.domain.ApplicationFormDocument;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
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
    protected Institution institution;
    protected Program program;
    protected SourcesOfInterest interest;
    protected ProgramDetails programDetails;
    protected QualificationType qualificationType;
    protected Qualification qualification1;
    protected Qualification qualification2;
    protected Funding funding;
    protected ApplicationForm applicationForm;
    private ApplicationFormBuilder applicationFormBuilder;

    public ValidApplicationFormBuilder() {
    }

    protected Document getRandomDocument(DocumentType docType, String filename, RegisteredUser user) {
        try {
            Resource testFileAsResurce = new ClassPathResource("/pdf/valid.pdf");
            Document document = new DocumentBuilder().dateUploaded(new Date()).contentType("application/pdf").fileName(filename)
                    .content(FileUtils.readFileToByteArray(testFileAsResurce.getFile())).type(docType).build();
            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ApplicationForm build(SessionFactory sessionFactory) {
        ApplicationForm applicationForm = build();
        save(sessionFactory, user, cvDocument, proofOfAwardDocument, referenceDocument, personalStatement, languageQualificationDocument, approverUser,
                language, country, domicile, address, institution, program, employmentPosition, disability, ethnicity, interest, applicationForm);
        program.setCode("TMRMBISING001");
        return applicationForm;
    }

    protected void save(SessionFactory sessionFactory, Object... domainObjects) {
        for (Object domainObject : domainObjects) {
            sessionFactory.getCurrentSession().save(domainObject);
        }
    }

    public ApplicationForm build() {
        String addressStr = "Zuhlke Engineering Ltd\n43 Whitfield Street\nLondon\n\nW1T 4HD\nUnited Kingdom";
        user = new RegisteredUserBuilder().firstName("Kevin").firstName2("Franciszek").firstName3("Duncan").lastName("Denver").username("denk@zhaw.ch")
                .email("ked@zuhlke.com").enabled(true).build();
        cvDocument = getRandomDocument(DocumentType.CV, "My CV.pdf", user);
        referenceDocument = getRandomDocument(DocumentType.REFERENCE, "My Reference.pdf", user);
        personalStatement = getRandomDocument(DocumentType.PERSONAL_STATEMENT, "My Personal Statement (v1.0).pdf", user);
        proofOfAwardDocument = getRandomDocument(DocumentType.PROOF_OF_AWARD, "My Proof of Award.pdf", user);
        languageQualificationDocument = getRandomDocument(DocumentType.LANGUAGE_QUALIFICATION, "Language Qualification - My Name.pdf", user);
        fundingDocument = getRandomDocument(DocumentType.SUPPORTING_FUNDING, "Supporting Funding - My Name.pdf", user);
        approverUser = new RegisteredUserBuilder().id(Integer.MAX_VALUE - 1).username("approver@zhaw.ch").enabled(true).build();
        country = new CountryBuilder().code("XK").name("United Kingdom").enabled(true).build();
        domicile = new DomicileBuilder().code("XK").name("United Kingdom").enabled(true).build();
        address = new AddressBuilder().domicile(domicile).address1(addressStr.split("\n")[0]).address2(addressStr.split("\n")[1])
                .address3(addressStr.split("\n")[2]).address4(addressStr.split("\n")[3]).address5(addressStr.split("\n")[4]).build();
        referenceComment1 = new ReferenceCommentBuilder().comment("Hello From Bob").document(referenceDocument).providedBy(user).suitableForProgramme(true)
                .suitableForUcl(true).user(user).build();
        referenceComment2 = new ReferenceCommentBuilder().comment("Hello From Jane").document(referenceDocument).providedBy(user).suitableForProgramme(true)
                .suitableForUcl(true).user(user).build();
        refereeOne = new RefereeBuilder().id(Integer.MAX_VALUE - 1).user(approverUser).email("ked1@zuhlke.com").firstname("Bob").lastname("Smith")
                .addressDomicile(domicile).address1(addressStr.split("\n")[0]).address2(addressStr.split("\n")[1]).address3(addressStr.split("\n")[2])
                .address4(addressStr.split("\n")[3]).address5(addressStr.split("\n")[4]).jobEmployer("Zuhlke Engineering Ltd.").jobTitle("Software Engineer")
                .messenger("skypeAddress").phoneNumber("+44 (0) 123 123 1234").sendToUCL(true).reference(referenceComment1).build();
        refereeTwo = new RefereeBuilder().id(Integer.MAX_VALUE - 2).user(approverUser).email("ked2@zuhlke.com").firstname("Jane").lastname("Austen")
                .addressDomicile(domicile).address1(addressStr.split("\n")[0]).address2(addressStr.split("\n")[1]).address3(addressStr.split("\n")[2])
                .address4(addressStr.split("\n")[3]).address5(addressStr.split("\n")[4]).jobEmployer("Zuhlke Engineering Ltd.").jobTitle("Software Engineer")
                .messenger("skypeAddress").phoneNumber("+44 (0) 123 123 1234").sendToUCL(true).reference(referenceComment2).build();
        refereeOne.setReference(referenceComment1);
        refereeTwo.setReference(referenceComment2);
        employmentPosition = new EmploymentPositionBuilder().current(true).address1(addressStr.split("\n")[0]).address2(addressStr.split("\n")[1])
                .address3(addressStr.split("\n")[2]).address4(addressStr.split("\n")[3]).address5(addressStr.split("\n")[4]).domicile(domicile)
                .position("Software Engineer").current(true).startDate(DateUtils.addYears(new Date(), -2)).remit("Developer").employerName("Zuhlke Ltd.")
                .toEmploymentPosition();
        language = new LanguageBuilder().code("GB").name("England").enabled(true).build();
        disability = new DisabilityBuilder().code("0").name("No Disability").enabled(true).build();
        ethnicity = new EthnicityBuilder().code("10").name("White").enabled(true).build();
        personalDetails = new PersonalDetailsBuilder()
                .firstNationality(language)
                .country(country)
                .dateOfBirth(org.apache.commons.lang.time.DateUtils.addYears(new Date(), -28))
                .disability(disability)
                .englishFirstLanguage(true)
                .ethnicity(ethnicity)
                .gender(Gender.MALE)
                .requiresVisa(true)
                .passportInformation(
                        new PassportInformationBuilder().number("000").name("Kevin Francis Denver")
                                .expiryDate(org.apache.commons.lang.time.DateUtils.addYears(new Date(), 20))
                                .issueDate(org.apache.commons.lang.time.DateUtils.addYears(new Date(), -10)).build())
                .languageQualificationAvailable(true)
                .languageQualification(
                        new LanguageQualificationBuilder().examDate(new Date()).examOnline(false).languageQualification(LanguageQualificationEnum.OTHER)
                                .listeningScore("1").qualificationTypeName("FooBar").overallScore("1").readingScore("1").speakingScore("1").writingScore("1")
                                .languageQualificationDocument(languageQualificationDocument).build()).phoneNumber("+44 (0) 123 123 1234")
                .residenceDomicile(domicile).title(Title.MR).build();
        additionalInformation = new AdditionalInformationBuilder().setConvictions(false).build();
        instance = new ProgramInstanceBuilder().academicYear("2013").applicationDeadline(org.apache.commons.lang.time.DateUtils.addYears(new Date(), 1))
                .applicationStartDate(org.apache.commons.lang.time.DateUtils.addMonths(new Date(), 5)).enabled(true).studyOption("F+++++", "Full-time")
                .identifier("0009").build();
        institution = new QualificationInstitutionBuilder().code("code").name("jakas instytucja").domicileCode("AE").enabled(true).build();
        program = new ProgramBuilder().contactUser(approverUser).code("TMRMBISING99").enabled(true).instances(instance)
                .title("MRes Medical and Biomedical Imaging").institution(institution).build();
        interest = new SourcesOfInterestBuilder().code("BRIT_COUN").name("British Council").build();
        programDetails = new ProgrammeDetailsBuilder().sourcesOfInterest(interest).startDate(org.apache.commons.lang.time.DateUtils.addDays(new Date(), 1))
                .studyOption(new StudyOption("F+++++", "Full-time")).build();
        qualificationType = new QualificationTypeBuilder().code("DEGTRE").name("Bachelors Degree - France").enabled(true).build();
        qualification1 = new QualificationBuilder().id(Integer.MAX_VALUE - 1).awardDate(new Date()).grade("6").institution(institution)
                .languageOfStudy("English").startDate(org.apache.commons.lang.time.DateUtils.addYears(new Date(), -1)).subject("Engineering").title("MSc")
                .type(qualificationType).isCompleted(true).proofOfAward(proofOfAwardDocument).sendToUCL(true).build();
        qualification2 = new QualificationBuilder().id(Integer.MAX_VALUE - 2).awardDate(new Date()).grade("6").institution(institution)
                .languageOfStudy("English").startDate(org.apache.commons.lang.time.DateUtils.addYears(new Date(), -1)).subject("Engineering").title("MSc")
                .type(qualificationType).isCompleted(true).proofOfAward(proofOfAwardDocument).sendToUCL(true).build();
        funding = new FundingBuilder().awardDate(DateUtils.addYears(new Date(), -1)).description("Received a funding").document(fundingDocument)
                .type(FundingType.SCHOLARSHIP).value("5").build();
        applicationFormBuilder = new ApplicationFormBuilder().applicant(user).acceptedTerms(true).additionalInformation(additionalInformation)
                .appDate(new Date()).applicant(user).applicationNumber("TMRMBISING01-2012-999999")
                .closingDate(org.apache.commons.lang.time.DateUtils.addMonths(new Date(), 1))
                .applicationFormAddress(new ApplicationFormAddress().withCurrentAddress(address).withContactAddress(address))
                .dueDate(org.apache.commons.lang.time.DateUtils.addMonths(new Date(), 1)).employmentPositions(employmentPosition).fundings(funding)
                .lastUpdated(new Date()).personalDetails(personalDetails).advert(program).programmeDetails(programDetails).projectTitle("Project Title")
                .qualification(qualification1, qualification2).status(new State().withId(ApplicationFormStatus.APPROVED)).submittedDate(new Date())
                .applicationFormDocument(new ApplicationFormDocument().withPersonalStatement(personalStatement).withCv(cvDocument))
                .referees(refereeOne, refereeTwo).ipAddress("127.0.0.1");
        applicationForm = getApplicationFormBuilder().build();

        personalDetails.setApplication(applicationForm);
        qualification1.setApplication(applicationForm);
        qualification2.setApplication(applicationForm);
        employmentPosition.setApplication(applicationForm);
        funding.setApplication(applicationForm);
        refereeOne.setApplication(applicationForm);
        refereeTwo.setApplication(applicationForm);
        additionalInformation.setApplication(applicationForm);

        return applicationForm;
    }

    public ApplicationFormBuilder getApplicationFormBuilder() {
        return applicationFormBuilder;
    }
}
