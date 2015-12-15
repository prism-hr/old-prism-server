package uk.co.alumeni.prism.domain.definitions.workflow;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.workflow.evaluators.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.*;

public enum PrismScopeSectionDefinition {

    IMAGES(ResourceImagesEvaluator.class, SYSTEM_RESOURCE_IMAGES_HEADER, SYSTEM_RESOURCE_IMAGES_INCOMPLETE),
    DETAILS(ResourceDetailsEvaluator.class, SYSTEM_RESOURCE_SUMMARY_HEADER, SYSTEM_RESOURCE_DETAILS_INCOMPLETE),
    CATEGORIES(ResourceCategoriesEvaluator.class, SYSTEM_RESOURCE_ADVERT_CATEGORIES_HEADER, SYSTEM_RESOURCE_CATEGORIES_INCOMPLETE),
    COMPETENCES(ResourceCompetencesEvaluator.class, SYSTEM_RESOURCE_COMPETENCES_HEADER, SYSTEM_RESOURCE_COMPETENCES_INCOMPLETE),
    TARGETS(ResourceTargetsEvaluator.class, SYSTEM_RESOURCE_TARGETS_HEADER, SYSTEM_RESOURCE_TARGETS_INCOMPLETE);

    private static HashMultimap<PrismScope, PrismScopeSectionDefinition> requiredSections = HashMultimap.create();

    private static SetMultimap<PrismScopeSectionDefinition, PrismScope> scopesPerSection = HashMultimap.create();

    static {
        requiredSections.putAll(PrismScope.INSTITUTION, getCommonRequiredSections());
        requiredSections.putAll(PrismScope.DEPARTMENT, getCommonRequiredSections());
        requiredSections.putAll(PrismScope.PROGRAM, getCommonRequiredSections());
        requiredSections.putAll(PrismScope.PROJECT, getCommonRequiredSections());
        requiredSections.put(PrismScope.PROGRAM, COMPETENCES);
        requiredSections.put(PrismScope.PROJECT, COMPETENCES);
        requiredSections.putAll(PrismScope.DEPARTMENT, getResourceParentRequiredSections());
        requiredSections.putAll(PrismScope.INSTITUTION, getResourceParentRequiredSections());

        for (Map.Entry<PrismScope, PrismScopeSectionDefinition> entry : requiredSections.entries()) {
            scopesPerSection.put(entry.getValue(), entry.getKey());
        }
    }

    private final Class<? extends ResourceCompletenessEvaluator<?>> completenessEvaluator;

    private final PrismDisplayPropertyDefinition name;

    private final PrismDisplayPropertyDefinition incompleteExplanation;

    PrismScopeSectionDefinition(Class<? extends ResourceCompletenessEvaluator<?>> completenessEvaluator, PrismDisplayPropertyDefinition name, PrismDisplayPropertyDefinition incompleteExplanation) {
        this.completenessEvaluator = completenessEvaluator;
        this.name = name;
        this.incompleteExplanation = incompleteExplanation;
    }

    private static List<PrismScopeSectionDefinition> getCommonRequiredSections() {
        return Lists.newArrayList(DETAILS, CATEGORIES);
    }

    private static List<PrismScopeSectionDefinition> getResourceParentRequiredSections() {
        return Lists.newArrayList(IMAGES, TARGETS);
    }

    public static Set<PrismScopeSectionDefinition> getRequiredSections(PrismScope scope) {
        return requiredSections.get(scope);
    }

    public static Set<PrismScope> getScopes(PrismScopeSectionDefinition section) {
        return scopesPerSection.get(section);
    }

    public Class<? extends ResourceCompletenessEvaluator<?>> getCompletenessEvaluator() {
        return completenessEvaluator;
    }

    public PrismDisplayPropertyDefinition getName() {
        return name;
    }

    public PrismDisplayPropertyDefinition getIncompleteExplanation() {
        return incompleteExplanation;
    }

}
