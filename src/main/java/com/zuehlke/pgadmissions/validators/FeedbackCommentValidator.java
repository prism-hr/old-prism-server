package com.zuehlke.pgadmissions.validators;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;

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
			if(!comment.isDecline() ){
				if(comment.getSuitableCandidateForUcl() == null ){
					errors.rejectValue("suitableCandidateForUcl", EMPTY_DROPDOWN_ERROR_MESSAGE);
				}
				if(comment.getSuitableCandidateForProgramme() == null ){
					errors.rejectValue("suitableCandidateForProgramme", EMPTY_DROPDOWN_ERROR_MESSAGE);
				}
				if(comment.getWillingToInterview() == null){
					errors.rejectValue("willingToInterview", EMPTY_DROPDOWN_ERROR_MESSAGE);
				}
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", EMPTY_FIELD_ERROR_MESSAGE);
			}
		}
		else if (target instanceof InterviewComment) {
			InterviewComment comment = (InterviewComment) target;
			if(!comment.isDecline() ){
				if(comment.getSuitableCandidateForUcl() == null ){
					errors.rejectValue("suitableCandidateForUcl", EMPTY_DROPDOWN_ERROR_MESSAGE);
				}
				if(comment.getSuitableCandidateForProgramme() == null ){
					errors.rejectValue("suitableCandidateForProgramme", EMPTY_DROPDOWN_ERROR_MESSAGE);
				}
				if(comment.getWillingToSupervise() == null){
					errors.rejectValue("willingToSupervise", EMPTY_DROPDOWN_ERROR_MESSAGE);
				}
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", EMPTY_FIELD_ERROR_MESSAGE);
			}
		}
		else if (target instanceof ReferenceComment) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", EMPTY_FIELD_ERROR_MESSAGE);
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "suitableForProgramme", EMPTY_DROPDOWN_ERROR_MESSAGE);
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "suitableForUCL", EMPTY_DROPDOWN_ERROR_MESSAGE);
		}
		
		
	}

	
}
