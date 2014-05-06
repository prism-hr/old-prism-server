package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
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
    private ApplicationFormAddressValidator applicationFormAddressValidator;

    @Autowired
    private AdditionalInformationValidator additionalInformationValidator;

    @Autowired
    private ApplicationFormDocumentValidator applicationFormDocumentValidator;

    @Override
    public boolean supports(Class<?> clazz) {
        return ApplicationForm.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        ApplicationForm applicationForm = (ApplicationForm) target;
        ProgramDetails programDetails = applicationForm.getProgramDetails();
        PersonalDetails personalDetails = applicationForm.getPersonalDetails();
        ApplicationAddress applicationFormAddress = applicationForm.getApplicationAddress();
        AdditionalInformation additionalInformation = applicationForm.getAdditionalInformation();
        ApplicationDocument applicationFormDocument = applicationForm.getApplicationDocument();

        if (!programDetailsValidator.isValid(programDetails)) {
            errors.rejectValue("programDetails", "user.programDetails.incomplete");
        }

        if (!personalDetailsValidator.isValid(personalDetails)) {
            errors.rejectValue("personalDetails", "user.personalDetails.incomplete");
        }

        if (!applicationFormAddressValidator.isValid(applicationFormAddress)) {
            errors.rejectValue("applicationFormAddress", "user.addresses.notempty");
        }

        if (!additionalInformationValidator.isValid(additionalInformation)) {
            errors.rejectValue("additionalInformation", "user.additionalInformation.incomplete");
        }

        if (!applicationFormDocumentValidator.isValid(applicationFormDocument)) {
            errors.rejectValue("applicationDocument", "documents.section.invalid");
        }

        if (applicationForm.getReferees().size() < 3) {
            errors.rejectValue("referees", "user.referees.notvalid");
        }

        if (BooleanUtils.isNotTrue(applicationForm.getAcceptedTermsOnSubmission())) {
            errors.rejectValue("acceptedTermsOnSubmission", EMPTY_FIELD_ERROR_MESSAGE);
        }

        if (!applicationForm.getProgram().isEnabled()) {
            errors.rejectValue("program", "application.program.invalid");
        }

        if (applicationForm.getProject() != null && !applicationForm.getProject().isEnabled()) {
            applicationForm.setProject(null);
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