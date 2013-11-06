package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.OfferRecommendedComment;

@Component
public class OfferRecommendedCommentValidator extends AbstractValidator {

    @Autowired
    private SupervisorsValidator supervisorsValidator;
    
    @Override
    public boolean supports(Class<?> clazz) {
        return OfferRecommendedComment.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        OfferRecommendedComment comment = (OfferRecommendedComment) target;
        
        ValidationUtils.invokeValidator(supervisorsValidator, comment, errors);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectTitle", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectAbstract", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedStartDate", EMPTY_FIELD_ERROR_MESSAGE);
        
        Date startDate = comment.getRecommendedStartDate();
        Date today = new Date();
        if (startDate != null && !startDate.after(today)) {
            errors.rejectValue("recommendedStartDate", "date.field.notfuture");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedConditionsAvailable", EMPTY_DROPDOWN_ERROR_MESSAGE);

        if (BooleanUtils.isTrue(comment.getRecommendedConditionsAvailable())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedConditions", EMPTY_FIELD_ERROR_MESSAGE);
        }
    }

    public void setSupervisorsValidator(SupervisorsValidator supervisorsValidator) {
        this.supervisorsValidator = supervisorsValidator;
    }
    
}
