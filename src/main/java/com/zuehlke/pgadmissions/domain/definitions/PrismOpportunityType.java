package com.zuehlke.pgadmissions.domain.definitions;

import com.google.common.collect.LinkedListMultimap;
import uk.co.alumeni.prism.api.model.advert.EnumDefinition;

import java.util.List;

import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.*;

public enum PrismOpportunityType implements EnumDefinition<uk.co.alumeni.prism.enums.PrismOpportunityType>, PrismLocalizableDefinition {

    STUDY_UNDERGRADUATE(false, STUDY), //
    STUDY_POSTGRADUATE_TAUGHT(false, STUDY), //
    STUDY_POSTGRADUATE_RESEARCH(false, STUDY), //
    TRAINING(false, PERSONAL_DEVELOPMENT), //
    WORK_EXPERIENCE(true, EXPERIENCE), //
    VOLUNTEERING(true, EXPERIENCE), //
    EMPLOYMENT(true, WORK), //
    EMPLOYMENT_SECONDMENT(true, WORK); //

    private static final LinkedListMultimap<PrismOpportunityCategory, PrismOpportunityType> byCategory = LinkedListMultimap.create();

    static {
        for (PrismOpportunityType opportunityType : PrismOpportunityType.values()) {
            byCategory.put(opportunityType.getCategory(), opportunityType);
        }
    }

    private boolean published;
    private PrismOpportunityCategory category;

    PrismOpportunityType(boolean published, PrismOpportunityCategory category) {
        this.published = published;
        this.category = category;
    }

    public static List<PrismOpportunityType> getOpportunityTypes(PrismOpportunityCategory opportunityCategory) {
        return byCategory.get(opportunityCategory);
    }

    public static PrismOpportunityType getSystemOpportunityType() {
        return EMPLOYMENT;
    }

    public boolean isPublished() {
        return published;
    }

    public PrismOpportunityCategory getCategory() {
        return category;
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
