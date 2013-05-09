package com.zuehlke.pgadmissions.validators;

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
import org.springframework.validation.Errors;

public abstract class AbstractValidator implements org.springframework.validation.Validator, ApplicationContextAware, ConstraintValidatorFactory {

    protected static final String EMPTY_FIELD_ERROR_MESSAGE = "text.field.empty";
    
    protected static final String EMPTY_DROPDOWN_ERROR_MESSAGE = "dropdown.radio.select.none";
    
    protected static final String NOT_BEFORE_ERROR_MESSAGE = "date.field.notbefore";

    protected static final String NOT_AFTER_ERROR_MESSAGE = "date.field.notafter";
    
    protected static final String MANDATORY_CHECKBOX = "checkbox.mandatory";
    
    protected static final String MUST_SELECT_DATE_AND_TIME = "datepicker.field.mustselectdate";
    
    protected static final String MUST_SELECT_DATE_AND_TIMES_IN_THE_FUTURE = "datepicker.field.mustselectdatetimesinfuture";
    
    protected static final String INVALID_TIME = "time.field.invalid"; 
    
    protected static final Pattern timePattern = Pattern.compile("([01]?[0-9]|2[0-3]):[0-5][0-9]");
    
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
}