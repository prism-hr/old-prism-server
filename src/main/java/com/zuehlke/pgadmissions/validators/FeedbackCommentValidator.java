package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;

@Component
public class FeedbackCommentValidator extends AbstractValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(ReviewComment.class) || clazz.isAssignableFrom(InterviewComment.class) || clazz.isAssignableFrom(ReferenceComment.class)
                || clazz.isAssignableFrom(Comment.class);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        Comment comment = (Comment) target;
        if (comment instanceof ReviewComment || comment instanceof InterviewComment) {
            if (!comment.getDeclinedResponse()) {
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "suitableForProgramme", EMPTY_DROPDOWN_ERROR_MESSAGE);
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "suitableForInstitution", EMPTY_DROPDOWN_ERROR_MESSAGE);
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "willingToInterview", EMPTY_DROPDOWN_ERROR_MESSAGE);
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "willingToSupervise", EMPTY_DROPDOWN_ERROR_MESSAGE);

                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", EMPTY_FIELD_ERROR_MESSAGE);
            }
        } else if (comment instanceof ReferenceComment) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "content", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "suitableForProgramme", EMPTY_DROPDOWN_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "suitableForInstitution", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicantRating", EMPTY_FIELD_ERROR_MESSAGE);

    }

}
