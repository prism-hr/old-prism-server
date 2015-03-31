package com.zuehlke.pgadmissions.rest.representation.workflow;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCustomQuestionDefinition;

public class ActionRepresentation {

    private PrismAction id;

    private PrismActionCategory actionCategory;

    private PrismActionCustomQuestionDefinition actionCustomQuestionDefinition;

    private PrismAction.PrismActionDisplayPropertyDescriptor displayProperties;

    public ActionRepresentation(PrismAction id, PrismActionCategory actionCategory, PrismActionCustomQuestionDefinition actionCustomQuestionDefinition, PrismAction.PrismActionDisplayPropertyDescriptor displayProperties) {
        this.id = id;
        this.actionCategory = actionCategory;
        this.actionCustomQuestionDefinition = actionCustomQuestionDefinition;
        this.displayProperties = displayProperties;
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

    public PrismAction.PrismActionDisplayPropertyDescriptor getDisplayProperties() {
        return displayProperties;
    }
}
