package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_PLURAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DEADLINE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DEADLINE_PLURAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DIVISION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DIVISION_PLURAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITY_PLURAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ORGANIZATION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ORGANIZATION_PLURAL;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;

public enum PrismScopeCategory {

	ORGANIZATION(SYSTEM_ORGANIZATION, SYSTEM_ORGANIZATION_PLURAL), //
	DIVISION(SYSTEM_DIVISION, SYSTEM_DIVISION_PLURAL), //
	OPPORTUNITY(SYSTEM_OPPORTUNITY, SYSTEM_OPPORTUNITY_PLURAL), //
	DEADLINE(SYSTEM_DEADLINE, SYSTEM_DEADLINE_PLURAL), //
	APPLICATION(SYSTEM_APPLICATION, SYSTEM_APPLICATION_PLURAL);

	private PrismDisplayPropertyDefinition displayPropertyDefinition;

	private PrismDisplayPropertyDefinition displayPropertyDefinitionPlural;

	private PrismScopeCategory(PrismDisplayPropertyDefinition displayPropertyDefinition, PrismDisplayPropertyDefinition displayPropertyDefinitionPlural) {
		this.displayPropertyDefinition = displayPropertyDefinition;
		this.displayPropertyDefinitionPlural = displayPropertyDefinitionPlural;
	}

	public PrismDisplayPropertyDefinition getDisplayPropertyDefinition() {
		return displayPropertyDefinition;
	}

	public PrismDisplayPropertyDefinition getDisplayPropertyDefinitionPlural() {
		return displayPropertyDefinitionPlural;
	}

}
