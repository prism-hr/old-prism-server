package com.zuehlke.pgadmissions.validators;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;

@Component
public class FeedbackCommentValidator extends AbstractValidator {

    private static final Logger log = LoggerFactory.getLogger(FeedbackCommentValidator.class);

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
            Question question = score.getOriginalQuestion();
            boolean required = BooleanUtils.toBoolean(question.isRequired());
            switch (score.getQuestionType()) {
            case TEXT:
            case TEXTAREA:
                if (required && StringUtils.isBlank(score.getTextResponse())) {
                    errors.rejectValue("scores[" + i + "]", EMPTY_FIELD_ERROR_MESSAGE);
                }
                break;
            case DATE:
                Date date = score.getDateResponse();
                Date minDate = parseQuestionDate(question.getMinDate());
                Date maxDate = parseQuestionDate(question.getMaxDate());
                if (required && date == null) {
                    errors.rejectValue("scores[" + i + "]", EMPTY_FIELD_ERROR_MESSAGE);
                } else if (date != null && minDate != null && date.before(minDate)) {
                    errors.rejectValue("scores[" + i + "]", NOT_BEFORE_ERROR_MESSAGE, new Object[] { minDate }, null);
                } else if (date != null && maxDate != null && date.after(maxDate)) {
                    errors.rejectValue("scores[" + i + "]", NOT_AFTER_ERROR_MESSAGE, new Object[] { maxDate }, null);
                }
                break;
            default:
                break;
            }
        }
    }

    private Date parseQuestionDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        if ("today".equalsIgnoreCase(dateString)) {
            return DateUtils.round(new Date(), Calendar.DAY_OF_MONTH);
        }
        try {
            return DateUtils.parseDate(dateString, new String[] { "yyyy-MM-dd" });
        } catch (ParseException e) {
        }
        log.error("Unknown date format: " + dateString);
        return null;

    }

}
