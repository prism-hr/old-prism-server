package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.Arrays;
import java.util.List;

public enum PrismActionEnhancement {

    APPLICATION_VIEW_EDIT_AS_CREATOR(PrismScope.APPLICATION), //
    APPLICATION_VIEW_EDIT_AS_ADMITTER(PrismScope.APPLICATION), //
    APPLICATION_VIEW_AS_CREATOR(PrismScope.APPLICATION), //
    APPLICATION_VIEW_AS_RECRUITER(PrismScope.APPLICATION), //
    APPLICATION_VIEW_AS_ADMITTER(PrismScope.APPLICATION), //
    APPLICATION_VIEW_AS_REFEREE(PrismScope.APPLICATION), //
    PROJECT_VIEW_EDIT_AS_USER(PrismScope.PROJECT), //
    PROJECT_VIEW_AS_USER(PrismScope.PROJECT),
    PROGRAM_VIEW_EDIT_AS_USER(PrismScope.PROGRAM), //
    PROGRAM_VIEW_AS_USER(PrismScope.PROGRAM), //
    INSTITUTION_VIEW_EDIT_AS_USER(PrismScope.INSTITUTION), //
    INSTITUTION_VIEW_AS_USER(PrismScope.INSTITUTION), //
    SYSTEM_VIEW_EDIT_AS_USER(PrismScope.SYSTEM);

    private PrismScope scope;

    private PrismActionEnhancement(PrismScope scope) {
        this.scope = scope;
    }

    public PrismScope getScope() {
        return scope;
    }

    public enum PrismActionEnhancementGroup {

        APPLICATION_EQUAL_OPPORTUNITIES_VIEWER(Arrays.asList(APPLICATION_VIEW_AS_CREATOR, APPLICATION_VIEW_AS_ADMITTER, APPLICATION_VIEW_EDIT_AS_CREATOR,
                APPLICATION_VIEW_EDIT_AS_ADMITTER));

        private List<PrismActionEnhancement> actionEnhancements;

        private PrismActionEnhancementGroup(List<PrismActionEnhancement> actionEnhancements) {
            this.actionEnhancements = actionEnhancements;
        }

        public List<PrismActionEnhancement> getActionEnhancements() {
            return actionEnhancements;
        }

    }

}
