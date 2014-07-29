package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.ApplicationPersonalDetails;
import com.zuehlke.pgadmissions.domain.ApplicationProgramDetails;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.services.ProgramService;

@Component
public class ApplicationFormValidator extends AbstractValidator {

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramDetailsValidator programDetailsValidator;

    @Autowired
    private PersonalDetailsValidator personalDetailsValidator;

    @Autowired
    private ApplicationAddressValidator applicationFormAddressValidator;

    @Autowired
    private ApplicationFormDocumentValidator applicationFormDocumentValidator;

    @Override
    public boolean supports(Class<?> clazz) {
        return Application.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        Application applicationForm = (Application) target;
        ApplicationProgramDetails programDetails = applicationForm.getProgramDetails();
        ApplicationPersonalDetails personalDetails = applicationForm.getPersonalDetails();
        ApplicationAddress applicationFormAddress = applicationForm.getAddress();
        ApplicationAdditionalInformation additionalInformation = applicationForm.getAdditionalInformation();
        ApplicationDocument applicationFormDocument = applicationForm.getDocument();

        if (!programDetailsValidator.isValid(programDetails)) {
            errors.rejectValue("programDetails", "user.programDetails.incomplete");
        }

        if (!personalDetailsValidator.isValid(personalDetails)) {
            errors.rejectValue("personalDetails", "user.personalDetails.incomplete");
        }

        if (!applicationFormAddressValidator.isValid(applicationFormAddress)) {
            errors.rejectValue("applicationAddress", "user.addresses.notempty");
        }

        if (!applicationFormDocumentValidator.isValid(applicationFormDocument)) {
            errors.rejectValue("applicationDocument", "documents.section.invalid");
        }

        if (applicationForm.getReferees().size() < 3) {
            errors.rejectValue("referees", "user.referees.notvalid");
        }

        if (BooleanUtils.isNotTrue(applicationForm.getAcceptedTerms())) {
            errors.rejectValue("acceptedTerms", EMPTY_FIELD_ERROR_MESSAGE);
        }

        if (programDetails != null && programDetails.getStudyOption() != null) {
            List<ProgramInstance> programInstances = programService.getActiveProgramInstancesForStudyOption(applicationForm.getProgram(),
                    programDetails.getStudyOption());
            if (programInstances == null || programInstances.isEmpty()) {
                errors.rejectValue("programDetails.studyOption", "programDetails.studyOption.invalid");
            }
        }
    }

}