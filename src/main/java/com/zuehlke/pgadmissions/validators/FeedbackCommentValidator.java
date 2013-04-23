package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
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

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(ReviewComment.class) || clazz.isAssignableFrom(InterviewComment.class) || clazz.isAssignableFrom(ReferenceComment.class);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        if (target instanceof ReviewComment) {
            ReviewComment comment = (ReviewComment) target;
            if (!comment.isDecline()) {
                if (comment.getSuitableCandidateForUcl() == null) {
                    errors.rejectValue("suitableCandidateForUcl", EMPTY_DROPDOWN_ERROR_MESSAGE);
                }
                if (comment.getSuitableCandidateForProgramme() == null) {
                    errors.rejectValue("suitableCandidateForProgramme", EMPTY_DROPDOWN_ERROR_MESSAGE);
                }
                if (comment.getWillingToInterview() == null) {
                    errors.rejectValue("willingToInterview", EMPTY_DROPDOWN_ERROR_MESSAGE);
                }
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", EMPTY_FIELD_ERROR_MESSAGE);
            }
        } else if (target instanceof InterviewComment) {
            InterviewComment comment = (InterviewComment) target;
            if (!comment.isDecline()) {
                if (comment.getSuitableCandidateForUcl() == null) {
                    errors.rejectValue("suitableCandidateForUcl", EMPTY_DROPDOWN_ERROR_MESSAGE);
                }
                if (comment.getSuitableCandidateForProgramme() == null) {
                    errors.rejectValue("suitableCandidateForProgramme", EMPTY_DROPDOWN_ERROR_MESSAGE);
                }
                if (comment.getWillingToSupervise() == null) {
                    errors.rejectValue("willingToSupervise", EMPTY_DROPDOWN_ERROR_MESSAGE);
                }
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", EMPTY_FIELD_ERROR_MESSAGE);
            }
        } else if (target instanceof ReferenceComment) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "suitableForProgramme", EMPTY_DROPDOWN_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "suitableForUCL", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }

        Comment comment = (Comment) target;
        List<Score> scores = comment.getScores();
        if (scores != null) {
            validateScores(errors, scores);
        }

    }

    private void validateScores(Errors errors, List<Score> scores) {
        for (int i = 0; i < scores.size(); i++) {
            Score score = scores.get(i);
            if (BooleanUtils.isNotTrue(score.getRequired())) {
                continue;
            }
            switch (score.getQuestionType()) {
            case TEXT:
            case TEXTAREA:
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "scores[" + i + "].textResponse", EMPTY_FIELD_ERROR_MESSAGE);
                break;
            case DATE:
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "scores[" + i + "].dateResponse", EMPTY_FIELD_ERROR_MESSAGE);
                break;
            default:
                break;
            }
        }
    }

}
