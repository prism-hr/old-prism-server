package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.domain.Badge;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Component
public class BadgeValidator extends AbstractValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Badge.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        Badge badge = (Badge) target;

        if (badge.getProgram() == null) {
            errors.rejectValue("program", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }
        
        if (StringUtils.isNotBlank(badge.getProgrammeHomepage()) && !GenericValidator.isUrl(badge.getProgrammeHomepage())) {
            errors.rejectValue("programmeHomepage", "interview.locationURL.invalid");
        }
        
        if (badge.getClosingDate() != null) {
            if (!DateUtils.isToday(badge.getClosingDate()) && badge.getClosingDate().before(new Date())) {
                errors.rejectValue("closingDate", "date.field.notfuture");
            }
        }
    }
}
