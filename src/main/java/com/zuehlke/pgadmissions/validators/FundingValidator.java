package com.zuehlke.pgadmissions.validators;

import java.io.IOException;

import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.dto.Funding;

public class FundingValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Funding.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fundingType", "user.fundingType.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fundingDescription", "user.fundingDescription.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fundingValue", "user.fundingValue.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fundingAwardDate", "user.fundingAwardDate.notempty");

		Funding fund = (Funding) target;

		if (fund.getFundingFile() != null) {
			Document document = new Document();
			document.setFileName(fund.getFundingFile().getOriginalFilename());
			document.setContentType(fund.getFundingFile().getContentType());
			try {
				document.setContent(fund.getFundingFile().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			document.setType(DocumentType.SUPPORTING_FUNDING);
			
			BindingResult errorsSupportingDocument = newErrors(document);
			DocumentValidator documentValidator = new DocumentValidator();
			documentValidator .validate(document, errorsSupportingDocument);
			if(errorsSupportingDocument.hasFieldErrors("fileName")){
				errors.rejectValue("fundingFile",  errorsSupportingDocument.getFieldError("fileName").getCode());
			}
		} else {
			errors.rejectValue("fundingFile", "upload.file.missing");
		}

	}
	
	BindingResult newErrors(Document document) {
		return new DirectFieldBindingResult(document, document.getFileName());
	}

}
