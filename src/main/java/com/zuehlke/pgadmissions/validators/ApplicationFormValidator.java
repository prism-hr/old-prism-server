package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;

@Component
public class ApplicationFormValidator extends AbstractValidator {

    private final ProgramInstanceDAO programInstanceDAO;

    private final ProgrammeDetailsValidator programmeDetailsValidator;
    
    ApplicationFormValidator() {
        this(null, null);
    }

    @Autowired
    public ApplicationFormValidator(ProgramInstanceDAO programInstanceDAO, ProgrammeDetailsValidator programmeDetailsValidator) {
        this.programInstanceDAO = programInstanceDAO;
        this.programmeDetailsValidator = programmeDetailsValidator;

    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ApplicationForm.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        ApplicationForm applicationForm = (ApplicationForm) target;
        ProgrammeDetails programmeDetails = applicationForm.getProgrammeDetails();

        if (programmeDetails != null && programmeDetails.getId() == null) {
            errors.rejectValue("programmeDetails", "user.programmeDetails.incomplete");
        } else {
            Errors programDetailsErrors = new BeanPropertyBindingResult(programmeDetails, "programmeDetails");
            programmeDetailsValidator.validate(programmeDetails, programDetailsErrors);
            if (programDetailsErrors.hasErrors()) {
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programmeDetails", "user.programmeDetails.incomplete");
            }
        }

        if (applicationForm.getPersonalDetails() != null && applicationForm.getPersonalDetails().getId() == null) {
            errors.rejectValue("personalDetails", "user.personalDetails.incomplete");
        }

        if (applicationForm.getAdditionalInformation() != null && applicationForm.getAdditionalInformation().getId() == null) {
            errors.rejectValue("additionalInformation", "user.additionalInformation.incomplete");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddress", "user.addresses.notempty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddress", "user.addresses.notempty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "personalStatement", "documents.section.invalid");
        if (applicationForm.getReferees().size() < 3) {
            errors.rejectValue("referees", "user.referees.notvalid");
        }
        if (BooleanUtils.isNotTrue(applicationForm.getAcceptedTermsOnSubmission())) {
            errors.rejectValue("acceptedTermsOnSubmission", EMPTY_FIELD_ERROR_MESSAGE);
        }
        if (programmeDetails != null && programmeDetails.getStudyOption() != null) {
            List<ProgramInstance> programInstances = programInstanceDAO.getProgramInstancesWithStudyOptionAndDeadlineNotInPast(applicationForm.getProgram(),
                    programmeDetails.getStudyOption());
            if (programInstances == null || programInstances.isEmpty()) {
                List<ProgramInstance> allActiveProgramInstances = programInstanceDAO.getActiveProgramInstances(applicationForm.getProgram());
                if (allActiveProgramInstances == null || allActiveProgramInstances.isEmpty()) {
                    errors.rejectValue("program", "application.program.invalid");
                } else {
                    // program is active, but not with selected study option
                    errors.rejectValue("programmeDetails.studyOption", "programmeDetails.studyOption.invalid");
                }

            }
        }
    }
}