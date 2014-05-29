package com.zuehlke.pgadmissions.domain.builders;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.Passport;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.SourcesOfInterest;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.Title;

public class ValidApplicationFormBuilder {

    protected User user;
    protected Document cvDocument;
    protected Document referenceDocument;
    protected Document personalStatement;
    protected Document proofOfAwardDocument;
    protected Document languageQualificationDocument;
    protected Document fundingDocument;
    protected User approverUser;
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
    protected ImportedInstitution importedInstitution;
    protected Institution institution;
    protected Program program;
    protected SourcesOfInterest interest;
    protected ProgramDetails programDetails;
    protected QualificationType qualificationType;
    protected Qualification qualification1;
    protected Qualification qualification2;
    protected Funding funding;
    protected Application applicationForm;
    private ApplicationFormBuilder applicationFormBuilder;
    private State state;

    public ValidApplicationFormBuilder() {
    }

    protected Document getRandomDocument(DocumentType docType, String filename, User user) {
        try {
            Resource testFileAsResurce = new ClassPathResource("/pdf/valid.pdf");
            Document document = new Document().withCreatedTimestamp(new Date()).withContentType("application/pdf").withFileName(filename)
                    .withContent(FileUtils.readFileToByteArray(testFileAsResurce.getFile())).withType(docType);
            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Application build(SessionFactory sessionFactory) {
        Application applicationForm = build();
        save(sessionFactory, state, user, cvDocument, proofOfAwardDocument, referenceDocument, personalStatement, languageQualificationDocument, approverUser,
                language, country, domicile, address, importedInstitution, program, employmentPosition, disability, ethnicity, interest, applicationForm);
        program.setCode("TMRMBISING001");
        return applicationForm;
    }

    protected void save(SessionFactory sessionFactory, Object... domainObjects) {
        for (Object domainObject : domainObjects) {
            sessionFactory.getCurrentSession().save(domainObject);
        }
    }

    public Application build() {
        user = new User().withFirstName("Kevin").withFirstName2("Franciszek").withFirstName3("Duncan").withLastName("Denver").withEmail("ked@zuhlke.com")
                .withAccount(new UserAccount().withEnabled(true));
        cvDocument = getRandomDocument(DocumentType.CV, "My CV.pdf", user);
        referenceDocument = getRandomDocument(DocumentType.REFERENCE, "My Reference.pdf", user);
        personalStatement = getRandomDocument(DocumentType.PERSONAL_STATEMENT, "My Personal Statement (v1.0).pdf", user);
        proofOfAwardDocument = getRandomDocument(DocumentType.PROOF_OF_AWARD, "My Proof of Award.pdf", user);
        languageQualificationDocument = getRandomDocument(DocumentType.LANGUAGE_QUALIFICATION, "Language Qualification - My Name.pdf", user);
        fundingDocument = getRandomDocument(DocumentType.SUPPORTING_FUNDING, "Supporting Funding - My Name.pdf", user);
        approverUser = new User().withId(Integer.MAX_VALUE - 1).withEmail("approver@zhaw.ch").withAccount(new UserAccount().withEnabled(true));
        country = new Country().withCode("XK").withName("United Kingdom");
        domicile = new Domicile().withCode("XK").withName("United Kingdom");
        address = TestData.anAddress(domicile);
        // referenceComment1 = new ReferenceCommentBuilder().comment("Hello From Bob").document(referenceDocument).providedBy(user).suitableForProgramme(true)
        // .suitableForUcl(true).user(user).build();
        // referenceComment2 = new ReferenceCommentBuilder().comment("Hello From Jane").document(referenceDocument).providedBy(user).suitableForProgramme(true)
        // .suitableForUcl(true).user(user).build();
        refereeOne = new Referee().withId(Integer.MAX_VALUE - 1).withUser(approverUser).withAddress(TestData.anAddress(domicile))
                .withJobEmployer("Zuhlke Engineering Ltd.").withJobTitle("Software Engineer").withSkype("skypeAddress").withPhoneNumber("+44 (0) 123 123 1234")
                .withIncludeInExport(true).withComment(referenceComment1);
        refereeTwo = new Referee().withId(Integer.MAX_VALUE - 2).withUser(approverUser).withAddress(TestData.anAddress(domicile))
                .withJobEmployer("Zuhlke Engineering Ltd.").withJobTitle("Software Engineer").withSkype("skypeAddress").withPhoneNumber("+44 (0) 123 123 1234")
                .withIncludeInExport(true).withComment(referenceComment2);
        refereeOne.setComment(referenceComment1);
        refereeTwo.setComment(referenceComment2);
        employmentPosition = new EmploymentPosition().withCurrent(true).withEmployerAddress(TestData.anAddress(domicile)).withPosition("Software Engineer")
                .withCurrent(true).withStartDate(new LocalDate().minusYears(2)).withRemit("Developer").withEmployerName("Zuhlke Ltd.");
        language = new Language().withCode("GB").withName("England");
        disability = new Disability().withCode("0").withName("No Disability");
        ethnicity = new Ethnicity().withCode("10").withName("White");
        personalDetails = new PersonalDetails()
                .withFirstNationality(language)
                .withCountry(country)
                .withDateOfBirth(new LocalDate().minusYears(28))
                .withDisability(disability)
                .withEnglishFirstLanguage(true)
                .withEthnicity(ethnicity)
                .withGender(Gender.MALE)
                .withRequiresVisa(true)
                .withPassportInformation(
                        new Passport().withNumber("000").withName("Kevin Francis Denver").withExpiryDate(new LocalDate().plusYears(20))
                                .withIssueDate(new LocalDate().minusYears(10)))
                .withLanguageQualificationAvailable(true)
                .withLanguageQualification(
                        new LanguageQualification().withExamDate(new LocalDate()).withExamOnline(false).withQualificationType(LanguageQualificationEnum.OTHER)
                                .withListeningScore("1").withQualificationTypeOther("FooBar").withOverallScore("1").withReadingScore("1")
                                .withSpeakingScore("1").withWritingScore("1").withProofOfAward(languageQualificationDocument))
                .withPhoneNumber("+44 (0) 123 123 1234").withResidenceCountry(domicile).withTitle(Title.MR);
        additionalInformation = new AdditionalInformation().withHasConvictions(false);
        instance = new ProgramInstance().withAcademicYear("2013").withApplicationDeadline(new LocalDate().plusYears(1))
                .withApplicationStartDate(new LocalDate().plusMonths(5)).withEnabled(true).withStudyOption("F+++++", "Full-time").withIdentifier("0009");
        importedInstitution = new ImportedInstitution().withCode("code").withName("jakas instytucja").withDomicile(domicile).withEnabled(true);
        state = new State().withId(PrismState.PROGRAM_APPROVED);
        program = new Program().withUser(approverUser).withCode("TMRMBISING99").withState(state).withInstances(instance)
                .withTitle("MRes Medical and Biomedical Imaging").withInstitution(institution);
        interest = new SourcesOfInterest().withCode("BRIT_COUN").withName("British Council");
        programDetails = new ProgrammeDetailsBuilder().sourcesOfInterest(interest).startDate(new LocalDate().plusDays(1))
                .studyOption(new StudyOption("F+++++", "Full-time")).build();
        qualificationType = new QualificationType().withCode("DEGTRE").withName("Bachelors Degree - France");
        qualification1 = new Qualification().withId(Integer.MAX_VALUE - 1).withAwardDate(new LocalDate()).withGrade("6").withInstitution(importedInstitution)
                .withLanguage("English").withStartDate(new LocalDate().minusYears(1)).withSubject("Engineering").withTitle("MSc").withType(qualificationType)
                .withCompleted(true).withDocument(proofOfAwardDocument).withIncludeInExport(true);
        qualification2 = new Qualification().withId(Integer.MAX_VALUE - 2).withAwardDate(new LocalDate()).withGrade("6").withInstitution(importedInstitution)
                .withLanguage("English").withStartDate(new LocalDate().minusYears(1)).withSubject("Engineering").withTitle("MSc").withType(qualificationType)
                .withCompleted(true).withDocument(proofOfAwardDocument).withIncludeInExport(true);
        funding = new Funding().withAwardDate(new LocalDate().minusYears(1)).withDescription("Received a funding").withDocument(fundingDocument)
                .withType(FundingType.SCHOLARSHIP).withValue("5");
        applicationFormBuilder = new ApplicationFormBuilder().applicant(user).acceptedTerms(true).additionalInformation(additionalInformation)
                .createdTimestamp(new DateTime()).applicant(user).applicationNumber("TMRMBISING01-2012-999999").closingDate(new LocalDate().plusMonths(1))
                .applicationFormAddress(new ApplicationAddress().withCurrentAddress(address).withContactAddress(address))
                .dueDate(new LocalDate().plusMonths(1)).employmentPositions(employmentPosition).fundings(funding).personalDetails(personalDetails)
                .program(program).programmeDetails(programDetails).qualification(qualification1, qualification2)
                .status(new State().withId(PrismState.APPLICATION_APPROVED)).submittedDate(new DateTime())
                .applicationFormDocument(new ApplicationDocument().withPersonalStatement(personalStatement).withCv(cvDocument))
                .referees(refereeOne, refereeTwo);
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
