package com.zuehlke.pgadmissions.validators;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Document;

@Component
public class DocumentValidator implements Validator {

	private static final String[] EXTENSION_WHITE_LIST = { "pdf"};

	@Override
	public boolean supports(Class<?> clazz) {
		return Document.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Document document = (Document) target;
		if (StringUtils.isBlank(document.getFileName())) {
			errors.rejectValue("fileName", "upload.file.missing");
		} else {
			
			if (document.getFileName().indexOf(".") < 0) {
				errors.rejectValue("fileName", "upload.file.invalidtype");
			} else {
				String extension = document.getFileName().substring(document.getFileName().indexOf(".") + 1, document.getFileName().length());
				if (!Arrays.asList(EXTENSION_WHITE_LIST).contains(extension)) {
					errors.rejectValue("fileName", "upload.file.invalidtype");
				}

			}
			if(document.getFileName().length() > 200){
				errors.rejectValue("fileName", "upload.file.toolong");
			}

		}
		if(document.getContent() != null && document.getContent().length > 10000000){
			errors.rejectValue("content", "upload.file.toobig");
		}

	}
}
