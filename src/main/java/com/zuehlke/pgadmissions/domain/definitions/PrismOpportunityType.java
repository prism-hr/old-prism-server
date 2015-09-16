package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.EXPERIENCE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.FUNDING;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.LEARNING;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.STUDY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.WORK;

import java.util.List;

import com.google.common.collect.LinkedListMultimap;

import uk.co.alumeni.prism.api.model.advert.EnumDefinition;

public enum PrismOpportunityType implements EnumDefinition<uk.co.alumeni.prism.enums.PrismOpportunityType>, PrismLocalizableDefinition {

    STUDY_UNDERGRADUATE(STUDY), //
    STUDY_POSTGRADUATE_TAUGHT(STUDY), //
    STUDY_POSTGRADUATE_RESEARCH(STUDY), //
    SCHOLARSHIP_UNDERGRADUATE(FUNDING), //
    SCHOLARSHIP_POSTGRADUATE_TAUGHT(FUNDING), //
    SCHOLARSHIP_POSTGRADUATE_RESEARCH(FUNDING), //
    WORK_EXPERIENCE(EXPERIENCE), //
    VOLUNTEERING(EXPERIENCE), //
    EMPLOYMENT(WORK), //
    EMPLOYMENT_SECONDMENT(WORK), //
    TRAINING(LEARNING); //

    private PrismOpportunityCategory category;

    private static final LinkedListMultimap<PrismOpportunityCategory, PrismOpportunityType> byCategory = LinkedListMultimap.create();

    static {
        for (PrismOpportunityType opportunityType : PrismOpportunityType.values()) {
            byCategory.put(opportunityType.getCategory(), opportunityType);
        }
    }

    private PrismOpportunityType(PrismOpportunityCategory category) {
        this.category = category;
    }

    @Override
    public uk.co.alumeni.prism.enums.PrismOpportunityType getDefinition() {
        return uk.co.alumeni.prism.enums.PrismOpportunityType.valueOf(name());
    }

    public PrismOpportunityCategory getCategory() {
        return category;
    }

    public static List<PrismOpportunityType> getOpportunityTypes(PrismOpportunityCategory opportunityCategory) {
        return byCategory.get(opportunityCategory);
    }

    public static PrismOpportunityType getSystemOpportunityType() {
        return STUDY_POSTGRADUATE_RESEARCH;
    }

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_OPPORTUNITY_TYPE_" + name());
    }

}
