package com.zuehlke.pgadmissions.validators;

import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.dto.ApplicationFormDetails;

public class ApplicationFormValidator implements Validator{


	@Override
	public boolean supports(Class<?> clazz) {
		return ApplicationFormDetails.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ApplicationFormDetails applicationFormDetails = (ApplicationFormDetails) target;
		if (applicationFormDetails.getNumberOfAddresses() == 0) {
			errors.rejectValue("numberOfAddresses", "user.addresses.notempty");
		}

		if (applicationFormDetails.getNumberOfReferees() < 2) {
			errors.rejectValue("numberOfReferees", "user.referees.notvalid");
		}

		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationFormDetails.getProgrammeDetails(), "programmeDetails");
		if (applicationFormDetails.getProgrammeDetails() == null) {
			errors.rejectValue("programmeDetails", "user.personalDetails.incomplete");
		} else {
			ProgrammeDetailsValidator validator = new ProgrammeDetailsValidator();
			validator.validate(applicationFormDetails.getProgrammeDetails(), mappingResult);
			if (mappingResult.hasErrors()) {
				errors.rejectValue("programmeDetails", "user.programmeDetails.incomplete");
			}
		}

		mappingResult = new DirectFieldBindingResult(applicationFormDetails.getPersonalDetails(), "personalDetails");

		if (applicationFormDetails.getPersonalDetails() == null) {
			errors.rejectValue("personalDetails", "user.personalDetails.incomplete");
		} else {
			PersonalDetailValidator validator = new PersonalDetailValidator();
			validator.validate(applicationFormDetails.getPersonalDetails(), mappingResult);
			if (mappingResult.hasErrors()) {
				errors.rejectValue("personalDetails", "user.personalDetails.incomplete");
			}
		}

		if (applicationFormDetails.getSupportingDocuments() != null) {
			boolean hasUploadedResume = false;
			boolean hasUploadedPersonalStatement = false;

			for (Document supportingDocument : applicationFormDetails.getSupportingDocuments()) {
				if (supportingDocument.getType() == DocumentType.CV) {
					hasUploadedResume = true;
				} else if (supportingDocument.getType() == DocumentType.PERSONAL_STATEMENT) {
					hasUploadedPersonalStatement = true;
				} else {
					throw new RuntimeException("Unsupported document type encountered!");
				}
			}
			
			if (!(hasUploadedPersonalStatement && hasUploadedResume)) {
				errors.rejectValue("supportingDocuments", "user.supportingDocuments.incomplete");
			}
		} else {
			errors.rejectValue("supportingDocuments", "user.supportingDocuments.incomplete");
		}

	}
}
