package com.zuehlke.pgadmissions.validators;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;

@Component
public class FeedbackCommentValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(ReviewComment.class) || clazz.isAssignableFrom(InterviewComment.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if (target instanceof ReviewComment) {
			ReviewComment comment = (ReviewComment) target;
			if(!comment.isDecline() ){
				if(comment.getSuitableCandidate() == null ){
					errors.rejectValue("suitableCandidate", "feedbackComment.suitableCandidate.notempty");
				}
				if(comment.getWillingToInterview() == null){
					errors.rejectValue("willingToInterview", "feedbackComment.willingToInterview.notempty");
				}
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", "feedbackComment.comment.notempty");
			}
		}
		else if (target instanceof InterviewComment) {
			InterviewComment comment = (InterviewComment) target;
			if(!comment.isDecline() ){
				if(comment.getSuitableCandidate() == null ){
					errors.rejectValue("suitableCandidate", "feedbackComment.suitableCandidate.notempty");
				}
				if(comment.getWillingToSupervise() == null){
					errors.rejectValue("willingToSupervise", "feedbackComment.willingToSupervice.notempty");
				}
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", "feedbackComment.comment.notempty");
			}
		}
		
		
	}

	
}
