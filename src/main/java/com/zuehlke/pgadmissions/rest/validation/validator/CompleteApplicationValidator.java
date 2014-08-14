package com.zuehlke.pgadmissions.rest.validation.validator;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationProgramDetails;
import com.zuehlke.pgadmissions.services.ApplicationService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class CompleteApplicationValidator extends AbstractValidator {

    @Autowired
    private ApplicationService applicationService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Application.class.isAssignableFrom(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        Application application = (Application) target;

        ValidationUtils.rejectIfEmpty(errors, "programDetails", "notNull");
        ValidationUtils.rejectIfEmpty(errors, "personalDetails", "notNull");
        ValidationUtils.rejectIfEmpty(errors, "address", "notNull");
        ValidationUtils.rejectIfEmpty(errors, "document", "notNull");
        ValidationUtils.rejectIfEmpty(errors, "additionalInformation", "notNull");


        ApplicationProgramDetails programDetails = application.getProgramDetails();
        if (programDetails != null) {
            errors.pushNestedPath("programDetails");
            LocalDate startDate = programDetails.getStartDate();
            LocalDate earliestStartDate = applicationService.getEarliestStartDate(application);
            LocalDate latestStartDate = applicationService.getLatestStartDate(application);

            if (startDate.isBefore(earliestStartDate)) {
                errors.rejectValue("startDate", "notBefore", new Object[]{earliestStartDate}, null);
            } else if (startDate.isAfter(latestStartDate)) {
                errors.rejectValue("startDate", "notAfter", new Object[]{latestStartDate}, null);
            }

            errors.popNestedPath();
        }
        if (application.getReferees().size() != 3) {
            errors.rejectValue("referees", "size.exact", new Object[3], null);
        }

    }
}
