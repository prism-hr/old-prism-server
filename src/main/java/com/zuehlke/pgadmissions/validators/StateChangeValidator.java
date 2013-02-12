package com.zuehlke.pgadmissions.validators;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.StateChangeComment;
import com.zuehlke.pgadmissions.domain.ValidationComment;

@Component
public class StateChangeValidator extends AbstractValidator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(ValidationComment.class) || clazz.isAssignableFrom(StateChangeComment.class);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		if (target instanceof ValidationComment) {
			ValidationComment comment = (ValidationComment) target;
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", "text.field.empty");
			if(comment.getQualifiedForPhd() == null ){
				errors.rejectValue("qualifiedForPhd", "dropdown.radio.select.none");
			}
			if(comment.getEnglishCompentencyOk() == null ){
				errors.rejectValue("englishCompentencyOk", "dropdown.radio.select.none");
			}
			if(comment.getHomeOrOverseas() == null ){
				errors.rejectValue("homeOrOverseas", "dropdown.radio.select.none");
			}
			if(comment.getNextStatus() == null ){
				errors.rejectValue("nextStatus", "dropdown.radio.select.none");
			}
		}
		else if (target instanceof StateChangeComment) {
			StateChangeComment comment = (StateChangeComment) target;
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", "text.field.empty");
			if(comment.getNextStatus() == null ){
				errors.rejectValue("nextStatus", "dropdown.radio.select.none");
			}
			
		}
	}
}
