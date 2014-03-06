package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;

@Component
public class ApplicationFormValidator extends AbstractValidator {

    private final ProgramInstanceDAO programInstanceDAO;

    private final ProgrammeDetailsValidator programmeDetailsValidator;

    private final PersonalDetailsValidator personalDetailsValidator;

    private final AddressValidator addressValidator;

    private final AdditionalInformationValidator additionalInformationValidator;

    ApplicationFormValidator() {
        this(null, null, null, null, null);
    }

    @Autowired
    public ApplicationFormValidator(ProgramInstanceDAO programInstanceDAO, ProgrammeDetailsValidator programmeDetailsValidator,
            PersonalDetailsValidator personalDetailsValidator, AddressValidator addressValidator, AdditionalInformationValidator additionalInformationValidator) {
        this.programInstanceDAO = programInstanceDAO;
        this.programmeDetailsValidator = programmeDetailsValidator;
        this.personalDetailsValidator = personalDetailsValidator;
        this.addressValidator = addressValidator;
        this.additionalInformationValidator = additionalInformationValidator;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ApplicationForm.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        ApplicationForm applicationForm = (ApplicationForm) target;
        ProgrammeDetails programmeDetails = applicationForm.getProgrammeDetails();
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        Address currentAddress = applicationForm.getCurrentAddress();
        Address contactAddress = applicationForm.getContactAddress();
        AdditionalInformation additionalInformation = applicationForm.getAdditionalInformation();

        if (!programmeDetailsValidator.isValid(programmeDetails)) {
            errors.rejectValue("programmeDetails", "user.programmeDetails.incomplete");
        }

        if (!personalDetailsValidator.isValid(personalDetails)) {
            errors.rejectValue("personalDetails", "user.personalDetails.incomplete");
        }

        if (!addressValidator.isValid(currentAddress)) {
            errors.rejectValue("currentAddress", "user.addresses.notempty");
        }

        if (!addressValidator.isValid(contactAddress)) {
            errors.rejectValue("contactAddress", "user.addresses.notempty");
        }

        if (!additionalInformationValidator.isValid(additionalInformation)) {
            errors.rejectValue("additionalInformation", "user.additionalInformation.incomplete");
        }

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