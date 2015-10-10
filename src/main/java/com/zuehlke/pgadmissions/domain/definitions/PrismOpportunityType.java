package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITY_TYPE_PLACEMENT_DESCRIPTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITY_TYPE_WORK_EXPERIENCE_DESCRIPTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITY_TYPE_WORK_VOLUNTEERING_DESCRIPTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.EXPERIENCE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.PERSONAL_DEVELOPMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.STUDY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.WORK;

import java.util.List;

import com.google.common.collect.LinkedListMultimap;

import uk.co.alumeni.prism.api.model.advert.EnumDefinition;

public enum PrismOpportunityType implements EnumDefinition<uk.co.alumeni.prism.enums.PrismOpportunityType>,
    PrismLocalizableDefinition {

    STUDY_UNDERGRADUATE(STUDY, false, null), //
    STUDY_POSTGRADUATE_TAUGHT(STUDY, false, null), //
    STUDY_POSTGRADUATE_RESEARCH(STUDY, false, null), //
    TRAINING(PERSONAL_DEVELOPMENT, false, null), //
    WORK_EXPERIENCE(EXPERIENCE, true, SYSTEM_OPPORTUNITY_TYPE_WORK_EXPERIENCE_DESCRIPTION), //
    PLACEMENT(EXPERIENCE, true, SYSTEM_OPPORTUNITY_TYPE_PLACEMENT_DESCRIPTION), //
    VOLUNTEERING(EXPERIENCE, true, SYSTEM_OPPORTUNITY_TYPE_WORK_VOLUNTEERING_DESCRIPTION), //
    EMPLOYMENT(WORK, true, null); //

    private PrismOpportunityCategory opportunityCategory;

    private boolean published;

    private PrismDisplayPropertyDefinition description;

    private static final LinkedListMultimap<PrismOpportunityCategory, PrismOpportunityType> byCategory = LinkedListMultimap.create();

    static {
        for (PrismOpportunityType opportunityType : PrismOpportunityType.values()) {
            byCategory.put(opportunityType.getOpportunityCategory(), opportunityType);
        }
    }

    private PrismOpportunityType(PrismOpportunityCategory opportunityCategory, boolean published, PrismDisplayPropertyDefinition description) {
        this.opportunityCategory = opportunityCategory;
        this.published = published;
        this.description = description;
    }

    public PrismOpportunityCategory getOpportunityCategory() {
        return opportunityCategory;
    }

    public boolean isPublished() {
        return published;
    }

    public PrismDisplayPropertyDefinition getDescription() {
        return description;
    }

    public static List<PrismOpportunityType> getOpportunityTypes(PrismOpportunityCategory opportunityCategory) {
        return byCategory.get(opportunityCategory);
    }

    public static PrismOpportunityType getSystemOpportunityType() {
        return EMPLOYMENT;
    }

    @Override
    public uk.co.alumeni.prism.enums.PrismOpportunityType getDefinition() {
        return uk.co.alumeni.prism.enums.PrismOpportunityType.valueOf(name());
    }

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_OPPORTUNITY_TYPE_" + name());
    }

}
