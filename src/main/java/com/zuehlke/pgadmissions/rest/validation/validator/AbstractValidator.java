package com.zuehlke.pgadmissions.rest.validation.validator;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

public abstract class AbstractValidator implements org.springframework.validation.Validator, ApplicationContextAware, ConstraintValidatorFactory {

    public static final Integer ERROR_VALUE_FOR_DURATION_OF_STUDY = -1;
    public static final Integer ERROR_UNIT_FOR_DURATION_OF_STUDY = -2;
    
    public static final String EMPTY_FIELD_ERROR_MESSAGE = "text.field.empty";
    
    public static final String MAXIMUM_500_CHARACTERS = "maximum.500.characters";

    public static final String EMPTY_DROPDOWN_ERROR_MESSAGE = "dropdown.radio.select.none";

    public static final String NOT_BEFORE_ERROR_MESSAGE = "date.field.notbefore";

    public static final String NOT_AFTER_ERROR_MESSAGE = "date.field.notafter";

    public static final String MANDATORY_CHECKBOX = "checkbox.mandatory";

    public static final String MUST_SELECT_DATE_AND_TIME = "datepicker.field.mustselectdate";

    public static final String MUST_SELECT_DATE_AND_TIMES_IN_THE_FUTURE = "datepicker.field.mustselectdatetimesinfuture";

    public static final String INVALID_TIME = "time.field.invalid";

    public static final Pattern TIME_PATTERN = Pattern.compile("([01]?[0-9]|2[0-3]):[0-5][0-9]");
    
    public static final String PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER = "prospectus.durationOfStudy.emptyOrNotInteger";

    @Autowired
    private Validator validator;

    private ApplicationContext applicationContext;

    public AbstractValidator() {
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(final Class<T> key) {
        Map<String, T> beansByNames = applicationContext.getBeansOfType(key);

        if (beansByNames.isEmpty()) {
            try {
                return key.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException("Could not instantiate constraint validator class '" + key.getName() + "'", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Could not instantiate constraint validator class '" + key.getName() + "'", e);
            }
        }

        if (beansByNames.size() > 1) {
            throw new RuntimeException("Only one bean of type '" + key.getName() + "' is allowed in the application context");
        }
        return (T) beansByNames.values().iterator().next();
    }

    @Override
    public boolean supports(Class<?> c) {
        return true;
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(target);
        for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
            String propertyPath = constraintViolation.getPropertyPath().toString();
            String message = constraintViolation.getMessage();
            errors.rejectValue(propertyPath, "", message);
        }
        addExtraValidation(target, errors);
    }

    protected abstract void addExtraValidation(final Object target, final Errors errors);

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }
    
    protected void validateStudyDuration(Errors errors, Integer studyDuration) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "studyDuration", PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER);
        if (studyDuration != null && studyDuration.equals(ERROR_VALUE_FOR_DURATION_OF_STUDY)) {
            errors.rejectValue("studyDuration", PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER);
        }
        if (studyDuration != null && studyDuration.equals(ERROR_UNIT_FOR_DURATION_OF_STUDY)) {
            errors.rejectValue("studyDuration", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }
    }
    
    public boolean isValid(Object target) {
        if(target == null){
            return false;
        }
        Errors errors = new BeanPropertyBindingResult(target, "target");
        validate(target, errors);
        return !errors.hasErrors();
    }
    
}