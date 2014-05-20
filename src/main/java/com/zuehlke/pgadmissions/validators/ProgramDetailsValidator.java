package com.zuehlke.pgadmissions.validators;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.services.ProgramService;

@Component
public class ProgramDetailsValidator extends FormSectionObjectValidator implements Validator {

    @Autowired
    private ProgramService programService;

    @Override
    public boolean supports(Class<?> clazz) {
        return ProgramDetails.class.isAssignableFrom(clazz);
    }

    @Override
    public void addExtraValidation(final Object target, final Errors errors) {
        super.addExtraValidation(target, errors);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programmeName", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "studyOption", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startDate", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sourcesOfInterest", EMPTY_DROPDOWN_ERROR_MESSAGE);

        ProgramDetails programDetail = (ProgramDetails) target;

        if (programDetail.getSourceOfInterest() != null && programDetail.getSourceOfInterest().isFreeText()) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sourcesOfInterestText", EMPTY_FIELD_ERROR_MESSAGE);
        }

        List<ProgramInstance> programInstances = programService.getActiveProgramInstancesForStudyOption(programDetail.getApplication().getProgram(),
                programDetail.getStudyOption());
        if (programInstances.isEmpty()) {
            errors.rejectValue("studyOption", "programmeDetails.studyOption.invalid");
        }

        if (programDetail.getStartDate() != null) {
            DateTime earliestStartDate = new DateTime(programInstances.get(0).getApplicationStartDate());
            DateTime latestStartDate = new DateTime(programInstances.get(programInstances.size() - 1).getApplicationDeadline()).plusYears(1);
            DateTime preferredStartDate = new DateTime(programDetail.getStartDate());

            if (preferredStartDate.isBefore(earliestStartDate) || preferredStartDate.isAfter(latestStartDate)) {
                errors.rejectValue("startDate", "programmeDetails.startDate.invalid", new Object[] { earliestStartDate.toString("dd-MMM-yyyy"),
                        latestStartDate.toString("dd-MMM-yyyy") }, "");
            }
        } else if (programDetail.getStartDate() != null && !programDetail.getStartDate().isAfter(new LocalDate())) {
            errors.rejectValue("startDate", "date.field.notfuture");
        }

        Set<String> supervisorEmails = new HashSet<String>();
        for (SuggestedSupervisor supervisor : programDetail.getSuggestedSupervisors()) {
            if (StringUtils.isBlank(supervisor.getUser().getFirstName())) {
                errors.rejectValue("suggestedSupervisors", EMPTY_FIELD_ERROR_MESSAGE);
            }

            if (StringUtils.isBlank(supervisor.getUser().getLastName())) {
                errors.rejectValue("suggestedSupervisors", EMPTY_FIELD_ERROR_MESSAGE);
            }

            if (StringUtils.isBlank(supervisor.getUser().getEmail())) {
                errors.rejectValue("suggestedSupervisors", EMPTY_FIELD_ERROR_MESSAGE);
            }

            if (StringUtils.isNotBlank(supervisor.getUser().getEmail())) {
                if (supervisorEmails.contains(supervisor.getUser().getEmail())) {
                    errors.rejectValue("suggestedSupervisors", "suggestedSupervisors.duplicate.email");
                } else {
                    supervisorEmails.add(supervisor.getUser().getEmail());
                }
            }
        }
    }
}
