package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.PassportInformation;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Component
public class PassportInformationValidator extends AbstractValidator {

    @Override
    public void validate(final Object target, final Errors errors) {
        PassportInformation passportInformation = (PassportInformation) target;
        
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passportNumber", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nameOnPassport", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passportIssueDate", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passportExpiryDate", EMPTY_FIELD_ERROR_MESSAGE);

        if (passportInformation != null) {
            Date passportExpiryDate = passportInformation.getPassportExpiryDate();
            Date passportIssueDate = passportInformation.getPassportIssueDate();
            
            if (passportExpiryDate != null) {
                if (!DateUtils.isToday(passportExpiryDate) && passportExpiryDate.before(new Date())) {
                    errors.rejectValue("passportExpiryDate", "date.field.notfuture");
                }
            }
            
            if (passportIssueDate != null) {
                if (!DateUtils.isToday(passportIssueDate) && passportIssueDate.after(new Date())) {
                    errors.rejectValue("passportIssueDate", "date.field.notpast");
                }
            }
            
            if (passportExpiryDate != null && passportIssueDate != null) {
                if (org.apache.commons.lang.time.DateUtils.isSameDay(passportExpiryDate, passportIssueDate)) {
                    errors.rejectValue("passportExpiryDate", "date.field.same");
                    errors.rejectValue("passportIssueDate", "date.field.same");
                }
            }
        }
    }

    @Override
    protected void addExtraValidation(Object target, Errors errors) {
    }
}
