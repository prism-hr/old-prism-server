package com.zuehlke.pgadmissions.workflow;

import java.util.UUID;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.Passport;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.builders.TestObjectProvider;
import com.zuehlke.pgadmissions.domain.enums.FundingType;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.services.AdditionalInformationService;
import com.zuehlke.pgadmissions.services.ApplicationAddressService;
import com.zuehlke.pgadmissions.services.ApplicationDocumentService;
import com.zuehlke.pgadmissions.services.EmploymentPositionService;
import com.zuehlke.pgadmissions.services.FundingService;
import com.zuehlke.pgadmissions.services.PersonalDetailsService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;

@Service
@Transactional
public class ApplicationTestDataProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private PersonalDetailsService personalDetailsService;

    @Autowired
    private ApplicationAddressService applicationAdressService;

    @Autowired
    private QualificationService qualificationService;

    @Autowired
    private EmploymentPositionService employmentPositionService;

    @Autowired
    private FundingService fundingService;

    @Autowired
    private RefereeService refereeService;

    @Autowired
    private ApplicationDocumentService applicationDocumentService;

    @Autowired
    private AdditionalInformationService additionalInformationService;

    @Autowired
    private TestObjectProvider testObjectProvider;

    public void fillWithData(Application application) throws Exception {
        createPersonalDetails(application);
        createAddress(application);
        createQualifications(application);
        createEmployments(application);
        createFunding(application);
        createReferees(application);
        createDocuments(application);
        createAdditionalInformation(application);
    }

    private void createPersonalDetails(Application application) {
        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setGender(Gender.FEMALE);
        personalDetails.setDateOfBirth(new LocalDate().minusYears(28));
        personalDetails.setCountry(testObjectProvider.get(Country.class));
        personalDetails.setFirstNationality(testObjectProvider.get(Language.class));
        personalDetails.setSecondNationality(testObjectProvider.get(Language.class));
        personalDetails.setLanguageQualificationAvailable(true);
        LanguageQualification languageQualification = new LanguageQualification();
        languageQualification.setQualificationType(LanguageQualificationEnum.OTHER);
        languageQualification.setQualificationTypeOther("I tak sie chuja nauczylem");
        languageQualification.setExamDate(new LocalDate(1967, 9, 14));
        languageQualification.setOverallScore("6");
        languageQualification.setReadingScore("6");
        languageQualification.setWritingScore("6");
        languageQualification.setSpeakingScore("6");
        languageQualification.setListeningScore("6");
        languageQualification.setExamOnline(false);
        languageQualification.setProofOfAward(testObjectProvider.get(Document.class));
        personalDetails.setLanguageQualification(languageQualification);
        personalDetails.setResidenceCountry(testObjectProvider.get(Domicile.class));
        personalDetails.setRequiresVisa(true);
        Passport passport = new Passport();
        passport.setNumber("666");
        passport.setName("Kubus Fibinger");
        passport.setIssueDate(new LocalDate(2003, 9, 14));
        passport.setExpiryDate(new LocalDate(2084, 8, 14));
        personalDetails.setPassport(passport);
        personalDetails.setPhoneNumber("+44(4)5435435");
        personalDetails.setMessenger("dupajasia");
        personalDetails.setEthnicity(testObjectProvider.get(Ethnicity.class));
        personalDetails.setDisability(testObjectProvider.get(Disability.class));

        personalDetailsService.saveOrUpdate(application, personalDetails, application.getUser());
    }

    private void createAddress(Application application) {
        ApplicationAddress applicationAddress = new ApplicationAddress();
        Address address = new Address();
        address.setAddressLine1("ul. Siewna");
        address.setAddressLine2("Bielsko-Biala");
        address.setAddressRegion("woj. Slaskie");
        address.setAddressCode("43-300");
        address.setDomicile(testObjectProvider.get(Domicile.class));
        applicationAddress.setCurrentAddress(address);
        applicationAddress.setContactAddress(address);

        applicationAdressService.saveOrUpdate(application, applicationAddress);
    }

    private void createQualifications(Application application) {
        for (int i = 0; i < 3; i++) {
            Qualification qualification = new Qualification();
            qualification.setInstitution(testObjectProvider.get(ImportedInstitution.class));
            qualification.setType(testObjectProvider.get(QualificationType.class));
            qualification.setTitle("Mistrzostwo");
            qualification.setSubject("Jestem piekny i mam wspaniale miesnie");
            qualification.setLanguage("Polish");
            qualification.setStartDate(new LocalDate().minusYears(15));
            qualification.setCompleted(true);
            qualification.setGrade("6");
            qualification.setAwardDate(new LocalDate().minusYears(10));
            qualification.setDocument(testObjectProvider.get(Document.class));
            qualificationService.saveOrUpdate(application, null, qualification);
        }
    }

    private void createEmployments(Application application) {
        EmploymentPosition employment = new EmploymentPosition();
        employment.setEmployerName("Szef");
        Address address = new Address();
        address.setAddressLine1("ul. Leszczynska");
        address.setAddressLine2("Bielsko-Biala");
        address.setAddressRegion("woj. Slaskie");
        address.setAddressCode("43-300");
        address.setDomicile(testObjectProvider.get(Domicile.class));
        employment.setEmployerAddress(address);
        employment.setPosition("Robol");
        employment.setRemit("Nic ino zapierdalac trza bylo");
        employment.setStartDate(new LocalDate().minusYears(2));
        employment.setCurrent(true);
        employmentPositionService.saveOrUpdate(application, null, employment);
    }

    private void createFunding(Application application) {
        Funding funding = new Funding();
        funding.setType(FundingType.SCHOLARSHIP);
        funding.setDescription("Aa narucham troche kasy, niewazne skont");
        funding.setValue("2000000");
        funding.setAwardDate(new LocalDate().minusYears(1));
        funding.setDocument(testObjectProvider.get(Document.class));
        fundingService.saveOrUpdate(application, null, funding);
    }

    private void createReferees(Application application) {
        for (int i = 0; i < 3; i++) {
            Referee referee = new Referee();
            referee.setUser(userService.getOrCreateUser("Jakis", "Polecacz", "polecacz" + UUID.randomUUID().toString() + "@email.com"));
            referee.setJobEmployer("Kozacka firma");
            referee.setJobTitle("Szef wszystkich szefow");
            Address address = new Address();
            address.setAddressLine1("ul. Piastowska 84");
            address.setAddressLine2("Bielsko-Biala");
            address.setAddressRegion("woj. Slaskie");
            address.setAddressCode("43-300");
            address.setDomicile(testObjectProvider.get(Domicile.class));
            referee.setAddress(address);
            referee.setPhoneNumber("+44(0)5435435");
            referee.setMessenger("szefwszystkichszefow");
            refereeService.saveOrUpdate(application, null, referee);
        }
    }

    private void createDocuments(Application application) {
        ApplicationDocument applicationDocument = new ApplicationDocument();
        applicationDocument.setPersonalStatement(testObjectProvider.get(Document.class));
        applicationDocument.setCv(testObjectProvider.get(Document.class));
        applicationDocumentService.saveOrUpdate(application, applicationDocument);
    }

    private void createAdditionalInformation(Application application) {
        AdditionalInformation additionalInformation = new AdditionalInformation();
        additionalInformation.setConvictions(true);
        additionalInformation.setConvictionsText("I was a bad person");
    }

}
