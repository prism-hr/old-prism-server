package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Component
public class FeedbackCommentValidator extends AbstractValidator {

    @Autowired
    private ScoresValidator scoresValidator;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(ReviewComment.class) || clazz.isAssignableFrom(InterviewComment.class)
                || clazz.isAssignableFrom(ReferenceComment.class) || clazz.isAssignableFrom(Comment.class);
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
                if (comment.getWillingToWorkWithApplicant() == null) {
                    errors.rejectValue("willingToWorkWithApplicant", EMPTY_DROPDOWN_ERROR_MESSAGE);
                }
                if (BooleanUtils.isNotTrue(comment.getConfirmNextStage())) {
                    errors.rejectValue("confirmNextStage", MANDATORY_CHECKBOX);
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
                if (BooleanUtils.isNotTrue(comment.getConfirmNextStage())) {
                    errors.rejectValue("confirmNextStage", MANDATORY_CHECKBOX);
                }
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", EMPTY_FIELD_ERROR_MESSAGE);
            }
        } else if (target instanceof ReferenceComment) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", EMPTY_FIELD_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "suitableForProgramme", EMPTY_DROPDOWN_ERROR_MESSAGE);
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "suitableForUCL", EMPTY_DROPDOWN_ERROR_MESSAGE);

            if (BooleanUtils.isNotTrue(((Comment) target).getConfirmNextStage())) {
                errors.rejectValue("confirmNextStage", MANDATORY_CHECKBOX);
            }
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "applicantRating", EMPTY_FIELD_ERROR_MESSAGE);
        
        Comment comment = (Comment) target;
        CommentType commentType = comment.getType();
        List<Score> scores = comment.getScores();
        ApplicationForm application = comment.getApplication();
        
        if ((commentType == CommentType.REVIEW
                && BooleanUtils.isTrue(application.getLatestReviewRound().getUseCustomQuestions())) 
                || (commentType == CommentType.INTERVIEW
                        && BooleanUtils.isTrue(application.getLatestInterview().getUseCustomQuestions()))
                || (commentType == CommentType.REFERENCE
                        && BooleanUtils.isTrue(application.getUseCustomReferenceQuestions()))) {
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
