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
			if(comment.getSuitableCandidate() == null || (comment.getSuitableCandidate() != CheckedStatus.YES && comment.getSuitableCandidate() != CheckedStatus.NO)){
				errors.rejectValue("suitableCandidate", "reviewComment.suitableCandidate.notempty");
			}
			if(comment.getWillingToSupervice() == null || (comment.getWillingToSupervice() != CheckedStatus.YES && comment.getWillingToSupervice() != CheckedStatus.NO)){
				errors.rejectValue("willingToSupervice", "reviewComment.willingToSupervice.notempty");
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", "reviewComment.comment.notempty");
		}
		
	}

	
}
