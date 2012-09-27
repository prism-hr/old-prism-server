package com.zuehlke.pgadmissions.validators;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
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
        }
        
        if (StringUtils.isNotBlank(badge.getProgrammeHomepage()) && !GenericValidator.isUrl(badge.getProgrammeHomepage())) {
            errors.rejectValue("programmeHomepage", "interview.locationURL.invalid");
        }
        
        if (badge.getClosingDate() != null) {
            if (!isToday(badge.getClosingDate()) && badge.getClosingDate().before(new Date())) {
                errors.rejectValue("closingDate", "date.field.notfuture");
            }
        }
    }
    
    public static boolean isToday(Date date) {
        return isSameDay(date, Calendar.getInstance().getTime());
    }
    
    private static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }
    
    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
}
