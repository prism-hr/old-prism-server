package com.zuehlke.pgadmissions.validators;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;

@Component
public class FeedbackCommentValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(ReviewComment.class) || clazz.isAssignableFrom(InterviewComment.class) || clazz.isAssignableFrom(ReferenceComment.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if (target instanceof ReviewComment) {
			ReviewComment comment = (ReviewComment) target;
			if(!comment.isDecline() ){
				if(comment.getSuitableCandidate() == null ){
					errors.rejectValue("suitableCandidate", "dropdown.radio.select.none");
				}
				if(comment.getWillingToInterview() == null){
					errors.rejectValue("willingToInterview", "dropdown.radio.select.none");
				}
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", "text.field.empty");
			}
		}
		else if (target instanceof InterviewComment) {
			InterviewComment comment = (InterviewComment) target;
			if(!comment.isDecline() ){
				if(comment.getSuitableCandidateForUcl() == null ){
					errors.rejectValue("suitableCandidateForUcl", "dropdown.radio.select.none");
				}
				if(comment.getSuitableCandidateForProgramme() == null ){
					errors.rejectValue("suitableCandidateForProgramme", "dropdown.radio.select.none");
				}
				if(comment.getWillingToSupervise() == null){
					errors.rejectValue("willingToSupervise", "dropdown.radio.select.none");
				}
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", "text.field.empty");
			}
		}
		else if (target instanceof ReferenceComment) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", "text.field.empty");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "suitableForProgramme", "dropdown.radio.select.none");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "suitableForUCL", "dropdown.radio.select.none");
		}
		
		
	}

	
}
