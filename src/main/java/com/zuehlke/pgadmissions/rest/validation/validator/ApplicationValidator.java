package com.zuehlke.pgadmissions.rest.validation.validator;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.application.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.application.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationProgramDetail;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.Disability;
import com.zuehlke.pgadmissions.domain.imported.Ethnicity;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.program.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowPropertyConfiguration;
import com.zuehlke.pgadmissions.exceptions.CannotApplyException;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CustomizationService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

@Component
public class ApplicationValidator extends LocalValidatorFactoryBean implements Validator {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private ProgramService programService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Application.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        validate(target, errors, new Object[0]);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void validate(Object target, Errors errors, Object... validationHints) {
        super.validate(target, errors, validationHints);
        Application application = (Application) target;

        ValidationUtils.rejectIfEmpty(errors, "programDetail", "notNull");
        ValidationUtils.rejectIfEmpty(errors, "personalDetail", "notNull");
        ValidationUtils.rejectIfEmpty(errors, "address", "notNull");

        validateStartDate(application, application.getProgramDetail(), errors);

        List<WorkflowPropertyConfiguration> configurations = (List<WorkflowPropertyConfiguration>) (List<?>) customizationService.getConfigurationsWithVersion(
                PrismConfiguration.WORKFLOW_PROPERTY, application.getWorkflowPropertyConfigurationVersion());

        for (WorkflowPropertyConfiguration configuration : configurations) {
            switch (configuration.getWorkflowPropertyDefinition().getId()) {
            case APPLICATION_ASSIGN_REFEREE:
                validateRangeConstraint(application, "referees", configuration, errors);
                break;
            case APPLICATION_ASSIGN_SUGGESTED_SUPERVISOR:
                validateRangeConstraint(application, "supervisors", configuration, errors);
                break;
            case APPLICATION_DEMOGRAPHIC:
                validateDemographicConstraint(application, configuration, errors);
                break;
            case APPLICATION_CRIMINAL_CONVICTION:
                validateCriminalConvictionConstraint(application, configuration, errors);
                break;
            case APPLICATION_DOCUMENT_COVERING_LETTER:
                validateDocumentConstraint(application, "coveringLetter", configuration, errors);
                break;
            case APPLICATION_DOCUMENT_CV:
                validateDocumentConstraint(application, "cv", configuration, errors);
                break;
            case APPLICATION_DOCUMENT_PERSONAL_STATEMENT:
                validateDocumentConstraint(application, "personalStatement", configuration, errors);
                break;
            case APPLICATION_DOCUMENT_RESEARCH_STATEMENT:
                validateDocumentConstraint(application, "researchStatement", configuration, errors);
                break;
            case APPLICATION_EMPLOYMENT_POSITION:
                validateRangeConstraint(application, "employmentPositions", configuration, errors);
                break;
            case APPLICATION_FUNDING:
                validateRangeConstraint(application, "fundings", configuration, errors);
                break;
            case APPLICATION_FUNDING_PROOF_OF_AWARD:
                validateDocumentConstraint(application, "fundings", "document", configuration, errors);
                break;
            case APPLICATION_LANGUAGE:
                validateLanguageConstraint(application, configuration, errors);
                break;
            case APPLICATION_LANGUAGE_PROOF_OF_AWARD:
                validateLanguageProofOfAwardConstraint(application, configuration, errors);
                break;
            case APPLICATION_PRIZE:
                validateRangeConstraint(application, "prizes", configuration, errors);
                break;
            case APPLICATION_QUALIFICATION:
                validateRangeConstraint(application, "qualifications", configuration, errors);
                break;
            case APPLICATION_QUALIFICATION_PROOF_OF_AWARD:
                validateDocumentConstraint(application, "qualifications", "document", configuration, errors);
                break;
            case APPLICATION_RESIDENCE:
                validateResidenceConstraint(application, configuration, errors);
                break;
            case APPLICATION_STUDY_DETAIL:
                validateStudyDetailConstraint(errors, application, configuration);
                break;
            case APPLICATION_THEME_PRIMARY:
                validateImplodedRangeConstraint(application, "primaryTheme", configuration, errors);
                break;
            case APPLICATION_THEME_SECONDARY:
                validateImplodedRangeConstraint(application, "secondaryTheme", configuration, errors);
                break;
            default:
                continue;
            }
        }

    }
    
    private void validateStartDate(Application application, ApplicationProgramDetail programDetail, Errors errors) {
        errors.pushNestedPath("programDetail");
        LocalDate startDate = programDetail.getStartDate();

        Program program = application.getProgram();
        ProgramStudyOption studyOption = programService.getEnabledProgramStudyOption(program, programDetail.getStudyOption());

        if (studyOption == null) {
            List<ProgramStudyOption> otherStudyOptions = programService.getEnabledProgramStudyOptions(program);
            if (otherStudyOptions.isEmpty()) {
                throw new CannotApplyException();
            }
            errors.rejectValue("studyOption", "notAvailable");
        } else {
            LocalDate earliestStartDate = applicationService.getEarliestStartDate(studyOption.getId(), new LocalDate());
            LocalDate latestStartDate = applicationService.getLatestStartDate(studyOption.getId());

            if (startDate.isBefore(earliestStartDate)) {
                errors.rejectValue("startDate", "notBefore", new Object[] { earliestStartDate }, null);
            } else if (startDate.isAfter(latestStartDate)) {
                errors.rejectValue("startDate", "notAfter", new Object[] { latestStartDate }, null);
            }
        }

        errors.popNestedPath();
    }

    private void validateRangeConstraint(Application application, String property, WorkflowPropertyConfiguration configuration, Errors errors) {
        Collection<?> properties = (Collection<?>) ReflectionUtils.getProperty(application, property);
        validateRangeConstraint(configuration, property, properties.size(), errors);
    }

    private void validateImplodedRangeConstraint(Application application, String property, WorkflowPropertyConfiguration configuration, Errors errors) {
        String properties = (String) ReflectionUtils.getProperty(application, property);
        if (properties == null || properties.isEmpty()) {
            validateRangeConstraint(configuration, property, 0, errors);
        } else {
            validateRangeConstraint(configuration, property, StringUtils.countMatches(properties, "|") + 1, errors);
        }
    }

    private void validateDocumentConstraint(Application application, String property, String propertyDocument, WorkflowPropertyConfiguration configuration,
            Errors errors) throws Error {
        int i = 0;
        Collection<?> instances = (Collection<?>) ReflectionUtils.getProperty(application, property);
        for (Object instance : instances) {
            Document document = (Document) ReflectionUtils.getProperty(instance, propertyDocument);
            validateRequiredConstraint(document, property + "[" + i + "]", propertyDocument, configuration, errors);
            i++;
        }
    }

    private void validateDocumentConstraint(Application application, String propertyDocument, WorkflowPropertyConfiguration configuration, Errors errors)
            throws Error {
        ApplicationDocument documents = application.getDocument();
        Document cv = documents == null ? null : (Document) ReflectionUtils.getProperty(documents, propertyDocument);
        validateRequiredConstraint(cv, "document", propertyDocument, configuration, errors);
    }

    private void validateDemographicConstraint(Application application, WorkflowPropertyConfiguration configuration, Errors errors) {
        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        boolean personalDetailNull = personalDetail == null;

        Ethnicity ethnicity = personalDetailNull ? null : personalDetail.getEthnicity();
        Disability disability = personalDetailNull ? null : personalDetail.getDisability();

        validateRequiredConstraint(ethnicity, "personalDetail", "ethnicity", configuration, errors);
        validateRequiredConstraint(disability, "personalDetail", "disability", configuration, errors);
    }

    private void validateCriminalConvictionConstraint(Application application, WorkflowPropertyConfiguration configuration, Errors errors) {
        ApplicationAdditionalInformation additionalInformation = application.getAdditionalInformation();
        validateRequiredConstraint(additionalInformation, "additionalInformation", "convictionsText", configuration, errors);
    }

    private void validateLanguageProofOfAwardConstraint(Application application, WorkflowPropertyConfiguration configuration, Errors errors) {
        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        ApplicationLanguageQualification languageQualification = personalDetail == null ? null : personalDetail.getLanguageQualification();
        
        if (languageQualification != null) {
            validateRequiredConstraint(languageQualification.getDocument(), "personalDetail.languageQualification", "document", configuration, errors);
        }
    }

    private void validateLanguageConstraint(Application application, WorkflowPropertyConfiguration configuration, Errors errors) {
        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        Boolean firstLanguageLocale = personalDetail == null ? null : personalDetail.getFirstLanguageLocale();

        validateRequiredConstraint(firstLanguageLocale, "personalDetail", "firstLanguageLocale", configuration, errors);
        if (BooleanUtils.isFalse(firstLanguageLocale)) {
            validateRequiredConstraint(personalDetail.getLanguageQualification(), "personalDetail", "languageQualification", configuration, errors);
        }
    }

    private void validateResidenceConstraint(Application application, WorkflowPropertyConfiguration configuration, Errors errors) {
        ApplicationPersonalDetail personalDetail = application.getPersonalDetail();
        Boolean visaRequired = personalDetail == null ? null : personalDetail.getVisaRequired();

        validateRequiredConstraint(visaRequired, "personalDetail", "visaRequired", configuration, errors);
        if (BooleanUtils.isTrue(visaRequired)) {
            validateRequiredConstraint(personalDetail.getPassport(), "personalDetail", "passport", configuration, errors);
        }
    }

    private void validateStudyDetailConstraint(Errors errors, Application application, WorkflowPropertyConfiguration configuration) throws Error {
        if (BooleanUtils.isTrue(configuration.getEnabled())) {
            if (BooleanUtils.isTrue(configuration.getRequired())) {
                ValidationUtils.rejectIfEmpty(errors, "studyDetail", "notNull");
            }
        }
    }

    private void validateRangeConstraint(WorkflowPropertyConfiguration configuration, String property, Integer propertiesSize, Errors errors) {
        if (BooleanUtils.isTrue(configuration.getEnabled())) {
            Integer minimum = configuration.getMinimum();
            Integer maximum = configuration.getMaximum();
            
            if (minimum != null && propertiesSize < minimum) {
                errors.rejectValue(property, "tooFew");
            } else if (maximum != null && propertiesSize > maximum) {
                errors.rejectValue(property, "tooMany");
            }
        } else if (propertiesSize > 0) {
            throw new Error();
        }
    }

    private void validateRequiredConstraint(Object object, String parentProperty, String property, WorkflowPropertyConfiguration configuration, Errors errors) {
        if (BooleanUtils.isTrue(configuration.getEnabled())) {
            if (BooleanUtils.isTrue(configuration.getRequired()) && object == null) {
                errors.pushNestedPath(parentProperty);
                errors.rejectValue(property, "notNull");
                errors.popNestedPath();
            }
        } else if (object != null) {
            throw new Error();
        }
    }

}
