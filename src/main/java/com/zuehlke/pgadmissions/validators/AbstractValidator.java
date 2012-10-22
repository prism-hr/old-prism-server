package com.zuehlke.pgadmissions.validators;

import java.util.Map;
import java.util.Set;

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
}