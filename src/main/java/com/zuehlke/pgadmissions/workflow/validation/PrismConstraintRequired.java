package com.zuehlke.pgadmissions.workflow.validation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;

@Retention(RUNTIME)
@Target(ElementType.FIELD)
public @interface PrismConstraintRequired {
	
	PrismDisplayPropertyDefinition error();

}
