package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_ADVERT_CATEGORIES_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_ADVERT_CATEGORIES_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_ADVERT_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_ADVERT_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_APPLICATION_FORM_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_CLOSING_DATES_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_COMPETENCES_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_COMPETENCES_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_CONFIGURATION_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_DEPARTMENT_PROGRAMS_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_DEPARTMENT_PROGRAMS_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_EMAIL_TEMPLATES_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_FEES_PAYMENTS_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_MANAGE_USERS_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_RESUME_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_STATISTICS_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_SUMMARY_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_SUMMARY_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_TARGETS_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_TARGETS_INCOMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_TIMELINE_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_TRANSLATIONS_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_USER_BOUNCES_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RESOURCE_WORKFLOW_HEADER;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.resource.ResourceSectionRepresentation;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceAdvertCategoriesEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceAdvertCompetencesEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceAdvertEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceAdvertTargetsEvaluator;
import com.zuehlke.pgadmissions.workflow.evaluators.ResourceSummaryEvaluator;

public class ResourceSectionBuilder {

    private List<ResourceSectionRepresentation> sections = new LinkedList<>();

    public static List<ResourceSectionRepresentation> buildResourceParentSections() {
        return new ResourceSectionBuilder()
                .addResourceSummarySection()
                .addResourceParentSections(false, false)
                .build();
    }

    public static List<ResourceSectionRepresentation> buildOpportunitySections() {
        return new ResourceSectionBuilder()
                .addResourceSummarySection()
                .addResourceParentSections(true, false)
                .build();
    }

    public static List<ResourceSectionRepresentation> buildDepartmentSections() {
        return new ResourceSectionBuilder()
                .addResourceSummarySection()
                .addResourceParentSections(false, true)
                .build();
    }

    public static List<ResourceSectionRepresentation> buildSystemSections() {
        return new ResourceSectionBuilder()
                .addSections(new ResourceSectionRepresentation()
                        .withDisplayProperty(SYSTEM_RESOURCE_STATISTICS_HEADER))
                .addSections(buildResourceConfigurationSections().toArray(new ResourceSectionRepresentation[0]))
                .build();
    }

    public static List<ResourceSectionRepresentation> buildResumeSections() {
        return new ResourceSectionBuilder()
                .addSections(new ResourceSectionRepresentation()
                        .withDisplayProperty(SYSTEM_RESOURCE_RESUME_HEADER),
                        new ResourceSectionRepresentation() //
                                .withDisplayProperty(SYSTEM_RESOURCE_TIMELINE_HEADER))
                .build();
    }
    
    public static List<ResourceSectionRepresentation> buildApplicationSections() {
        return new ResourceSectionBuilder()
                .addSections(new ResourceSectionRepresentation()
                        .withDisplayProperty(SYSTEM_RESOURCE_APPLICATION_FORM_HEADER),
                        new ResourceSectionRepresentation() //
                                .withDisplayProperty(SYSTEM_RESOURCE_TIMELINE_HEADER))
                .build();
    }

    private static List<ResourceSectionRepresentation> buildResourceConfigurationSections() {
        return new ResourceSectionBuilder()
                .addSections(new ResourceSectionRepresentation()
                        .withDisplayProperty(SYSTEM_RESOURCE_MANAGE_USERS_HEADER),
                        new ResourceSectionRepresentation()
                                .withDisplayProperty(SYSTEM_RESOURCE_USER_BOUNCES_HEADER),
                        new ResourceSectionRepresentation()
                                .withDisplayProperty(SYSTEM_RESOURCE_EMAIL_TEMPLATES_HEADER),
                        new ResourceSectionRepresentation()
                                .withDisplayProperty(SYSTEM_RESOURCE_TRANSLATIONS_HEADER),
                        new ResourceSectionRepresentation()
                                .withDisplayProperty(SYSTEM_RESOURCE_WORKFLOW_HEADER))
                .build();
    }

    private static List<ResourceSectionRepresentation> buildAdvertSections(boolean isOpportunity) {
        ResourceSectionBuilder builder = new ResourceSectionBuilder();
        builder.addSections(new ResourceSectionRepresentation()
                .withDisplayProperty(SYSTEM_RESOURCE_ADVERT_CATEGORIES_HEADER)
                .withCompletenessEvaluator(ResourceAdvertCategoriesEvaluator.class)
                .withIncompleteExplanation(SYSTEM_RESOURCE_ADVERT_CATEGORIES_INCOMPLETE));

        if (isOpportunity) {
            builder.addSections(new ResourceSectionRepresentation()
                    .withDisplayProperty(SYSTEM_RESOURCE_FEES_PAYMENTS_HEADER),
                    new ResourceSectionRepresentation() //
                            .withDisplayProperty(SYSTEM_RESOURCE_CLOSING_DATES_HEADER));
        }

        return builder.build();
    }

    public List<ResourceSectionRepresentation> build() {
        return sections;
    }

    public ResourceSectionBuilder addResourceSummarySection() {
        addSections(new ResourceSectionRepresentation()
                .withDisplayProperty(SYSTEM_RESOURCE_SUMMARY_HEADER)
                .withCompletenessEvaluator(ResourceSummaryEvaluator.class)
                .withIncompleteExplanation(SYSTEM_RESOURCE_SUMMARY_INCOMPLETE));
        return this;
    }

    public ResourceSectionBuilder addResourceParentSections(boolean isOpportunity, boolean isDepartment) {
        ResourceSectionRepresentation summarySection = new ResourceSectionRepresentation()
                .withDisplayProperty(SYSTEM_RESOURCE_SUMMARY_HEADER)
                .withCompletenessEvaluator(ResourceSummaryEvaluator.class)
                .withIncompleteExplanation(SYSTEM_RESOURCE_SUMMARY_INCOMPLETE);

        if (isDepartment) {
            summarySection.withSubsections(new ResourceSectionRepresentation()
                    .withDisplayProperty(SYSTEM_RESOURCE_DEPARTMENT_PROGRAMS_HEADER)
                    .withCompletenessEvaluator(ResourceSummaryEvaluator.class)
                    .withIncompleteExplanation(SYSTEM_RESOURCE_DEPARTMENT_PROGRAMS_INCOMPLETE));
        }

        addSections(summarySection);

        addSections(new ResourceSectionRepresentation()
                .withDisplayProperty(SYSTEM_RESOURCE_ADVERT_HEADER)
                .withCompletenessEvaluator(ResourceAdvertEvaluator.class)
                .withIncompleteExplanation(SYSTEM_RESOURCE_ADVERT_INCOMPLETE)
                .withSubsections(buildAdvertSections(isOpportunity)),
                new ResourceSectionRepresentation()
                        .withDisplayProperty(SYSTEM_RESOURCE_TARGETS_HEADER)
                        .withCompletenessEvaluator(ResourceAdvertTargetsEvaluator.class)
                        .withIncompleteExplanation(SYSTEM_RESOURCE_TARGETS_INCOMPLETE),
                new ResourceSectionRepresentation()
                        .withDisplayProperty(SYSTEM_RESOURCE_COMPETENCES_HEADER)
                        .withCompletenessEvaluator(ResourceAdvertCompetencesEvaluator.class)
                        .withIncompleteExplanation(SYSTEM_RESOURCE_COMPETENCES_INCOMPLETE),
                new ResourceSectionRepresentation()
                        .withDisplayProperty(SYSTEM_RESOURCE_STATISTICS_HEADER),
                new ResourceSectionRepresentation()
                        .withDisplayProperty(SYSTEM_RESOURCE_TIMELINE_HEADER),
                new ResourceSectionRepresentation()
                        .withDisplayProperty(SYSTEM_RESOURCE_CONFIGURATION_HEADER)
                        .withSubsections(buildResourceConfigurationSections()));
        return this;
    }

    private ResourceSectionBuilder addSections(ResourceSectionRepresentation... newSections) {
        sections.addAll(Arrays.asList(newSections));
        return this;
    }
}
