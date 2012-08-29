package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.domain.Badge;

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
            errors.rejectValue("program", "dropdown.radio.select.none");
            return;
        }
        
        if (StringUtils.isBlank(badge.getProjectTitle()) && badge.getClosingDate() == null) {
            errors.rejectValue("projectTitle", "text.field.empty");
            errors.rejectValue("closingDate", "text.field.empty");
        }
    }
}
