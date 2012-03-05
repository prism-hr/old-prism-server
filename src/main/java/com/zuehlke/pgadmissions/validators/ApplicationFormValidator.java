package com.zuehlke.pgadmissions.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.dto.PersonalDetails;

public class ApplicationFormValidator implements Validator{


	@Override
	public boolean supports(Class<?> clazz) {
		return ApplicationForm.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ApplicationForm applicationForm = (ApplicationForm) target;

		PersonalDetails ps = new PersonalDetails();
		ps.setFirstName(applicationForm.getApplicant().getFirstName());
		ps.setLastName(applicationForm.getApplicant().getLastName());
		ps.setEmail(applicationForm.getApplicant().getEmail());

		applicationForm.setPersonalDetails(ps);

		try {
			errors.pushNestedPath("personalDetails");
		ValidationUtils.invokeValidator(new PersonalDetailsValidator(), applicationForm.getPersonalDetails(), errors);
		} finally {
			errors.popNestedPath();
		}

		Address addr = new Address();
		addr.setAddress(applicationForm.getApplicant().getAddress());

		applicationForm.setAddress(addr);

		try {
			errors.pushNestedPath("address");
			ValidationUtils.invokeValidator(new AddressValidator(), applicationForm.getAddress(), errors);
		} finally {
			errors.popNestedPath();
		}

		Funding funding = new Funding();
		funding.setFunding(applicationForm.getFunding());
		applicationForm.setFund(funding);

		try {
			errors.pushNestedPath("fund");
			ValidationUtils.invokeValidator(new FundingValidator(), applicationForm.getFund(), errors);
		} finally {
			errors.popNestedPath();
		}
	}

}
