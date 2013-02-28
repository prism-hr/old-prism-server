package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.domain.ReviewRound;

@Component
public class ReviewRoundValidator extends AbstractValidator {

	@Override
	public boolean supports(Class<?> clazz) {
		return ReviewRound.class.equals(clazz);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {

		ReviewRound reviewRound = (ReviewRound) target;

		if (reviewRound.getReviewers().isEmpty()) {
			errors.rejectValue("reviewers", EMPTY_DROPDOWN_ERROR_MESSAGE);
		}

	}

}
