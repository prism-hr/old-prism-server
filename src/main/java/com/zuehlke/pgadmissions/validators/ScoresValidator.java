package com.zuehlke.pgadmissions.validators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.propertyeditors.ScoresPropertyEditor;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;

@Component
public class ScoresValidator extends AbstractValidator {

	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final Logger log = LoggerFactory.getLogger(ScoresValidator.class);

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(Score.class);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		Score score = (Score) target;

		Question question = score.getOriginalQuestion();
		boolean required = BooleanUtils.toBoolean(question.isRequired());
		switch (score.getQuestionType()) {
		case TEXT:
		case TEXTAREA:
			if (required && StringUtils.isBlank(score.getTextResponse())) {
				errors.rejectValue(null, EMPTY_FIELD_ERROR_MESSAGE);
			}
			break;
		case DATE:
			Date date = score.getDateResponse();
			Date minDate = parseQuestionDate(question.getMinDate());
			Date maxDate = parseQuestionDate(question.getMaxDate());
			if (required && date == null) {
				errors.rejectValue(null, EMPTY_FIELD_ERROR_MESSAGE);
			} else if (date != null) {
				if (minDate != null && date.before(minDate)) {
					errors.rejectValue(null, NOT_BEFORE_ERROR_MESSAGE, new Object[] { formatDateForErrorMessage(minDate) }, null);
				} else if (maxDate != null && date.after(maxDate)) {
					errors.rejectValue(null, NOT_AFTER_ERROR_MESSAGE, new Object[] { formatDateForErrorMessage(maxDate) }, null);
				}
			}
			break;
		// case DATE_RANGE:
		// date = score.getDateResponse();
		// Date secondDate = score.getSecondDateResponse();
		// minDate = parseQuestionDate(question.getMinDate());
		// maxDate = parseQuestionDate(question.getMaxDate());
		// if (required && (date == null || secondDate == null)) {
		// errors.rejectValue("scores[" + i + "]", EMPTY_FIELD_ERROR_MESSAGE);
		// }
		// if (date != null && secondDate != null) {
		// if (date.after(secondDate)) {
		// errors.rejectValue("scores[" + i + "]", "daterange.field.notafter");
		// } else if (minDate != null && date.before(minDate)) {
		// errors.rejectValue("scores[" + i + "]", NOT_BEFORE_ERROR_MESSAGE, new
		// Object[] { minDate }, null);
		// } else if (maxDate != null && secondDate.after(maxDate)) {
		// errors.rejectValue("scores[" + i + "]", NOT_AFTER_ERROR_MESSAGE, new
		// Object[] { maxDate }, null);
		// }
		// }
		// break;
		case DROPDOWN:
			if (required && StringUtils.isBlank(score.getTextResponse())) {
				errors.rejectValue(null, EMPTY_FIELD_ERROR_MESSAGE);
			}
			break;
		case RATING:
			if (required && score.getRatingResponse() == null) {
				errors.rejectValue(null, EMPTY_FIELD_ERROR_MESSAGE);
			} else if (score.getRatingResponse() != null && (score.getRatingResponse() < 0 || score.getRatingResponse() > 5)) {
				errors.rejectValue(null, EMPTY_FIELD_ERROR_MESSAGE);
			}
			break;
		default:
			break;
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
			return DateUtils.parseDate(dateString, new String[] { DATE_FORMAT });
		} catch (ParseException e) {
		}
		log.error("Unknown date format: " + dateString);
		return null;
	}

	private String formatDateForErrorMessage(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(ScoresPropertyEditor.DATE_FORMAT_ON_CLIENT_SIDE);
		return dateFormat.format(date);
	}

}
