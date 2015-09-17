package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.EXPERIENCE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.PROFESSIONAL_DEVELOPMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.STUDY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.WORK;

import java.util.List;

import com.google.common.collect.LinkedListMultimap;

import uk.co.alumeni.prism.api.model.advert.EnumDefinition;

public enum PrismOpportunityType implements EnumDefinition<uk.co.alumeni.prism.enums.PrismOpportunityType>,PrismLocalizableDefinition {

    STUDY_UNDERGRADUATE(false, STUDY), //
    STUDY_POSTGRADUATE_TAUGHT(false, STUDY), //
    STUDY_POSTGRADUATE_RESEARCH(false, STUDY), //
    TRAINING(false, PROFESSIONAL_DEVELOPMENT), //
    WORK_EXPERIENCE(true, EXPERIENCE), //
    VOLUNTEERING(true, EXPERIENCE), //
    EMPLOYMENT(true, WORK), //
    EMPLOYMENT_SECONDMENT(true, WORK); //

    private boolean published;

    private PrismOpportunityCategory category;

    private static final LinkedListMultimap<PrismOpportunityCategory, PrismOpportunityType> byCategory = LinkedListMultimap.create();

    static {
        for (PrismOpportunityType opportunityType : PrismOpportunityType.values()) {
            byCategory.put(opportunityType.getCategory(), opportunityType);
        }
    }

    private PrismOpportunityType(boolean published, PrismOpportunityCategory category) {
        this.published = published;
        this.category = category;
    }

    public boolean isPublished() {
        return published;
    }

    public PrismOpportunityCategory getCategory() {
        return category;
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
