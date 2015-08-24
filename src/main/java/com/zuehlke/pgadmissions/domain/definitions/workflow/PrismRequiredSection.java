package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_ADVERT_CATEGORIES_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_ADVERT_DETAILS_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_COMPETENCES_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_DEPARTMENT_PROGRAMS_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_DETAILS_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_TARGETS_INCOMPLETE;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.workflow.evaluators.DepartmentProgramsEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceAdvertCategoriesEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceAdvertCompetencesEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceAdvertDetailsEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceAdvertTargetsEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceCompletenessEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceDetailsEvaluator;

public enum PrismRequiredSection {

    RESOURCE_DETAILS(ResourceDetailsEvaluator.class, SYSTEM_RESOURCE_DETAILS_INCOMPLETE),
    DEPARTMENT_PROGRAMS(DepartmentProgramsEvaluator.class, SYSTEM_RESOURCE_DEPARTMENT_PROGRAMS_INCOMPLETE),
    ADVERT_CATEGORIES(ResourceAdvertCategoriesEvaluator.class, SYSTEM_RESOURCE_ADVERT_CATEGORIES_INCOMPLETE),
    ADVERT_DETAILS(ResourceAdvertDetailsEvaluator.class, SYSTEM_RESOURCE_ADVERT_DETAILS_INCOMPLETE),
    ADVERT_COMPETENCES(ResourceAdvertCompetencesEvaluator.class, SYSTEM_RESOURCE_COMPETENCES_INCOMPLETE),
    ADVERT_TARGETS(ResourceAdvertTargetsEvaluator.class, SYSTEM_RESOURCE_TARGETS_INCOMPLETE);

    private Class<? extends ResourceCompletenessEvaluator<?>> completenessEvaluator;

    private PrismDisplayPropertyDefinition incompleteExplanation;

    PrismRequiredSection(Class<? extends ResourceCompletenessEvaluator<?>> completenessEvaluator, PrismDisplayPropertyDefinition incompleteExplanation) {
        this.completenessEvaluator = completenessEvaluator;
        this.incompleteExplanation = incompleteExplanation;
    }

    public Class<? extends ResourceCompletenessEvaluator<?>> getCompletenessEvaluator() {
        return completenessEvaluator;
    }

    public PrismDisplayPropertyDefinition getIncompleteExplanation() {
        return incompleteExplanation;
    }
}
