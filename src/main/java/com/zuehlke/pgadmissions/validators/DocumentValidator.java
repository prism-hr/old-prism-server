package com.zuehlke.pgadmissions.validators;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.domain.Document;

@Component
public class DocumentValidator extends AbstractValidator {

	private static final String[] EXTENSION_WHITE_LIST = { "pdf"};

	@Override
	public boolean supports(Class<?> clazz) {
		return Document.class.isAssignableFrom(clazz);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		Document document = (Document) target;
		if (StringUtils.isBlank(document.getFileName())) {
			errors.rejectValue("fileName", "file.upload.empty");
		} else {
			
			if (document.getFileName().indexOf(".") < 0) {
				errors.rejectValue("fileName", "file.upload.notPDF");
			} else {
				String extension = document.getFileName().substring(document.getFileName().lastIndexOf(".") + 1, document.getFileName().length());
				if (!Arrays.asList(EXTENSION_WHITE_LIST).contains(extension)) {
					errors.rejectValue("fileName", "file.upload.notPDF");
				}

			}
			if(document.getFileName().length() > 200){
				errors.rejectValue("fileName", "upload.file.toolong");
			}

		}
		if(document.getContent() != null && document.getContent().length > 10000000){
			errors.rejectValue("content", "file.upload.large");
		}

	}
}
