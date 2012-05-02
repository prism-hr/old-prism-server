package com.zuehlke.pgadmissions.validators;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;

@Component
public class ReviewFeedbackValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(ReviewComment.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ReviewComment comment = (ReviewComment) target;
		if(comment.getDecline() ==  CheckedStatus.NO){
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "willingToSupervice", "reviewComment.willingToSupervice.notempty");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "suitableCandidate", "reviewComment.suitableCandidate.notempty");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", "reviewComment.comment.notempty");
		}
		
	}

	
}
