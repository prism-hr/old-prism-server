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

public enum PrismOpportunityType implements EnumDefinition<uk.co.alumeni.prism.enums.PrismOpportunityType>,
    PrismLocalizableDefinition {

    STUDY_UNDERGRADUATE(STUDY, false, false, null), //
    STUDY_POSTGRADUATE_TAUGHT(STUDY, false, false, null), //
    STUDY_POSTGRADUATE_RESEARCH(STUDY, false, false, null), //
    TRAINING(PERSONAL_DEVELOPMENT, false, false, null), //
    WORK_EXPERIENCE(EXPERIENCE, true, false, SYSTEM_OPPORTUNITY_TYPE_PAID_TOC), //
    ON_COURSE_PLACEMENT(EXPERIENCE, true, true, SYSTEM_OPPORTUNITY_TYPE_OPTIONAL_TOC), //
    VOLUNTEERING(EXPERIENCE, true, false, SYSTEM_OPPORTUNITY_TYPE_UNPAID_TOC), //
    EMPLOYMENT(WORK, true, false, SYSTEM_OPPORTUNITY_TYPE_PAID_TOC); //

    private PrismOpportunityCategory opportunityCategory;
    
    private boolean published;
    
    private boolean requireEndorsement;

    private PrismDisplayPropertyDefinition termsAndConditions;

    private static final LinkedListMultimap<PrismOpportunityCategory, PrismOpportunityType> byCategory = LinkedListMultimap.create();

    static {
        for (PrismOpportunityType opportunityType : PrismOpportunityType.values()) {
            byCategory.put(opportunityType.getOpportunityCategory(), opportunityType);
        }
    }

    private PrismOpportunityType(PrismOpportunityCategory category, boolean published, boolean requireEndorsement, PrismDisplayPropertyDefinition termsAndConditions) {
        this.opportunityCategory = category;
        this.published = published;
        this.requireEndorsement = requireEndorsement;
        this.termsAndConditions = termsAndConditions;
    }

    public PrismOpportunityCategory getOpportunityCategory() {
        return opportunityCategory;
    }
    
    public boolean isPublished() {
        return published;
    }

    public boolean isRequireEndorsement() {
        return requireEndorsement;
    }

    public PrismDisplayPropertyDefinition getTermsAndConditions() {
        return termsAndConditions;
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
