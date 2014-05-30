package com.zuehlke.pgadmissions.validators;

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

        // recommended offer validation
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recommendedStartDate", EMPTY_FIELD_ERROR_MESSAGE);
        LocalDate startDate = approvalComment.getPositionProvisionalStartDate();
        LocalDate today = new LocalDate();
        if (startDate != null && !startDate.isAfter(today)) {
            errors.rejectValue("recommendedStartDate", "date.field.notfuture");
        }

    }

    void setSupervisorsValidator(CommentAssignedUserValidator supervisorsValidator) {
        this.supervisorsValidator = supervisorsValidator;
    }

}