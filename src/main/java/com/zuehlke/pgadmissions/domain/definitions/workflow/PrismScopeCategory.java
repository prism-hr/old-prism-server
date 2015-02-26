package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;

public enum PrismScopeCategory {

	ORGANIZATION_CATEGORY, //
	DIVISION, //
	OPPORTUNITY_CATEGORY, //
	DEADLINE_CATEGORY, //
	APPLICATION_CATEGORY;
	
	public PrismDisplayPropertyDefinition getDisplayPropertyDefinition() {
		return PrismDisplayPropertyDefinition.valueOf("SYSTEM_" + name());
	}

	public PrismDisplayPropertyDefinition getDisplayPropertyDefinitionPlural() {
		return PrismDisplayPropertyDefinition.valueOf("SYSTEM_" + name() + "_PLURAL");
	}

}
