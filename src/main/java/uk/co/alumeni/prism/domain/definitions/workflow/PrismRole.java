package uk.co.alumeni.prism.domain.definitions.workflow;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.APPLICANT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.RECRUITER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.REFEREE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.STUDENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROJECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;

import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismLocalizableDefinition;

public enum PrismRole implements PrismLocalizableDefinition {

    APPLICATION_APPOINTEE(APPLICANT, false, APPLICATION), //
    APPLICATION_CREATOR(APPLICANT, false, APPLICATION), //
    APPLICATION_HIRING_MANAGER(RECRUITER, false, APPLICATION), //
    APPLICATION_INTERVIEWEE(APPLICANT, false, APPLICATION), //
    APPLICATION_INTERVIEWER(RECRUITER, false, APPLICATION), //
    APPLICATION_POTENTIAL_INTERVIEWEE(APPLICANT, false, APPLICATION), //
    APPLICATION_POTENTIAL_INTERVIEWER(RECRUITER, false, APPLICATION), //
    APPLICATION_REFEREE(REFEREE, false, APPLICATION), //
    APPLICATION_REVIEWER(RECRUITER, false, APPLICATION), //
    APPLICATION_SCHEDULED_INTERVIEWEE(APPLICANT, false, APPLICATION), //
    APPLICATION_SCHEDULED_INTERVIEWER(RECRUITER, false, APPLICATION), //
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
    DEPARTMENT_VIEWER_REJECTED(RECRUITER, false, DEPARTMENT), //
    DEPARTMENT_STUDENT(STUDENT, true, DEPARTMENT), //
    DEPARTMENT_STUDENT_UNVERIFIED(STUDENT, false, DEPARTMENT), //
    DEPARTMENT_STUDENT_REJECTED(STUDENT, false, DEPARTMENT), //

    INSTITUTION_ADMINISTRATOR(ADMINISTRATOR, true, INSTITUTION), //
    INSTITUTION_APPROVER(RECRUITER, true, INSTITUTION), //
    INSTITUTION_VIEWER(RECRUITER, true, INSTITUTION), //
    INSTITUTION_VIEWER_UNVERIFIED(RECRUITER, false, INSTITUTION), //
    INSTITUTION_VIEWER_REJECTED(RECRUITER, false, INSTITUTION), //
    INSTITUTION_STUDENT(STUDENT, true, INSTITUTION), //
    INSTITUTION_STUDENT_UNVERIFIED(STUDENT, false, INSTITUTION), //
    INSTITUTION_STUDENT_REJECTED(STUDENT, false, INSTITUTION), //

    SYSTEM_ADMINISTRATOR(ADMINISTRATOR, true, SYSTEM);

    private PrismRoleCategory roleCategory;

    private boolean directlyAssignable;

    private PrismScope scope;

    private static Set<PrismRole> verifiedRoles = Sets.newHashSet();

    private static HashMultimap<PrismRole, PrismScope> visibleScopes = HashMultimap.create();

    static {
        for (PrismRole role : values()) {
            if (!role.name().endsWith("_UNVERIFIED")) {
                verifiedRoles.add(role);

                PrismScope roleScope = role.getScope();
                visibleScopes.put(role, roleScope);

                if (roleScope.ordinal() <= PROJECT.ordinal()) {
                    visibleScopes.put(role, APPLICATION);
                }

                if (roleScope.ordinal() <= DEPARTMENT.ordinal()) {
                    visibleScopes.put(role, PROJECT);
                }

                if (roleScope.ordinal() <= INSTITUTION.ordinal()) {
                    visibleScopes.put(role, DEPARTMENT);
                }

                if (roleScope.ordinal() <= SYSTEM.ordinal()) {
                    visibleScopes.put(role, INSTITUTION);
                }
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

    public Set<PrismRole> getVerifiedRoles() {
        return verifiedRoles;
    }

    public Set<PrismScope> getVisibleScopes() {
        return visibleScopes.get(this);
    }

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_ROLE_" + name());
    }

    public enum PrismRoleCategory {

        ADMINISTRATOR, //
        RECRUITER, //
        STUDENT, //
        APPLICANT, //
        REFEREE;

    }

}
