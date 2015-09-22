package com.zuehlke.pgadmissions.domain.definitions;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

import java.time.Month;
import java.util.LinkedList;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;

public enum PrismResourceCreationContext {

    QUALIFICATION(new PrismScopeCreationFamilies()
            .withScopeCreationFamily(new PrismScopeCreationFamily()
                    .withScope(INSTITUTION)
                    .withScope(DEPARTMENT)
                    .withScope(PROGRAM))
            .withScopeCreationFamily(new PrismScopeCreationFamily()
                    .withScope(INSTITUTION)
                    .withScope(DEPARTMENT)
                    .withScope(PROGRAM)
                    .withScope(PROJECT)),
            Month.OCTOBER,
            PrismOpportunityCategory.STUDY),
    UNIVERSITY(new PrismScopeCreationFamilies()
            .withScopeCreationFamily(new PrismScopeCreationFamily()
                    .withScope(INSTITUTION)
                    .withScope(DEPARTMENT)),
            Month.OCTOBER,
            PrismOpportunityCategory.STUDY),
    EMPLOYER(new PrismScopeCreationFamilies()
            .withScopeCreationFamily(new PrismScopeCreationFamily()
                    .withScope(INSTITUTION)),
            Month.APRIL,
            PrismOpportunityCategory.EXPERIENCE, PrismOpportunityCategory.WORK);

    private PrismScopeCreationFamilies scopeCreationFamilies;

    private Month defaultBusinessYearStartMonth;

    private PrismOpportunityCategory[] categories;

    PrismResourceCreationContext(PrismScopeCreationFamilies scopeCreations, Month defaultBusinessYearStartMonth, PrismOpportunityCategory... categories) {
        this.scopeCreationFamilies = scopeCreations;
        this.defaultBusinessYearStartMonth = defaultBusinessYearStartMonth;
        this.categories = categories;
    }

    public PrismScopeCreationFamilies getScopeCreationFamilies() {
        return scopeCreationFamilies;
    }

    public Month getDefaultBusinessYearStartMonth() {
        return defaultBusinessYearStartMonth;
    }

    public PrismOpportunityCategory[] getCategories() {
        return categories;
    }

    public static class PrismScopeCreationFamilies extends LinkedList<PrismScopeCreationFamily> {

        private static final long serialVersionUID = 7042394502685379016L;

        public PrismScopeCreationFamilies withScopeCreationFamily(PrismScopeCreationFamily scopeCreationFamily) {
            super.add(scopeCreationFamily);
            return this;
        }

    }

    public static class PrismScopeCreationFamily extends LinkedList<PrismScope> {

        private static final long serialVersionUID = -6696268838881348249L;

        public PrismScopeCreationFamily withScope(PrismScope scopeCreation) {
            super.add(scopeCreation);
            return this;
        }

    }

}
