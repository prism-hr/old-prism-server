package com.zuehlke.pgadmissions.integration.providers;

import java.util.UUID;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.ApplicationFunding;
import com.zuehlke.pgadmissions.domain.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.ApplicationPassport;
import com.zuehlke.pgadmissions.domain.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.FundingSource;
import com.zuehlke.pgadmissions.domain.Gender;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageQualificationType;
import com.zuehlke.pgadmissions.domain.QualificationType;
import com.zuehlke.pgadmissions.domain.builders.TestObjectProvider;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.UserService;

@Service
@Transactional
public class ApplicationTestDataProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private TestObjectProvider testObjectProvider;
    
    @Autowired
    private EntityService entityService;

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
        ApplicationPersonalDetail personalDetails = new ApplicationPersonalDetail();
        personalDetails.setGender(testObjectProvider.get(Gender.class));
        personalDetails.setDateOfBirth(new LocalDate().minusYears(28));
        personalDetails.setCountry(testObjectProvider.get(Country.class));
        personalDetails.setFirstNationality(testObjectProvider.get(Language.class));
        personalDetails.setSecondNationality(testObjectProvider.get(Language.class));
        personalDetails.setLanguageQualificationAvailable(true);
        ApplicationLanguageQualification languageQualification = new ApplicationLanguageQualification();
        languageQualification.setType(entityService.getByProperty(LanguageQualificationType.class, "code", "IELTS_ACADEMIC"));
        languageQualification.setExamDate(new LocalDate(1967, 9, 14));
        languageQualification.setOverallScore("6");
        languageQualification.setReadingScore("6");
        languageQualification.setWritingScore("6");
        languageQualification.setSpeakingScore("6");
        languageQualification.setListeningScore("6");
        languageQualification.setDocument(testObjectProvider.get(Document.class));
        personalDetails.setLanguageQualification(languageQualification);
        personalDetails.setResidenceCountry(testObjectProvider.get(Domicile.class));
        personalDetails.setVisaRequired(true);
        ApplicationPassport passport = new ApplicationPassport();
        passport.setNumber("666");
        passport.setName("Kubus Fibinger");
        passport.setIssueDate(new LocalDate(2003, 9, 14));
        passport.setExpiryDate(new LocalDate(2084, 8, 14));
        personalDetails.setPassport(passport);
        personalDetails.setPhoneNumber("+44(4)5435435");
        personalDetails.setMessenger("dupajasia");
        personalDetails.setEthnicity(testObjectProvider.get(Ethnicity.class));
        personalDetails.setDisability(testObjectProvider.get(Disability.class));
        application.setPersonalDetail(personalDetails);
        entityService.save(personalDetails);
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
        application.setAddress(applicationAddress);
        entityService.save(applicationAddress);
    }

    private void createQualifications(Application application) {
        for (int i = 0; i < 3; i++) {
            ApplicationQualification qualification = new ApplicationQualification();
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
            qualification.setApplication(application);
            entityService.save(qualification);
        }
    }

    private void createEmployments(Application application) {
        ApplicationEmploymentPosition employment = new ApplicationEmploymentPosition();
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
        employment.setApplication(application);
        entityService.save(employment);
    }

    private void createFunding(Application application) {
        ApplicationFunding funding = new ApplicationFunding();
        funding.setFundingSource(entityService.getByProperty(FundingSource.class, "code", "SCHOLARSHIP"));
        funding.setDescription("Aa narucham troche kasy, niewazne skont");
        funding.setValue("2000000");
        funding.setAwardDate(new LocalDate().minusYears(1));
        funding.setDocument(testObjectProvider.get(Document.class));
        funding.setApplication(application);
        entityService.save(funding);
    }

    private void createReferees(Application application) throws Exception {
        for (int i = 0; i < 3; i++) {
            ApplicationReferee referee = new ApplicationReferee();
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
            referee.setSkype("szefwszystkichszefow");
            referee.setApplication(application);
            entityService.save(referee);
        }
    }

    private void createDocuments(Application application) {
        ApplicationDocument applicationDocument = new ApplicationDocument();
        applicationDocument.setPersonalStatement(testObjectProvider.get(Document.class));
        applicationDocument.setCv(testObjectProvider.get(Document.class));
        application.setDocument(applicationDocument);
        entityService.save(applicationDocument);
    }

    private void createAdditionalInformation(Application application) {
        ApplicationAdditionalInformation additionalInformation = new ApplicationAdditionalInformation();
        additionalInformation.setConvictionsText("I was a bad person");
        application.setAdditionalInformation(additionalInformation);
        entityService.save(additionalInformation);
    }

}
