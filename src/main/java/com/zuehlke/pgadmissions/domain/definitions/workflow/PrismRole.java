package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocalizableDefinition;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;
import static java.util.Arrays.asList;

public enum PrismRole implements PrismLocalizableDefinition {

    RESUME_CREATOR(APPLICANT, RESUME), //
    RESUME_REVIEWER(RECRUITER, RESUME), //

    APPLICATION_ADMINISTRATOR(RECRUITER, APPLICATION), //
    APPLICATION_CREATOR(APPLICANT, APPLICATION), //
    APPLICATION_INTERVIEWEE(APPLICANT, APPLICATION), //
    APPLICATION_INTERVIEWER(RECRUITER, APPLICATION), //
    APPLICATION_POTENTIAL_INTERVIEWEE(APPLICANT, APPLICATION), //
    APPLICATION_POTENTIAL_INTERVIEWER(RECRUITER, APPLICATION), //
    APPLICATION_PRIMARY_SUPERVISOR(RECRUITER, APPLICATION), //
    APPLICATION_REFEREE(REFEREE, APPLICATION), //
    APPLICATION_REVIEWER(RECRUITER, APPLICATION), //
    APPLICATION_SECONDARY_SUPERVISOR(RECRUITER, APPLICATION), //
    APPLICATION_VIEWER_RECRUITER(RECRUITER, APPLICATION), //
    APPLICATION_VIEWER_REFEREE(REFEREE, APPLICATION), //

    PROJECT_ADMINISTRATOR(ADMINISTRATOR, PROJECT), //
    PROJECT_PRIMARY_SUPERVISOR(RECRUITER, PROJECT), //
    PROJECT_SECONDARY_SUPERVISOR(RECRUITER, PROJECT), //
    PROJECT_APPROVER(RECRUITER, PROJECT), //
    PROJECT_VIEWER(RECRUITER, PROJECT), //

    PROGRAM_ADMINISTRATOR(ADMINISTRATOR, PROGRAM), //
    PROGRAM_APPROVER(RECRUITER, PROGRAM), //
    PROGRAM_VIEWER(RECRUITER, PROGRAM), //

    DEPARTMENT_ADMINISTRATOR(ADMINISTRATOR, DEPARTMENT), //
    DEPARTMENT_APPROVER(RECRUITER, DEPARTMENT), //
    DEPARTMENT_VIEWER(RECRUITER, DEPARTMENT), //
    DEPARTMENT_VIEWER_UNVERIFIED(RECRUITER, INSTITUTION), //

    INSTITUTION_ADMINISTRATOR(ADMINISTRATOR, INSTITUTION), //
    INSTITUTION_APPROVER(RECRUITER, INSTITUTION), //
    INSTITUTION_VIEWER(RECRUITER, INSTITUTION), //
    INSTITUTION_VIEWER_UNVERIFIED(RECRUITER, INSTITUTION), //

    SYSTEM_ADMINISTRATOR(ADMINISTRATOR, SYSTEM);

    private PrismRoleCategory roleCategory;

    private PrismScope scope;

    private PrismRole(PrismRoleCategory roleCategory, PrismScope scope) {
        this.roleCategory = roleCategory;
        this.scope = scope;
    }

    public static PrismRole getAdministratorRole(Resource resource) {
        return valueOf(resource.getResourceScope().name() + "_ADMINISTRATOR");
    }

    public static PrismRole getViewerRole(Resource resource) {
        if (ResourceParent.class.isAssignableFrom(resource.getClass())) {
            return valueOf(resource.getResourceScope().name() + "_VIEWER");
        }
        return null;
    }

    public static PrismRole getUnverifiedViewerRole(Resource resource) {
        return getUnverifiedViewerRole(resource.getResourceScope());
    }

    public static PrismRole getUnverifiedViewerRole(PrismScope resourceScope) {
        if (asList(DEPARTMENT, INSTITUTION).contains(resourceScope)) {
            return valueOf(resourceScope.name() + "_VIEWER_UNVERIFIED");
        }
        return null;
    }

    public PrismRoleCategory getRoleCategory() {
        return roleCategory;
    }

    public PrismScope getScope() {
        return scope;
    }

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_ROLE_" + name());
    }

    public enum PrismRoleCategory {

        ADMINISTRATOR,
        RECRUITER,
        APPLICANT,
        REFEREE

    }

}
