package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory.ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory.APPLICANT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory.RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory.REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocalizableDefinition;

public enum PrismRole implements PrismLocalizableDefinition {

    APPLICATION_ADMINISTRATOR(RECRUITER, false, APPLICATION), //
    APPLICATION_APPOINTEE(APPLICANT, false, APPLICATION), //
    APPLICATION_CREATOR(APPLICANT, false, APPLICATION), //
    APPLICATION_HIRING_MANAGER(RECRUITER, false, APPLICATION), //
    APPLICATION_INTERVIEWEE(APPLICANT, false, APPLICATION), //
    APPLICATION_INTERVIEWER(RECRUITER, false, APPLICATION), //
    APPLICATION_POTENTIAL_INTERVIEWEE(APPLICANT, false, APPLICATION), //
    APPLICATION_POTENTIAL_INTERVIEWER(RECRUITER, false, APPLICATION), //
    APPLICATION_REFEREE(REFEREE, false, APPLICATION), //
    APPLICATION_REVIEWER(RECRUITER, false, APPLICATION), //
    APPLICATION_VIEWER_RECRUITER(RECRUITER, false, APPLICATION), //
    APPLICATION_VIEWER_REFEREE(REFEREE, false, APPLICATION), //

    PROJECT_ADMINISTRATOR(ADMINISTRATOR, true, PROJECT), //
    PROJECT_APPROVER(RECRUITER, true, PROJECT), //
    PROJECT_VIEWER(RECRUITER, true, PROJECT), //

    PROGRAM_ADMINISTRATOR(ADMINISTRATOR, true, PROGRAM), //
    PROGRAM_APPROVER(RECRUITER, true, PROGRAM), //
    PROGRAM_VIEWER(RECRUITER, true, PROGRAM), //

    DEPARTMENT_ADMINISTRATOR(ADMINISTRATOR, true, DEPARTMENT), //
    DEPARTMENT_APPROVER(RECRUITER, true, DEPARTMENT), //
    DEPARTMENT_VIEWER(RECRUITER, true, DEPARTMENT), //
    DEPARTMENT_VIEWER_UNVERIFIED(RECRUITER, false, DEPARTMENT), //
    DEPARTMENT_STUDENT(APPLICANT, true, DEPARTMENT), //
    DEPARTMENT_STUDENT_UNVERIFIED(APPLICANT, false, DEPARTMENT), //

    INSTITUTION_ADMINISTRATOR(ADMINISTRATOR, true, INSTITUTION), //
    INSTITUTION_APPROVER(RECRUITER, true, INSTITUTION), //
    INSTITUTION_VIEWER(RECRUITER, true, INSTITUTION), //
    INSTITUTION_VIEWER_UNVERIFIED(RECRUITER, false, INSTITUTION), //
    INSTITUTION_STUDENT(APPLICANT, true, INSTITUTION), //
    INSTITUTION_STUDENT_UNVERIFIED(APPLICANT, true, INSTITUTION), //

    SYSTEM_ADMINISTRATOR(ADMINISTRATOR, true, SYSTEM);

    private PrismRoleCategory roleCategory;

    private boolean directlyAssignable;

    private PrismScope scope;

    private static HashMultimap<PrismScope, PrismRole> unverifiedRoles = HashMultimap.create();

    static {
        for (PrismRole role : PrismRole.values()) {
            if (role.name().contains("UNVERIFIED")) {
                unverifiedRoles.put(role.getScope(), role);
            }
        }
    }

    private PrismRole(PrismRoleCategory roleCategory, boolean directlyAssignable, PrismScope scope) {
        this.roleCategory = roleCategory;
        this.directlyAssignable = directlyAssignable;
        this.scope = scope;
    }

    public PrismRoleCategory getRoleCategory() {
        return roleCategory;
    }

    public boolean isDirectlyAssignable() {
        return directlyAssignable;
    }

    public PrismScope getScope() {
        return scope;
    }
    
    public static Set<PrismRole> getUnverifiedRoles(PrismScope scope) {
        return unverifiedRoles.get(scope);
    }

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_ROLE_" + name());
    }

    public enum PrismRoleCategory {

        ADMINISTRATOR, //
        RECRUITER, //
        APPLICANT, //
        REFEREE;

    }

}
