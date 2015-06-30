package com.zuehlke.pgadmissions.workflow.resolvers.validation;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField.ASSIGNED_USERS;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField;

@Component
public class ApplicationDelegateAdministrationProcessor implements PrismValidationCaveatProcessor {

	@Override
	public boolean validate(PrismActionCommentField field) {
		return field.equals(ASSIGNED_USERS);
	}

}
