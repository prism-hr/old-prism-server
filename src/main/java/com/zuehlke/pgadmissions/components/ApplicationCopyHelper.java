package com.zuehlke.pgadmissions.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.ApplicationFunding;
import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.Passport;
import com.zuehlke.pgadmissions.domain.ApplicationPersonalDetails;
import com.zuehlke.pgadmissions.domain.ApplicationProgramDetails;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.services.DocumentService;

@Component
public class ApplicationCopyHelper {

    @Autowired
    DocumentService documentService;

    @Transactional
    public void copyApplicationFormData(Application to, Application from) {
        if (from.getPersonalDetails() != null) {
            ApplicationPersonalDetails personalDetails = new ApplicationPersonalDetails();
            to.setPersonalDetails(personalDetails);
            personalDetails.setApplication(to);
            copyPersonalDetails(to.getPersonalDetails(), from.getPersonalDetails(), true);
        }

        if (from.getApplicationAddress() != null) {
            ApplicationAddress applicationFormAddress = new ApplicationAddress();
            to.setApplicationAddress(applicationFormAddress);
            applicationFormAddress.setApplication(to);
            copyApplicationFormAddress(to.getApplicationAddress(), from.getApplicationAddress(), true);
        }

        for (ApplicationQualification fromQualification : from.getApplicationQualifications()) {
            ApplicationQualification qualification = new ApplicationQualification();
            to.getApplicationQualifications().add(qualification);
            qualification.setApplication(to);
            copyQualification(qualification, fromQualification, false);
        }

        for (ApplicationEmploymentPosition fromEmployment : from.getApplicationEmploymentPositions()) {
            ApplicationEmploymentPosition employment = new ApplicationEmploymentPosition();
            to.getApplicationEmploymentPositions().add(employment);
            employment.setApplication(to);
            copyEmploymentPosition(employment, fromEmployment, true);
        }

        for (ApplicationFunding fromFunding : from.getApplicationFundings()) {
            ApplicationFunding funding = new ApplicationFunding();
            to.getApplicationFundings().add(funding);
            funding.setApplication(to);
            copyFunding(funding, fromFunding, true);
        }

        for (Referee fromReferee : from.getApplicationReferees()) {
            Referee referee = new Referee();
            to.getApplicationReferees().add(referee);
            referee.setApplication(to);
            copyReferee(referee, fromReferee, true);
        }

        if (from.getApplicationDocument() != null) {
            ApplicationDocument applicationFormDocument = new ApplicationDocument();
            to.setApplicationDocument(applicationFormDocument);
            applicationFormDocument.setApplication(to);
            copyApplicationFormDocument(to.getApplicationDocument(), from.getApplicationDocument(), true);
        }

        if (from.getAdditionalInformation() != null) {
            ApplicationAdditionalInformation additionalInformation = new ApplicationAdditionalInformation();
            to.setAdditionalInformation(additionalInformation);
            additionalInformation.setApplication(to);
            copyAdditionalInformation(additionalInformation, from.getAdditionalInformation());
        }
    }

    public void copyProgramDetails(ApplicationProgramDetails to, ApplicationProgramDetails from) {
        to.setStudyOption(from.getStudyOption());
        to.setStartDate(from.getStartDate());
        to.setSourceOfInterest(from.getSourceOfInterest());
        to.setSourceOfInterestText(from.getSourceOfInterestText());
        to.getSuggestedSupervisors().addAll(from.getSuggestedSupervisors());
    }

    public void copyAdditionalInformation(ApplicationAdditionalInformation to, ApplicationAdditionalInformation from) {
        to.setHasConvictions(from.getHasConvictions());
        to.setConvictionsText(from.getConvictionsText());
    }

    public void copyReferee(Referee to, Referee from, boolean doPerformDeepCopy) {
        to.setUser(from.getUser());
        to.setJobEmployer(from.getJobEmployer());
        to.setJobTitle(from.getJobTitle());
        to.setPhoneNumber(from.getPhoneNumber());
        to.setSkype(from.getSkype());
        if (doPerformDeepCopy) {
            to.setAddress(copyAddress(from.getAddress()));
        } else {
            to.setAddress(from.getAddress());
        }
    }

    public void copyFunding(ApplicationFunding to, ApplicationFunding from, boolean doPerformDeepCopy) {
        to.setFundingSource(from.getFundingSource());
        to.setDescription(from.getDescription());
        to.setValue(from.getValue());
        to.setAwardDate(from.getAwardDate());
        if (doPerformDeepCopy) {
            to.setDocument(copyDocument(from.getDocument()));
        } else {
            documentService.replaceDocument(from.getDocument(), to.getDocument());
            to.setDocument(from.getDocument());
        }
    }

    public void copyEmploymentPosition(ApplicationEmploymentPosition to, ApplicationEmploymentPosition from, boolean doPerformDeepCopy) {
        to.setEmployerName(from.getEmployerName());
        to.setPosition(from.getPosition());
        to.setRemit(from.getRemit());
        to.setStartDate(from.getStartDate());
        to.setCurrent(from.isCurrent());
        to.setEndDate(from.getEndDate());
        if (doPerformDeepCopy) {
            to.setEmployerAddress(copyAddress(from.getEmployerAddress()));
        } else {
            to.setEmployerAddress(from.getEmployerAddress());
        }
    }

    public void copyQualification(ApplicationQualification to, ApplicationQualification from, boolean doPerformDeepCopy) {
        to.setInstitution(from.getInstitution());
        to.setType(getEnabledImportedObject(from.getType()));
        to.setTitle(from.getTitle());
        to.setSubject(from.getSubject());
        to.setLanguage(from.getLanguage());
        to.setStartDate(from.getStartDate());
        to.setCompleted(from.getCompleted());
        to.setGrade(from.getGrade());
        to.setAwardDate(from.getAwardDate());
        if (doPerformDeepCopy) {
            to.setDocument(copyDocument(from.getDocument()));
        } else {
            documentService.replaceDocument(from.getDocument(), to.getDocument());
            to.setDocument(from.getDocument());
        }
    }

    public void copyPersonalDetails(ApplicationPersonalDetails to, ApplicationPersonalDetails from, boolean doPerformDeepCopy) {
        to.setTitle(from.getTitle());
        to.setGender(from.getGender());
        to.setDateOfBirth(from.getDateOfBirth());
        to.setCountry(getEnabledImportedObject(from.getCountry()));
        to.setFirstNationality(getEnabledImportedObject(from.getFirstNationality()));
        to.setSecondNationality(getEnabledImportedObject(from.getSecondNationality()));
        to.setFirstLanguageEnglish(from.getFirstLanguageEnglish());
        to.setLanguageQualificationAvailable(from.getLanguageQualificationAvailable());
        to.setResidenceCountry(getEnabledImportedObject(from.getResidenceCountry()));
        to.setVisaRequired(from.getVisaRequired());
        to.setPassportAvailable(from.getPassportAvailable());
        to.setPhoneNumber(from.getPhoneNumber());
        to.setMessenger(from.getMessenger());
        to.setEthnicity(getEnabledImportedObject(from.getEthnicity()));
        to.setDisability(getEnabledImportedObject(from.getDisability()));
        if (doPerformDeepCopy) {
            to.setLanguageQualification(copyLanguageQualification(from.getLanguageQualification()));
            to.setPassport(copyPassport(from.getPassport()));
        } else {
            ApplicationLanguageQualification toQualification = to.getLanguageQualification();
            ApplicationLanguageQualification fromQualification = from.getLanguageQualification();
            Document toQualificationDocument = null;
            if (toQualification != null) {
                toQualificationDocument = toQualification.getProofOfAward();
            }
            Document fromQualificationDocument = null;
            if (fromQualification != null) {
                fromQualificationDocument = fromQualification.getProofOfAward();
            }
            documentService.replaceDocument(fromQualificationDocument, toQualificationDocument);
            to.setLanguageQualification(from.getLanguageQualification());
            to.setPassport(from.getPassport());
        }
    }

    public void copyApplicationFormAddress(ApplicationAddress to, ApplicationAddress from, boolean doPerformDeepCopy) {
        if (doPerformDeepCopy) {
            to.setCurrentAddress(copyAddress(from.getCurrentAddress()));
            to.setContactAddress(copyAddress(from.getContactAddress()));
        } else {
            to.setCurrentAddress(from.getCurrentAddress());
            to.setContactAddress(from.getContactAddress());
        }
    }

    public void copyApplicationFormDocument(ApplicationDocument to, ApplicationDocument from, boolean doPerformDeepCopy) {
        if (doPerformDeepCopy) {
            to.setCv(copyDocument(from.getCv()));
            to.setPersonalStatement(copyDocument(from.getPersonalStatement()));
        } else {
            to.setCv(from.getCv());
            documentService.replaceDocument(from.getCv(), to.getCv());
            to.setPersonalStatement(from.getPersonalStatement());
            documentService.replaceDocument(from.getPersonalStatement(), to.getPersonalStatement());
        }
    }

    private Address copyAddress(Address from) {
        if (from == null) {
            return null;
        }
        Address to = new Address();
        to.setAddressLine1(from.getAddressLine1());
        to.setAddressLine2(from.getAddressLine2());
        to.setAddressTown(from.getAddressTown());
        to.setAddressRegion(from.getAddressRegion());
        to.setAddressCode(from.getAddressCode());
        to.setDomicile(getEnabledImportedObject(from.getDomicile()));
        return to;
    }

    private Document copyDocument(Document from) {
        if (from == null) {
            return null;
        }
        Document to = new Document();
        to.setType(from.getType());
        to.setContentType(from.getContentType());
        to.setFileName(from.getFileName());
        to.setContent(from.getContent());
        to.setIsReferenced(true);
        return to;
    }

    private ApplicationLanguageQualification copyLanguageQualification(ApplicationLanguageQualification from) {
        if (from == null) {
            return null;
        }
        ApplicationLanguageQualification to = new ApplicationLanguageQualification();
        to.setLanguageQualificationType(from.getLanguageQualificationType());
        to.setExamDate(from.getExamDate());
        to.setOverallScore(from.getOverallScore());
        to.setReadingScore(from.getReadingScore());
        to.setWritingScore(from.getWritingScore());
        to.setSpeakingScore(from.getSpeakingScore());
        to.setListeningScore(from.getListeningScore());
        to.setProofOfAward(copyDocument(from.getProofOfAward()));
        return to;
    }

    private Passport copyPassport(Passport from) {
        if (from == null) {
            return null;
        }
        Passport to = new Passport();
        to.setNumber(from.getNumber());
        to.setName(from.getName());
        to.setIssueDate(from.getIssueDate());
        to.setExpiryDate(from.getExpiryDate());
        return to;
    }

    private <T extends ImportedEntity> T getEnabledImportedObject(T object) {
        if (object == null || object.isEnabled()) {
            return object;
        }
        return null;
    }

}
