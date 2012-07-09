package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.ReviewRound;

@Component
public class ReviewRoundValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return ReviewRound.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		ReviewRound reviewRound = (ReviewRound) target;

		if (reviewRound.getReviewers().isEmpty()) {
			errors.rejectValue("reviewers", "dropdown.radio.select.none");
		}

	}

}
