package com.zuehlke.pgadmissions.rest.validation.validator;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationProgramDetail;
import com.zuehlke.pgadmissions.services.ApplicationService;

@Component
public class CompleteApplicationValidator extends LocalValidatorFactoryBean implements Validator {

    @Autowired
    private ApplicationService applicationService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Application.class.isAssignableFrom(clazz);

    }

    @Override
    public void validate(Object target, Errors errors) {
        validate(target, errors, new Object[0]);
    }

    @Override
    public void validate(Object target, Errors errors, Object... validationHints) {
        super.validate(target, errors, validationHints);
        Application application = (Application) target;

        ValidationUtils.rejectIfEmpty(errors, "programDetail", "notNull");
        ValidationUtils.rejectIfEmpty(errors, "personalDetail", "notNull");
        ValidationUtils.rejectIfEmpty(errors, "address", "notNull");
        // FIXME uncomment when documents are implemented
//        ValidationUtils.rejectIfEmpty(errors, "document", "notNull");
        ValidationUtils.rejectIfEmpty(errors, "additionalInformation", "notNull");


        ApplicationProgramDetail programDetail = application.getProgramDetail();
        if (programDetail != null) {
            errors.pushNestedPath("programDetail");
            LocalDate startDate = programDetail.getStartDate();
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
            errors.rejectValue("referees", "size.exact", new Object[]{3}, null);
        }

    }
}
