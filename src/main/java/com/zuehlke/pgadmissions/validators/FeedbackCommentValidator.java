package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Score;

@Component
public class FeedbackCommentValidator extends AbstractValidator {

    @Autowired
    private ScoresValidator scoresValidator;

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

        List<Score> scores = comment.getScores();

        if (BooleanUtils.isTrue(comment.getUseCustomRecruiterQuestions())
            ) {
            for (int i = 0; i < scores.size(); i++) {
                try {
                    errors.pushNestedPath("scores[" + i + "]");
                    ValidationUtils.invokeValidator(scoresValidator, scores.get(i), errors);
                } finally {
                    errors.popNestedPath();
                }
            }
        }
    }

}
