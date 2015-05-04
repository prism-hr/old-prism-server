package com.zuehlke.pgadmissions.workflow.resolvers.validation;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCommentField;

public interface PrismValidationCaveatProcessor {

	boolean validate(PrismActionCommentField field);

}
