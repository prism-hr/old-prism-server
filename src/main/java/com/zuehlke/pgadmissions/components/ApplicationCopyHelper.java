package com.zuehlke.pgadmissions.components;

import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.application.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.application.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationFunding;
import com.zuehlke.pgadmissions.domain.application.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationPassport;
import com.zuehlke.pgadmissions.domain.application.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationPrize;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.application.ApplicationSection;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.user.Address;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ApplicationCopyHelper {

    @Autowired
    private ImportedEntityService importedEntityService;

    @Autowired
    private CustomizationService customizationService;

    private final Set<ApplicationSection> sectionsWithErrors = Sets.newHashSet();

    @Transactional
    public void copyApplication(Application to, Application from) {
        copyApplicationPersonalDetail(to, from);
        copyApplicationAddress(to, from);
        copyApplicationQualifications(to, from);
        copyApplicationEmploymentPositions(to, from);
        copyApplicationFundings(to, from);
        copyApplicationPrizes(to, from);
        copyApplicationReferences(to, from);
        copyApplicationDocument(to, from);
        copyApplicationAdditionalInformation(to, from);

        for (ApplicationSection sectionWithError : sectionsWithErrors) {
            sectionWithError.setLastEditedTimestamp(null);
        }
    }

    private void copyApplicationPersonalDetail(Application to, Application from) {
        if (from.getPersonalDetail() != null) {
            ApplicationPersonalDetail personalDetail = new ApplicationPersonalDetail();
            to.setPersonalDetail(personalDetail);
            personalDetail.setApplication(to);

            Institution toInstitution = to.getInstitution();
            personalDetail.setTitle(getEnabledImportedObject(toInstitution, from.getPersonalDetail().getTitle(), personalDetail));
            personalDetail.setGender(getEnabledImportedObject(toInstitution, from.getPersonalDetail().getGender(), personalDetail));
            personalDetail.setDateOfBirth(from.getPersonalDetail().getDateOfBirth());
            personalDetail.setCountry(getEnabledImportedObject(toInstitution, from.getPersonalDetail().getCountry(), personalDetail));
            personalDetail.setFirstNationality(getEnabledImportedObject(toInstitution, from.getPersonalDetail().getFirstNationality(), personalDetail));
            personalDetail.setSecondNationality(getEnabledImportedObject(toInstitution, from.getPersonalDetail().getSecondNationality(), personalDetail));
            personalDetail.setDomicile(getEnabledImportedObject(toInstitution, from.getPersonalDetail().getDomicile(), personalDetail));
            personalDetail.setPhone(from.getPersonalDetail().getPhone());
            personalDetail.setSkype(from.getPersonalDetail().getSkype());

            if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, to, PrismWorkflowPropertyDefinition.APPLICATION_DEMOGRAPHIC)) {
                personalDetail.setEthnicity(getEnabledImportedObject(toInstitution, from.getPersonalDetail().getEthnicity(), personalDetail));
                personalDetail.setDisability(getEnabledImportedObject(toInstitution, from.getPersonalDetail().getDisability(), personalDetail));
            }

            if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, to, PrismWorkflowPropertyDefinition.APPLICATION_LANGUAGE)) {
                personalDetail.setFirstLanguageLocale(from.getPersonalDetail().getFirstLanguageLocale());
                personalDetail.setLanguageQualification(copyLanguageQualification(toInstitution, from.getPersonalDetail().getLanguageQualification(), to));
            }

            if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, to, PrismWorkflowPropertyDefinition.APPLICATION_RESIDENCE)) {
                personalDetail.setVisaRequired(from.getPersonalDetail().getVisaRequired());
                personalDetail.setPassport(copyPassport(from.getPersonalDetail().getPassport()));
            }

            personalDetail.setLastEditedTimestamp(new DateTime());
        }
    }

    private void copyApplicationAddress(Application to, Application from) {
        if (from.getAddress() != null) {
            ApplicationAddress applicationAddress = new ApplicationAddress();
            to.setAddress(applicationAddress);
            applicationAddress.setApplication(to);
            Institution toInstitution = to.getInstitution();
            applicationAddress.setCurrentAddress(copyAddress(toInstitution, from.getAddress().getCurrentAddress(), applicationAddress));
            applicationAddress.setContactAddress(copyAddress(toInstitution, from.getAddress().getContactAddress(), applicationAddress));
            applicationAddress.setLastEditedTimestamp(new DateTime());
        }
    }

    private void copyApplicationQualifications(Application to, Application from) {
        WorkflowPropertyConfiguration qualificationConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                PrismConfiguration.WORKFLOW_PROPERTY, PrismWorkflowPropertyDefinition.APPLICATION_QUALIFICATION, to.getWorkflowPropertyConfigurationVersion());

        if (BooleanUtils.isTrue(qualificationConfiguration.getEnabled())) {
            boolean qualificationProofOfAwardEnabled = customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, to,
                    PrismWorkflowPropertyDefinition.APPLICATION_QUALIFICATION_PROOF_OF_AWARD);

            Integer counter = 0;
            for (ApplicationQualification fromQualification : from.getQualifications()) {
                if (counter.equals(qualificationConfiguration.getMaximum())) {
                    break;
                }
                ApplicationQualification qualification = new ApplicationQualification();
                to.getQualifications().add(qualification);
                qualification.setApplication(to);
                copyQualification(qualification, fromQualification, qualificationProofOfAwardEnabled);
                counter++;
            }
        }
    }

    private void copyApplicationEmploymentPositions(Application to, Application from) {
        WorkflowPropertyConfiguration employmentConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                PrismConfiguration.WORKFLOW_PROPERTY, PrismWorkflowPropertyDefinition.APPLICATION_EMPLOYMENT_POSITION,
                to.getWorkflowPropertyConfigurationVersion());

        if (BooleanUtils.isTrue(employmentConfiguration.getEnabled())) {
            Integer counter = 0;
            for (ApplicationEmploymentPosition fromEmployment : from.getEmploymentPositions()) {
                if (counter.equals(employmentConfiguration.getMaximum())) {
                    break;
                }
                ApplicationEmploymentPosition employment = new ApplicationEmploymentPosition();
                to.getEmploymentPositions().add(employment);
                employment.setApplication(to);
                copyEmploymentPosition(employment, fromEmployment);
                counter++;
            }
        }
    }

    private void copyApplicationFundings(Application to, Application from) {
        WorkflowPropertyConfiguration fundingConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                PrismConfiguration.WORKFLOW_PROPERTY, PrismWorkflowPropertyDefinition.APPLICATION_FUNDING, to.getWorkflowPropertyConfigurationVersion());

        if (BooleanUtils.isTrue(fundingConfiguration.getEnabled())) {
            boolean fundingProofOfAwardEnabled = customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, to,
                    PrismWorkflowPropertyDefinition.APPLICATION_FUNDING_PROOF_OF_AWARD);

            Integer counter = 0;
            for (ApplicationFunding fromFunding : from.getFundings()) {
                if (counter.equals(fundingConfiguration.getMaximum())) {
                    break;
                }
                ApplicationFunding funding = new ApplicationFunding();
                to.getFundings().add(funding);
                funding.setApplication(to);
                copyFunding(funding, fromFunding, fundingProofOfAwardEnabled);
                counter++;
            }
        }
    }

    private void copyApplicationPrizes(Application to, Application from) {
        WorkflowPropertyConfiguration prizeConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                PrismConfiguration.WORKFLOW_PROPERTY, PrismWorkflowPropertyDefinition.APPLICATION_PRIZE, to.getWorkflowPropertyConfigurationVersion());

        if (BooleanUtils.isTrue(prizeConfiguration.getEnabled())) {
            Integer counter = 0;
            for (ApplicationPrize fromPrize : from.getPrizes()) {
                if (counter.equals(prizeConfiguration.getMaximum())) {
                    break;
                }
                ApplicationPrize prize = new ApplicationPrize();
                to.getPrizes().add(prize);
                prize.setApplication(to);
                copyPrize(prize, fromPrize);
                counter++;
            }
        }
    }

    private void copyApplicationReferences(Application to, Application from) {
        WorkflowPropertyConfiguration refereeConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                PrismConfiguration.WORKFLOW_PROPERTY, PrismWorkflowPropertyDefinition.APPLICATION_ASSIGN_REFEREE, to.getWorkflowPropertyConfigurationVersion());

        if (BooleanUtils.isTrue(refereeConfiguration.getEnabled())) {
            Integer counter = 0;
            for (ApplicationReferee fromReferee : from.getReferees()) {
                if (counter.equals(refereeConfiguration.getMaximum())) {
                    break;
                }
                ApplicationReferee referee = new ApplicationReferee();
                to.getReferees().add(referee);
                referee.setApplication(to);
                copyReferee(referee, fromReferee);
                counter++;
            }
        }
    }

    private void copyApplicationDocument(Application to, Application from) {
        boolean personalStatementEnabled = customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, to,
                PrismWorkflowPropertyDefinition.APPLICATION_DOCUMENT_PERSONAL_STATEMENT);

        boolean cvEnabled = customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, to,
                PrismWorkflowPropertyDefinition.APPLICATION_DOCUMENT_CV);

        boolean researchStatementEnabled = customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, to,
                PrismWorkflowPropertyDefinition.APPLICATION_DOCUMENT_RESEARCH_STATEMENT);

        boolean coveringLetterEnabled = customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, to,
                PrismWorkflowPropertyDefinition.APPLICATION_DOCUMENT_COVERING_LETTER);

        if (personalStatementEnabled || cvEnabled || researchStatementEnabled || coveringLetterEnabled) {
            if (from.getDocument() != null) {
                ApplicationDocument applicationDocument = new ApplicationDocument();
                to.setDocument(applicationDocument);
                applicationDocument.setApplication(to);

                if (personalStatementEnabled) {
                    applicationDocument.setPersonalStatement(copyDocument(from.getDocument().getCoveringLetter()));
                }

                if (cvEnabled) {
                    applicationDocument.setCv(copyDocument(from.getDocument().getCv()));
                }

                if (researchStatementEnabled) {
                    applicationDocument.setResearchStatement(copyDocument(from.getDocument().getResearchStatement()));
                }

                if (coveringLetterEnabled) {
                    applicationDocument.setCoveringLetter(copyDocument(from.getDocument().getCoveringLetter()));
                }
                applicationDocument.setLastEditedTimestamp(new DateTime());
            }
        }
    }

    private void copyApplicationAdditionalInformation(Application to, Application from) {
        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, to,
                PrismWorkflowPropertyDefinition.APPLICATION_CRIMINAL_CONVICTION) && from.getAdditionalInformation() != null) {
            ApplicationAdditionalInformation additionalInformation = new ApplicationAdditionalInformation();
            to.setAdditionalInformation(additionalInformation);
            additionalInformation.setApplication(to);
            additionalInformation.setConvictionsText(from.getAdditionalInformation().getConvictionsText());
            additionalInformation.setLastEditedTimestamp(new DateTime());
        }
    }

    public void copyReferee(ApplicationReferee to, ApplicationReferee from) {
        Institution toInstitution = to.getApplication().getInstitution();
        to.setUser(from.getUser());
        to.setJobEmployer(from.getJobEmployer());
        to.setJobTitle(from.getJobTitle());
        to.setPhone(from.getPhone());
        to.setSkype(from.getSkype());
        to.setAddress(copyAddress(toInstitution, from.getAddress(), to));
        to.setLastEditedTimestamp(new DateTime());
    }

    public void copyFunding(ApplicationFunding to, ApplicationFunding from, boolean proofOfAwardEnabled) {
        Institution toInstitution = to.getApplication().getInstitution();
        to.setFundingSource(getEnabledImportedObject(toInstitution, from.getFundingSource(), to));
        to.setDescription(from.getDescription());
        to.setValue(from.getValue());
        to.setAwardDate(from.getAwardDate());

        if (proofOfAwardEnabled) {
            to.setDocument(copyDocument(from.getDocument()));
        }

        to.setLastEditedTimestamp(new DateTime());
    }

    public void copyPrize(ApplicationPrize to, ApplicationPrize from) {
        to.setProvider(from.getProvider());
        to.setTitle(from.getTitle());
        to.setDescription(from.getDescription());
        to.setAwardDate(from.getAwardDate());
        to.setLastEditedTimestamp(new DateTime());
    }

    public void copyEmploymentPosition(ApplicationEmploymentPosition to, ApplicationEmploymentPosition from) {
        Institution toInstitution = to.getApplication().getInstitution();
        to.setEmployerName(from.getEmployerName());
        to.setPosition(from.getPosition());
        to.setRemit(from.getRemit());
        to.setStartDate(from.getStartDate());
        to.setCurrent(from.getCurrent());
        to.setEndDate(from.getEndDate());
        to.setEmployerAddress(copyAddress(toInstitution, from.getEmployerAddress(), to));
        to.setLastEditedTimestamp(new DateTime());
    }

    public void copyQualification(ApplicationQualification to, ApplicationQualification from, boolean proofOfAwardEnabled) {
        Institution toInstitution = to.getApplication().getInstitution();
        to.setInstitution(getEnabledImportedObject(toInstitution, from.getInstitution(), to));
        to.setType(getEnabledImportedObject(toInstitution, from.getType(), to));
        to.setTitle(from.getTitle());
        to.setSubject(from.getSubject());
        to.setLanguage(from.getLanguage());
        to.setStartDate(from.getStartDate());
        to.setCompleted(from.getCompleted());
        to.setGrade(from.getGrade());
        to.setAwardDate(from.getAwardDate());

        if (proofOfAwardEnabled) {
            to.setDocument(copyDocument(from.getDocument()));
        }

        to.setLastEditedTimestamp(new DateTime());
    }

    private Address copyAddress(Institution toInstitution, Address fromAddress, ApplicationSection toSection) {
        if (fromAddress == null) {
            return null;
        }
        Address toAddress = new Address();
        toAddress.setAddressLine1(fromAddress.getAddressLine1());
        toAddress.setAddressLine2(fromAddress.getAddressLine2());
        toAddress.setAddressTown(fromAddress.getAddressTown());
        toAddress.setAddressRegion(fromAddress.getAddressRegion());
        toAddress.setAddressCode(fromAddress.getAddressCode());
        toAddress.setDomicile(getEnabledImportedObject(toInstitution, fromAddress.getDomicile(), toSection));
        return toAddress;
    }

    private Document copyDocument(Document from) {
        if (from == null) {
            return null;
        }
        Document to = new Document();
        to.setContentType(from.getContentType());
        to.setFileName(from.getFileName());
        to.setCategory(from.getCategory());
        to.setContent(from.getContent());
        to.setUser(from.getUser());
        to.setCreatedTimestamp(new DateTime());
        return to;
    }

    private ApplicationLanguageQualification copyLanguageQualification(Institution toInstitution, ApplicationLanguageQualification from,
            Application toApplication) {
        if (from == null) {
            return null;
        }
        ApplicationLanguageQualification to = new ApplicationLanguageQualification();
        to.setType(getEnabledImportedObject(toInstitution, from.getType(), to));
        to.setExamDate(from.getExamDate());
        to.setOverallScore(from.getOverallScore());
        to.setReadingScore(from.getReadingScore());
        to.setWritingScore(from.getWritingScore());
        to.setSpeakingScore(from.getSpeakingScore());
        to.setListeningScore(from.getListeningScore());

        if (customizationService.isConfigurationEnabled(PrismConfiguration.WORKFLOW_PROPERTY, toApplication,
                PrismWorkflowPropertyDefinition.APPLICATION_LANGUAGE_PROOF_OF_AWARD)) {
            to.setDocument(copyDocument(from.getDocument()));
        }

        to.setLastEditedTimestamp(new DateTime());
        return to;
    }

    private ApplicationPassport copyPassport(ApplicationPassport from) {
        if (from == null) {
            return null;
        }
        ApplicationPassport to = new ApplicationPassport();
        to.setNumber(from.getNumber());
        to.setName(from.getName());
        to.setIssueDate(from.getIssueDate());
        to.setExpiryDate(from.getExpiryDate());
        to.setLastEditedTimestamp(new DateTime());
        return to;
    }

    private <T extends ImportedEntity> T getEnabledImportedObject(Institution toInstitution, T fromEntity, ApplicationSection toSection) {
        T toEntity = null;
        if (fromEntity == null) {
            toEntity = null;
        } else {
            Institution fromInstitution = fromEntity.getInstitution();
            if (fromEntity.getEnabled() && fromInstitution == toInstitution) {
                toEntity = fromEntity;
            } else if (fromInstitution != toInstitution) {
                toEntity = importedEntityService.getCorrespondingImportedEntity(toInstitution, fromEntity);
            }
        }

        if (fromEntity != null && toEntity == null) {
            sectionsWithErrors.add(toSection);
        }

        return toEntity;
    }

}
