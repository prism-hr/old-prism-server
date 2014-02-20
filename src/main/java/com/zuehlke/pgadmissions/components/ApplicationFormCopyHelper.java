package com.zuehlke.pgadmissions.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.DocumentDAO;
import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.PassportInformation;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.SelfReferringImportedObject;

@Component
public class ApplicationFormCopyHelper {

    @Autowired
    private DocumentDAO documentDAO;

    @Transactional
    public void copyApplicationFormData(ApplicationForm to, ApplicationForm from) {
        if (from.getPersonalDetails() != null) {
            PersonalDetails personalDetails = new PersonalDetails();
            to.setPersonalDetails(personalDetails);
            personalDetails.setApplication(to);
            copyPersonalDetailsData(to.getPersonalDetails(), from.getPersonalDetails());
        }

        to.setCurrentAddress(copyAddress(from.getCurrentAddress()));
        to.setContactAddress(copyAddress(from.getContactAddress()));

        for (Qualification fromQualification : from.getQualifications()) {
            Qualification qualification = new Qualification();
            to.getQualifications().add(qualification);
            qualification.setApplication(to);
            copyQualification(qualification, fromQualification);
        }

        for (EmploymentPosition fromEmployment : from.getEmploymentPositions()) {
            EmploymentPosition employment = new EmploymentPosition();
            to.getEmploymentPositions().add(employment);
            employment.setApplication(to);
            copyEmploymentPosition(employment, fromEmployment);
        }

        for (Funding fromFunding : from.getFundings()) {
            Funding funding = new Funding();
            to.getFundings().add(funding);
            funding.setApplication(to);
            copyFunding(funding, fromFunding);
        }

        for (Referee fromReferee : from.getReferees()) {
            Referee referee = new Referee();
            to.getReferees().add(referee);
            referee.setApplication(to);
            copyReferee(referee, fromReferee);
        }

        to.setPersonalStatement(copyDocument(from.getPersonalStatement()));
        to.setCv(copyDocument(from.getCv()));

        if (from.getAdditionalInformation() != null) {
            AdditionalInformation additionalInformation = new AdditionalInformation();
            to.setAdditionalInformation(additionalInformation);
            additionalInformation.setApplication(to);
            copyAdditionalInformation(additionalInformation, from.getAdditionalInformation());
        }
    }

    private void copyAdditionalInformation(AdditionalInformation to, AdditionalInformation from) {
        to.setConvictions(from.getConvictions());
        to.setConvictionsText(from.getConvictionsText());
    }

    private void copyReferee(Referee to, Referee from) {
        to.setFirstname(from.getFirstname());
        to.setLastname(from.getLastname());
        to.setEmail(from.getEmail());
        to.setJobEmployer(from.getJobEmployer());
        to.setJobTitle(from.getJobTitle());
        to.setAddressLocation(copyAddress(from.getAddressLocation()));
        to.setPhoneNumber(from.getPhoneNumber());
        to.setMessenger(from.getMessenger());
    }

    private void copyFunding(Funding to, Funding from) {
        to.setType(from.getType());
        to.setDescription(from.getDescription());
        to.setValue(from.getValue());
        to.setAwardDate(from.getAwardDate());
        to.setDocument(copyDocument(from.getDocument()));
    }

    private void copyEmploymentPosition(EmploymentPosition to, EmploymentPosition from) {
        to.setEmployerName(from.getEmployerName());
        to.setEmployerAddress(copyAddress(from.getEmployerAddress()));
        to.setPosition(from.getPosition());
        to.setRemit(from.getRemit());
        to.setStartDate(from.getStartDate());
        to.setCurrent(from.isCurrent());
        to.setEndDate(from.getEndDate());
    }

    private void copyQualification(Qualification to, Qualification from) {
        to.setInstitutionCountry(getEnabledImportedObject(from.getInstitutionCountry()));
        to.setQualificationInstitution(from.getQualificationInstitution());
        to.setQualificationInstitutionCode(from.getQualificationInstitutionCode());
        to.setOtherQualificationInstitution(from.getOtherQualificationInstitution());
        to.setQualificationType(getEnabledImportedObject(from.getQualificationType()));
        to.setQualificationTitle(from.getQualificationTitle());
        to.setQualificationSubject(from.getQualificationSubject());
        to.setQualificationLanguage(from.getQualificationLanguage());
        to.setQualificationStartDate(from.getQualificationStartDate());
        to.setCompleted(from.getCompleted());
        to.setQualificationGrade(from.getQualificationGrade());
        to.setQualificationAwardDate(from.getQualificationAwardDate());

        to.setProofOfAward(copyDocument(from.getProofOfAward()));

    }

    private void copyPersonalDetailsData(PersonalDetails to, PersonalDetails from) {
        to.setTitle(from.getTitle());
        to.setGender(from.getGender());
        to.setDateOfBirth(from.getDateOfBirth());
        to.setCountry(getEnabledImportedObject(from.getCountry()));
        to.setFirstNationality(getEnabledImportedObject(from.getFirstNationality()));
        to.setSecondNationality(getEnabledImportedObject(from.getSecondNationality()));
        to.setEnglishFirstLanguage(from.getEnglishFirstLanguage());
        to.setLanguageQualificationAvailable(from.getLanguageQualificationAvailable());

        LanguageQualification previousLanguageQualification = from.getLanguageQualification();

        if (previousLanguageQualification != null) {
            LanguageQualification languageQualification = new LanguageQualification();
            copyLanguageQualification(languageQualification, previousLanguageQualification);
            to.setLanguageQualification(languageQualification);
            languageQualification.setPersonalDetails(to);
        }

        to.setResidenceCountry(getEnabledImportedObject(from.getResidenceCountry()));
        to.setRequiresVisa(from.getRequiresVisa());
        to.setPassportAvailable(from.getPassportAvailable());

        PassportInformation previousPassportInformation = from.getPassportInformation();
        if (previousPassportInformation != null) {
            PassportInformation passportInformation = new PassportInformation();
            copyPassportInformation(passportInformation, previousPassportInformation);
            to.setPassportInformation(passportInformation);
            passportInformation.setPersonalDetails(to);
        }

        to.setPhoneNumber(from.getPhoneNumber());
        to.setMessenger(from.getMessenger());
        to.setEthnicity(getEnabledImportedObject(from.getEthnicity()));
        to.setDisability(getEnabledImportedObject(from.getDisability()));
    }

    private void copyPassportInformation(PassportInformation to, PassportInformation from) {
        to.setPassportNumber(from.getPassportNumber());
        to.setNameOnPassport(from.getNameOnPassport());
        to.setPassportIssueDate(from.getPassportIssueDate());
        to.setPassportExpiryDate(from.getPassportExpiryDate());
    }

    private void copyLanguageQualification(LanguageQualification to, LanguageQualification from) {
        to.setQualificationType(from.getQualificationType());
        to.setOtherQualificationTypeName(from.getOtherQualificationTypeName());
        to.setDateOfExamination(from.getDateOfExamination());
        to.setOverallScore(from.getOverallScore());
        to.setReadingScore(from.getReadingScore());
        to.setWritingScore(from.getWritingScore());
        to.setSpeakingScore(from.getSpeakingScore());
        to.setListeningScore(from.getListeningScore());
        to.setExamTakenOnline(from.getExamTakenOnline());
        to.setLanguageQualificationDocument(copyDocument(from.getLanguageQualificationDocument()));
    }

    private Address copyAddress(Address from) {
        if (from == null) {
            return null;
        }
        Address to = new Address();
        to.setAddress1(from.getAddress1());
        to.setAddress2(from.getAddress2());
        to.setAddress3(from.getAddress3());
        to.setAddress4(from.getAddress4());
        to.setAddress5(from.getAddress5());
        to.setDomicile(getEnabledImportedObject(from.getDomicile()));

        return to;
    }

    private Document copyDocument(Document from) {
        if (from == null) {
            return null;
        }
        Document to = new Document();
        to.setUploadedBy(from.getUploadedBy());
        to.setType(from.getType());
        to.setContentType(from.getContentType());
        to.setFileName(from.getFileName());
        to.setContent(from.getContent());

        documentDAO.save(to);

        return to;
    }

    @SuppressWarnings("unchecked")
    private <T extends SelfReferringImportedObject> T getEnabledImportedObject(T object) {
        if (object == null || object.getEnabled() || object.getEnabledObject() == null) {
            return object;
        }
        return (T) object.getEnabledObject();
    }

    void setDocumentDAO(DocumentDAO documentDAO) {
        this.documentDAO = documentDAO;
    }

}