package com.zuehlke.pgadmissions.workflow.resolvers.validation;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField;

public interface PrismValidationCaveatProcessor {

	public boolean validate(PrismActionCommentField field);

}
