package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.ApprovalRound;

@Component
public class ApprovalRoundValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return ApprovalRound.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		ApprovalRound approvalRound = (ApprovalRound) target;

		if (approvalRound.getSupervisors().isEmpty()) {
			errors.rejectValue("supervisors", "dropdown.radio.select.none");
		}

	}

}
