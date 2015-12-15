package uk.co.alumeni.prism.domain.definitions.workflow;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_ADVERT_CATEGORIES_INCOMPLETE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_ADVERT_DETAILS_INCOMPLETE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_COMPETENCES_INCOMPLETE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_DETAILS_INCOMPLETE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_TARGETS_INCOMPLETE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROJECT;

import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;

import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.workflow.evaluators.ResourceAdvertCategoriesEvaluator;
import uk.co.alumeni.prism.workflow.evaluators.ResourceAdvertCompetencesEvaluator;
import uk.co.alumeni.prism.workflow.evaluators.ResourceAdvertDetailsEvaluator;
import uk.co.alumeni.prism.workflow.evaluators.ResourceAdvertTargetsEvaluator;
import uk.co.alumeni.prism.workflow.evaluators.ResourceCompletenessEvaluator;
import uk.co.alumeni.prism.workflow.evaluators.ResourceDetailsEvaluator;

public enum PrismScopeSectionDefinition {

    RESOURCE_DETAILS(ResourceDetailsEvaluator.class, SYSTEM_RESOURCE_DETAILS_INCOMPLETE), //
    ADVERT_CATEGORIES(ResourceAdvertCategoriesEvaluator.class, SYSTEM_RESOURCE_ADVERT_CATEGORIES_INCOMPLETE), //
    ADVERT_DETAILS(ResourceAdvertDetailsEvaluator.class, SYSTEM_RESOURCE_ADVERT_DETAILS_INCOMPLETE), //
    ADVERT_COMPETENCES(ResourceAdvertCompetencesEvaluator.class, SYSTEM_RESOURCE_COMPETENCES_INCOMPLETE), //
    ADVERT_TARGETS(ResourceAdvertTargetsEvaluator.class, SYSTEM_RESOURCE_TARGETS_INCOMPLETE);

    private static HashMultimap<PrismScope, PrismScopeSectionDefinition> requiredSections = HashMultimap.create();

    static {
        requiredSections.putAll(INSTITUTION, getDefaultRequiredSections());
        requiredSections.putAll(DEPARTMENT, getDefaultRequiredSections());
        requiredSections.putAll(PROGRAM, getDefaultRequiredSections());
        requiredSections.put(PROGRAM, ADVERT_COMPETENCES);
        requiredSections.putAll(PROJECT, getDefaultRequiredSections());
        requiredSections.put(PROJECT, ADVERT_COMPETENCES);
    }

    private Class<? extends ResourceCompletenessEvaluator<?>> completenessEvaluator;

    private PrismDisplayPropertyDefinition incompleteExplanation;

    private PrismScopeSectionDefinition(Class<? extends ResourceCompletenessEvaluator<?>> completenessEvaluator, PrismDisplayPropertyDefinition incompleteExplanation) {
        this.completenessEvaluator = completenessEvaluator;
        this.incompleteExplanation = incompleteExplanation;
    }

    public Class<? extends ResourceCompletenessEvaluator<?>> getCompletenessEvaluator() {
        return completenessEvaluator;
    }

    public PrismDisplayPropertyDefinition getIncompleteExplanation() {
        return incompleteExplanation;
    }

    private static List<PrismScopeSectionDefinition> getDefaultRequiredSections() {
        return Lists.newArrayList(RESOURCE_DETAILS, ADVERT_DETAILS, ADVERT_CATEGORIES, ADVERT_TARGETS);
    }

    public static Set<PrismScopeSectionDefinition> getRequiredSections(PrismScope scope) {
        return requiredSections.get(scope);
    }

}
