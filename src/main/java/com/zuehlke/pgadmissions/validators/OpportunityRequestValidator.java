package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.services.FullTextSearchService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;

@Component
public class OpportunityRequestValidator extends AbstractValidator {

    @Autowired
    private RegisterFormValidator registerFormValidator;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private FullTextSearchService fullTextSearchService;

    private boolean validatingProgram;

    @Override
    public boolean supports(Class<?> clazz) {
        return OpportunityRequest.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        OpportunityRequest opportunityRequest = (OpportunityRequest) target;
        Program sourceProgram = opportunityRequest.getSourceProgram();

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "institutionCountry", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "institutionCode", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programDescription", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programType", EMPTY_DROPDOWN_ERROR_MESSAGE);

        // validate institution code / name
        String institutionCode = opportunityRequest.getInstitutionCode();
        if (StringUtils.equalsIgnoreCase("OTHER", institutionCode)) {
            if (StringUtils.isBlank(opportunityRequest.getOtherInstitution())) {
                errors.rejectValue("otherInstitution", EMPTY_FIELD_ERROR_MESSAGE);
            } else if (opportunityRequest.getInstitutionCountry() != null && !opportunityRequest.isForceCreatingNewInstitution()) {
                // check for similar institution names
                List<String> matchingInstitutions = fullTextSearchService.getMatchingInstitutions(opportunityRequest.getOtherInstitution(), opportunityRequest
                        .getInstitutionCountry().getCode());
                matchingInstitutions.remove(opportunityRequest.getOtherInstitution()); // remove the identical one
                if (!matchingInstitutions.isEmpty()) {
                    errors.rejectValue("otherInstitution", "institution.did.you.mean", Joiner.on("::").join(matchingInstitutions));
                }
            }
        }

        if (opportunityRequest.getInstitutionCountry() != null && "XK".equals(opportunityRequest.getInstitutionCountry().getCode())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "atasRequired", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }

        // validate study duration
        if (!errors.hasFieldErrors("studyDurationNumber")) {
            // no number format error
            if (opportunityRequest.getStudyDurationNumber() == null) {
                errors.rejectValue("studyDurationNumber", EMPTY_FIELD_ERROR_MESSAGE);
            } else if (opportunityRequest.getStudyDurationNumber() < 1) {
                errors.rejectValue("studyDurationNumber", "Min", new Object[] { null, "0" }, null);
            } else if (opportunityRequest.getStudyDurationUnit() == null) {
                errors.rejectValue("studyDurationUnit", EMPTY_DROPDOWN_ERROR_MESSAGE);
            } else if (!Lists.newArrayList("YEARS", "MONTHS").contains(opportunityRequest.getStudyDurationUnit())) {
                errors.rejectValue("studyDurationUnit", EMPTY_DROPDOWN_ERROR_MESSAGE);
            }
        }

        // trying to create new request
        if (sourceProgram == null) {

            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programTitle", EMPTY_FIELD_ERROR_MESSAGE);

            // validate an author
            if (opportunityRequest.getAuthor() != null) {
                errors.pushNestedPath("author");
                ValidationUtils.invokeValidator(registerFormValidator, opportunityRequest.getAuthor(), errors);
                errors.popNestedPath();
            }
        }

        // new request or editing custom program
        if (sourceProgram == null || !sourceProgram.isImported()) {

            if (Strings.isNullOrEmpty(opportunityRequest.getStudyOptions())) {
                errors.rejectValue("studyOptions", EMPTY_DROPDOWN_ERROR_MESSAGE);
            }

            // validate advertising deadline
            if (opportunityRequest.getAdvertisingDeadlineYear() == null) {
                errors.rejectValue("advertisingDeadlineYear", EMPTY_DROPDOWN_ERROR_MESSAGE);
            } else {
                int startYear = programInstanceService.getFirstProgramInstanceStartYear(new LocalDate());
                if (opportunityRequest.getAdvertisingDeadlineYear() <= startYear) {
                    errors.rejectValue("advertisingDeadlineYear", "Min", new Object[] { null, startYear }, null);
                } else if (opportunityRequest.getAdvertisingDeadlineYear() > startYear + 10) {
                    errors.rejectValue("advertisingDeadlineYear", "Max", new Object[] { null, startYear + 10 }, null);
                }
            }
        }

        if (validatingProgram) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "acceptingApplications", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }

    }

    void setRegisterFormValidator(RegisterFormValidator registerFormValidator) {
        this.registerFormValidator = registerFormValidator;
    }

    void setProgramInstanceService(ProgramInstanceService programInstanceService) {
        this.programInstanceService = programInstanceService;
    }

    void setFullTextSearchService(FullTextSearchService fullTextSearchService) {
        this.fullTextSearchService = fullTextSearchService;
    }

    public void setValidatingProgram(boolean validatingProgram) {
        this.validatingProgram = validatingProgram;
    }

}
