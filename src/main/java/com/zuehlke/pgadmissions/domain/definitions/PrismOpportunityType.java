package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITY_TYPE_OPTIONAL_TOC;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITY_TYPE_PAID_TOC;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_OPPORTUNITY_TYPE_UNPAID_TOC;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.EXPERIENCE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.PERSONAL_DEVELOPMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.STUDY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.WORK;

import java.util.List;

import com.google.common.collect.LinkedListMultimap;

import uk.co.alumeni.prism.api.model.advert.EnumDefinition;

public enum PrismOpportunityType implements EnumDefinition<uk.co.alumeni.prism.enums.PrismOpportunityType>,PrismLocalizableDefinition {

    STUDY_UNDERGRADUATE(false, null, STUDY), //
    STUDY_POSTGRADUATE_TAUGHT(false, null, STUDY), //
    STUDY_POSTGRADUATE_RESEARCH(false, null, STUDY), //
    TRAINING(false, null, PERSONAL_DEVELOPMENT), //
    WORK_EXPERIENCE(true, SYSTEM_OPPORTUNITY_TYPE_PAID_TOC, EXPERIENCE), //
    ON_COURSE_PLACEMENT(true, SYSTEM_OPPORTUNITY_TYPE_OPTIONAL_TOC, EXPERIENCE), //
    VOLUNTEERING(true, SYSTEM_OPPORTUNITY_TYPE_UNPAID_TOC, EXPERIENCE), //
    EMPLOYMENT(true, SYSTEM_OPPORTUNITY_TYPE_PAID_TOC, WORK), //
    EMPLOYMENT_SECONDMENT(true, SYSTEM_OPPORTUNITY_TYPE_PAID_TOC, WORK); //

    private boolean published;

    private PrismDisplayPropertyDefinition termsAndConditions;

    private PrismOpportunityCategory category;

    private static final LinkedListMultimap<PrismOpportunityCategory, PrismOpportunityType> byCategory = LinkedListMultimap.create();

    static {
        for (PrismOpportunityType opportunityType : PrismOpportunityType.values()) {
            byCategory.put(opportunityType.getCategory(), opportunityType);
        }
    }

    private PrismOpportunityType(boolean published, PrismDisplayPropertyDefinition termsAndConditions, PrismOpportunityCategory category) {
        this.published = published;
        this.termsAndConditions = termsAndConditions;
        this.category = category;
    }

    public boolean isPublished() {
        return published;
    }

    public PrismDisplayPropertyDefinition getTermsAndConditions() {
        return termsAndConditions;
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
