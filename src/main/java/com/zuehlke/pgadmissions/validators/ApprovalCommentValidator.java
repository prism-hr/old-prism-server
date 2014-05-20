package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.AssignSupervisorsComment;

@Component
public class ApprovalCommentValidator extends AbstractValidator {
    // TODO fix test (Supervisor changed to CommentAssignedUser)
    
    @Autowired
    private CommentAssignedUserValidator supervisorsValidator;

    @Override
    public boolean supports(Class<?> clazz) {
        return AssignSupervisorsComment.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {

        AssignSupervisorsComment approvalComment = (AssignSupervisorsComment) target;

        // supervisors validation
        ValidationUtils.invokeValidator(supervisorsValidator, approvalComment, errors);

        // project description validation
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectDescriptionAvailable", EMPTY_DROPDOWN_ERROR_MESSAGE);

        if (BooleanUtils.isTrue(approvalComment.getProjectDescriptionAvailable())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectTitle", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectAbstract", EMPTY_FIELD_ERROR_MESSAGE);
        }

        // recommended offer validation
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedStartDate", EMPTY_FIELD_ERROR_MESSAGE);
        LocalDate startDate = approvalComment.getRecommendedStartDate();
        LocalDate today = new LocalDate();
        if (startDate != null && !startDate.isAfter(today)) {
            errors.rejectValue("recommendedStartDate", "date.field.notfuture");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedConditionsAvailable", EMPTY_DROPDOWN_ERROR_MESSAGE);

        if (BooleanUtils.isTrue(approvalComment.getRecommendedConditionsAvailable())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedConditions", EMPTY_FIELD_ERROR_MESSAGE);
        }

    }

    void setSupervisorsValidator(CommentAssignedUserValidator supervisorsValidator) {
        this.supervisorsValidator = supervisorsValidator;
    }

}