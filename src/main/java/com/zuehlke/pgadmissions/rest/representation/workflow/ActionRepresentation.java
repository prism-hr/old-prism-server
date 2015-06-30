package com.zuehlke.pgadmissions.rest.representation.workflow;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition;

public class ActionRepresentation {

	private PrismAction id;

	private PrismActionCategory actionCategory;

	private PrismActionCustomQuestionDefinition actionCustomQuestionDefinition;

	public ActionRepresentation(PrismAction id, PrismActionCategory actionCategory, PrismActionCustomQuestionDefinition actionCustomQuestionDefinition) {
		this.id = id;
		this.actionCategory = actionCategory;
		this.actionCustomQuestionDefinition = actionCustomQuestionDefinition;
	}

	public PrismAction getId() {
		return id;
	}

	public PrismActionCategory getActionCategory() {
		return actionCategory;
	}

	public PrismActionCustomQuestionDefinition getActionCustomQuestionDefinition() {
		return actionCustomQuestionDefinition;
	}

}
