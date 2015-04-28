package com.zuehlke.pgadmissions.workflow.validation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

@Retention(RUNTIME)
@Target(ElementType.FIELD)
public @interface PrismConstraintRequiredStateTransition {

	PrismState state();

	PrismAction action();

	PrismState transitionState();
	
	PrismDisplayPropertyDefinition error();

}
