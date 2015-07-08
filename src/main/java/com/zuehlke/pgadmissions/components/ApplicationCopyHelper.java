package com.zuehlke.pgadmissions.components;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.WORKFLOW_PROPERTY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_ASSIGN_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_CRIMINAL_CONVICTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_DEMOGRAPHIC;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_DOCUMENT_COVERING_LETTER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_DOCUMENT_CV;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_DOCUMENT_PERSONAL_STATEMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_DOCUMENT_RESEARCH_STATEMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_EMPLOYMENT_POSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_FUNDING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_FUNDING_PROOF_OF_AWARD;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_LANGUAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_LANGUAGE_PROOF_OF_AWARD;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_PRIZE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_QUALIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_QUALIFICATION_PROOF_OF_AWARD;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_RESIDENCE;

import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.address.AddressApplication;
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
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.DocumentService;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ApplicationCopyHelper {

    @Inject
    private CustomizationService customizationService;

    @Inject
    private DocumentService documentService;

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
            sectionWithError.setLastUpdatedTimestamp(null);
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

            WorkflowPropertyConfiguration demographicConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                    WORKFLOW_PROPERTY, APPLICATION_DEMOGRAPHIC, to.getWorkflowPropertyConfigurationVersion());

            if (BooleanUtils.isTrue(demographicConfiguration.getEnabled())) {
                ImportedEntitySimple ethnicity = from.getPersonalDetail().getEthnicity();
                ImportedEntitySimple disability = from.getPersonalDetail().getDisability();

                personalDetail.setEthnicity(getEnabledImportedObject(toInstitution, ethnicity, personalDetail));
                personalDetail.setDisability(getEnabledImportedObject(toInstitution, disability, personalDetail));

                if (BooleanUtils.isTrue(demographicConfiguration.getRequired()) && (ethnicity == null || disability == null)) {
                    sectionsWithErrors.add(personalDetail);
                }
            }

            WorkflowPropertyConfiguration languageConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                    WORKFLOW_PROPERTY, APPLICATION_LANGUAGE, to.getWorkflowPropertyConfigurationVersion());

            if (BooleanUtils.isTrue(languageConfiguration.getEnabled())) {
                Boolean firstLanguageLocale = from.getPersonalDetail().getFirstLanguageLocale();
                personalDetail.setFirstLanguageLocale(firstLanguageLocale);
                personalDetail.setLanguageQualification(copyLanguageQualification(toInstitution, from.getPersonalDetail().getLanguageQualification(), to));

                if (BooleanUtils.isTrue(languageConfiguration.getRequired()) && firstLanguageLocale == null) {
                    sectionsWithErrors.add(personalDetail);
                }
            }

            WorkflowPropertyConfiguration residenceConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                    WORKFLOW_PROPERTY, APPLICATION_RESIDENCE, to.getWorkflowPropertyConfigurationVersion());

            if (BooleanUtils.isTrue(residenceConfiguration.getEnabled())) {
                Boolean visaRequired = from.getPersonalDetail().getVisaRequired();
                personalDetail.setVisaRequired(visaRequired);
                personalDetail.setPassport(copyPassport(from.getPersonalDetail().getPassport()));

                if (BooleanUtils.isTrue(residenceConfiguration.getRequired()) && visaRequired == null) {
                    sectionsWithErrors.add(personalDetail);
                }
            }

            personalDetail.setLastUpdatedTimestamp(new DateTime());
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
            applicationAddress.setLastUpdatedTimestamp(new DateTime());
        }
    }

    private void copyApplicationQualifications(Application to, Application from) {
        WorkflowPropertyConfiguration qualificationConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                WORKFLOW_PROPERTY, APPLICATION_QUALIFICATION, to.getWorkflowPropertyConfigurationVersion());

        if (BooleanUtils.isTrue(qualificationConfiguration.getEnabled())) {
            WorkflowPropertyConfiguration qualificationDocumentConfiguration = (WorkflowPropertyConfiguration) customizationService
                    .getConfigurationWithVersion(
                            WORKFLOW_PROPERTY, APPLICATION_QUALIFICATION_PROOF_OF_AWARD, to.getWorkflowPropertyConfigurationVersion());

            Integer counter = 0;
            for (ApplicationQualification fromQualification : from.getQualifications()) {
                if (counter.equals(qualificationConfiguration.getMaximum())) {
                    break;
                }
                ApplicationQualification qualification = new ApplicationQualification();
                to.getQualifications().add(qualification);
                qualification.setApplication(to);
                copyQualification(qualification, fromQualification, qualificationDocumentConfiguration);
                counter++;
            }
        }
    }

    private void copyApplicationEmploymentPositions(Application to, Application from) {
        WorkflowPropertyConfiguration employmentConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                WORKFLOW_PROPERTY, APPLICATION_EMPLOYMENT_POSITION, to.getWorkflowPropertyConfigurationVersion());

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
                WORKFLOW_PROPERTY, APPLICATION_FUNDING, to.getWorkflowPropertyConfigurationVersion());

        if (BooleanUtils.isTrue(fundingConfiguration.getEnabled())) {
            boolean fundingDocumentEnabled = customizationService.isWorkflowConfigurationEnabled(to, APPLICATION_FUNDING_PROOF_OF_AWARD);

            Integer counter = 0;
            for (ApplicationFunding fromFunding : from.getFundings()) {
                if (counter.equals(fundingConfiguration.getMaximum())) {
                    break;
                }
                ApplicationFunding funding = new ApplicationFunding();
                to.getFundings().add(funding);
                funding.setApplication(to);
                copyFunding(funding, fromFunding, fundingDocumentEnabled);
                counter++;
            }
        }
    }

    private void copyApplicationPrizes(Application to, Application from) {
        WorkflowPropertyConfiguration prizeConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                WORKFLOW_PROPERTY, APPLICATION_PRIZE, to.getWorkflowPropertyConfigurationVersion());

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
                WORKFLOW_PROPERTY, APPLICATION_ASSIGN_REFEREE, to.getWorkflowPropertyConfigurationVersion());

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
        WorkflowPropertyConfiguration personalStatementConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                WORKFLOW_PROPERTY, APPLICATION_DOCUMENT_PERSONAL_STATEMENT, to.getWorkflowPropertyConfigurationVersion());
        boolean personalStatementEnabled = BooleanUtils.isTrue(personalStatementConfiguration.getEnabled());

        WorkflowPropertyConfiguration cvConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                WORKFLOW_PROPERTY, APPLICATION_DOCUMENT_CV, to.getWorkflowPropertyConfigurationVersion());
        boolean cvEnabled = BooleanUtils.isTrue(cvConfiguration.getEnabled());

        WorkflowPropertyConfiguration researchStatementConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                WORKFLOW_PROPERTY, APPLICATION_DOCUMENT_RESEARCH_STATEMENT, to.getWorkflowPropertyConfigurationVersion());
        boolean researchStatementEnabled = BooleanUtils.isTrue(researchStatementConfiguration.getEnabled());

        WorkflowPropertyConfiguration coveringLetterConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                WORKFLOW_PROPERTY, APPLICATION_DOCUMENT_COVERING_LETTER, to.getWorkflowPropertyConfigurationVersion());
        boolean coveringLetterEnabled = BooleanUtils.isTrue(coveringLetterConfiguration.getEnabled());

        if (personalStatementEnabled || cvEnabled || researchStatementEnabled || coveringLetterEnabled) {
            if (from.getDocument() != null) {
                ApplicationDocument applicationDocument = new ApplicationDocument();
                to.setDocument(applicationDocument);
                applicationDocument.setApplication(to);

                if (personalStatementEnabled) {
                    Document document = from.getDocument().getPersonalStatement();
                    applicationDocument.setPersonalStatement(copyDocument(document));

                    if (BooleanUtils.isTrue(personalStatementConfiguration.getRequired()) && document == null) {
                        sectionsWithErrors.add(applicationDocument);
                    }
                }

                if (cvEnabled) {
                    Document document = from.getDocument().getCv();
                    applicationDocument.setCv(copyDocument(document));

                    if (BooleanUtils.isTrue(cvConfiguration.getRequired()) && document == null) {
                        sectionsWithErrors.add(applicationDocument);
                    }
                }

                if (researchStatementEnabled) {
                    Document document = from.getDocument().getResearchStatement();
                    applicationDocument.setResearchStatement(copyDocument(document));

                    if (BooleanUtils.isTrue(researchStatementConfiguration.getRequired()) && document == null) {
                        sectionsWithErrors.add(applicationDocument);
                    }
                }

                if (coveringLetterEnabled) {
                    Document document = from.getDocument().getCoveringLetter();
                    applicationDocument.setCoveringLetter(copyDocument(document));

                    if (BooleanUtils.isTrue(coveringLetterConfiguration.getRequired()) && document == null) {
                        sectionsWithErrors.add(applicationDocument);
                    }
                }

                applicationDocument.setLastUpdatedTimestamp(new DateTime());
            }
        }
    }

    private void copyApplicationAdditionalInformation(Application to, Application from) {
        if (customizationService.isWorkflowConfigurationEnabled(to, APPLICATION_CRIMINAL_CONVICTION) && from.getAdditionalInformation() != null) {
            ApplicationAdditionalInformation additionalInformation = new ApplicationAdditionalInformation();
            to.setAdditionalInformation(additionalInformation);
            additionalInformation.setApplication(to);
            additionalInformation.setConvictionsText(from.getAdditionalInformation().getConvictionsText());
            additionalInformation.setLastUpdatedTimestamp(new DateTime());
        }
    }

    public void copyReferee(ApplicationReferee to, ApplicationReferee from) {
        Institution toInstitution = to.getApplication().getInstitution();
        to.setUser(from.getUser());
        to.setRefereeType(from.getRefereeType());
        to.setJobEmployer(from.getJobEmployer());
        to.setJobTitle(from.getJobTitle());
        to.setPhone(from.getPhone());
        to.setSkype(from.getSkype());
        to.setAddress(copyAddress(toInstitution, from.getAddress(), to));
        to.setLastUpdatedTimestamp(new DateTime());
    }

    public void copyFunding(ApplicationFunding to, ApplicationFunding from, boolean documentEnabled) {
        Institution toInstitution = to.getApplication().getInstitution();
        to.setSponsor(from.getSponsor());
        to.setFundingSource(getEnabledImportedObject(toInstitution, from.getFundingSource(), to));
        to.setDescription(from.getDescription());
        to.setValue(from.getValue());
        to.setAwardDate(from.getAwardDate());
        to.setTerms(from.getTerms());

        if (documentEnabled) {
            to.setDocument(copyDocument(from.getDocument()));
        }

        to.setLastUpdatedTimestamp(new DateTime());
    }

    public void copyPrize(ApplicationPrize to, ApplicationPrize from) {
        to.setProvider(from.getProvider());
        to.setTitle(from.getTitle());
        to.setDescription(from.getDescription());
        to.setAwardDate(from.getAwardDate());
        to.setLastUpdatedTimestamp(new DateTime());
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
        to.setLastUpdatedTimestamp(new DateTime());
    }

    public void copyQualification(ApplicationQualification to, ApplicationQualification from, WorkflowPropertyConfiguration configuration) {
        Institution toInstitution = to.getApplication().getInstitution();
        to.setProgram(getEnabledImportedObject(toInstitution, from.getProgram(), to));
        to.setLanguage(from.getLanguage());
        to.setStartDate(from.getStartDate());
        to.setCompleted(from.getCompleted());
        to.setGrade(from.getGrade());
        to.setAwardDate(from.getAwardDate());

        if (BooleanUtils.isTrue(configuration.getEnabled())) {
            Document document = from.getDocument();
            to.setDocument(copyDocument(document));

            if (BooleanUtils.isTrue(configuration.getRequired()) && document == null) {
                sectionsWithErrors.add(to);
            }
        }

        to.setLastUpdatedTimestamp(new DateTime());
    }

    private AddressApplication copyAddress(Institution toInstitution, AddressApplication fromAddress, ApplicationSection toSection) {
        if (fromAddress == null) {
            return null;
        }
        AddressApplication toAddress = new AddressApplication();
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
        to.setContent(documentService.getDocumentContent(from));
        to.setUser(from.getUser());
        to.setCreatedTimestamp(new DateTime());
        to.setExported(false);
        return to;
    }

    private ApplicationLanguageQualification copyLanguageQualification(
            Institution toInstitution, ApplicationLanguageQualification from, Application toApplication) {
        if (from == null) {
            return null;
        }
        ApplicationLanguageQualification to = new ApplicationLanguageQualification();
        to.setLanguageQualificationType(getEnabledImportedObject(toInstitution, from.getLanguageQualificationType(), to));
        to.setExamDate(from.getExamDate());
        to.setOverallScore(from.getOverallScore());
        to.setReadingScore(from.getReadingScore());
        to.setWritingScore(from.getWritingScore());
        to.setSpeakingScore(from.getSpeakingScore());
        to.setListeningScore(from.getListeningScore());
        to.setPersonalDetail(toApplication.getPersonalDetail());

        WorkflowPropertyConfiguration languageDocumentConfiguration = (WorkflowPropertyConfiguration) customizationService.getConfigurationWithVersion(
                WORKFLOW_PROPERTY, APPLICATION_LANGUAGE_PROOF_OF_AWARD, toApplication.getWorkflowPropertyConfigurationVersion());

        if (BooleanUtils.isTrue(languageDocumentConfiguration.getEnabled())) {
            Document document = from.getDocument();
            to.setDocument(copyDocument(document));

            if (BooleanUtils.isTrue(languageDocumentConfiguration.getRequired()) && document == null) {
                sectionsWithErrors.add(to.getPersonalDetail());
            }
        }

        to.setLastUpdatedTimestamp(new DateTime());
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
        to.setLastUpdatedTimestamp(new DateTime());
        return to;
    }

    private <T extends ImportedEntity<?, ?>> T getEnabledImportedObject(Institution toInstitution, T fromEntity, ApplicationSection toSection) {
        T toEntity = null;
        if (fromEntity == null) {
            toEntity = null;
        } else if (fromEntity.getEnabled()) {
            toEntity = fromEntity;
        }

        if (fromEntity != null && toEntity == null) {
            sectionsWithErrors.add(toSection);
        }

        return toEntity;
    }

}
