package uk.co.alumeni.prism.domain.definitions.workflow;

import com.google.common.collect.HashMultimap;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismLocalizableDefinition;

import java.util.Set;

import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.apache.commons.lang3.ObjectUtils.compare;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.*;

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
    PROJECT_ENQUIRER(APPLICANT, false, PROJECT), //
    PROJECT_VIEWER(RECRUITER, true, PROJECT), //

    PROGRAM_ADMINISTRATOR(ADMINISTRATOR, true, PROGRAM), //
    PROGRAM_APPROVER(RECRUITER, true, PROGRAM), //
    PROGRAM_ENQUIRER(APPLICANT, false, PROGRAM), //
    PROGRAM_VIEWER(RECRUITER, true, PROGRAM), //

    DEPARTMENT_ADMINISTRATOR(ADMINISTRATOR, true, DEPARTMENT), //
    DEPARTMENT_APPROVER(RECRUITER, true, DEPARTMENT), //
    DEPARTMENT_ENQUIRER(APPLICANT, false, DEPARTMENT), //
    DEPARTMENT_VIEWER(RECRUITER, true, DEPARTMENT), //
    DEPARTMENT_VIEWER_UNVERIFIED(RECRUITER, false, DEPARTMENT), //
    DEPARTMENT_VIEWER_REJECTED(RECRUITER, false, DEPARTMENT), //
    DEPARTMENT_STUDENT(STUDENT, true, DEPARTMENT), //
    DEPARTMENT_STUDENT_UNVERIFIED(STUDENT, false, DEPARTMENT), //
    DEPARTMENT_STUDENT_REJECTED(STUDENT, false, DEPARTMENT), //

    INSTITUTION_ADMINISTRATOR(ADMINISTRATOR, true, INSTITUTION), //
    INSTITUTION_APPROVER(RECRUITER, true, INSTITUTION), //
    INSTITUTION_ENQUIRER(APPLICANT, false, INSTITUTION), //
    INSTITUTION_VIEWER(RECRUITER, true, INSTITUTION), //
    INSTITUTION_VIEWER_UNVERIFIED(RECRUITER, false, INSTITUTION), //
    INSTITUTION_VIEWER_REJECTED(RECRUITER, false, INSTITUTION), //
    INSTITUTION_STUDENT(STUDENT, true, INSTITUTION), //
    INSTITUTION_STUDENT_UNVERIFIED(STUDENT, false, INSTITUTION), //
    INSTITUTION_STUDENT_REJECTED(STUDENT, false, INSTITUTION), //

    SYSTEM_ADMINISTRATOR(ADMINISTRATOR, true, SYSTEM), //
    SYSTEM_CANDIDATE(STUDENT, false, SYSTEM);

    private PrismRoleCategory roleCategory;

    private boolean directlyAssignable;

    private PrismScope scope;

    private static Set<PrismRole> verifiedRoles = newLinkedHashSet();

    private static HashMultimap<PrismRole, PrismScope> visibleScopes = HashMultimap.create();

    static {
        for (PrismRole role : values()) {
            String roleName = role.name();
            if (!(roleName.endsWith("_UNVERIFIED") || roleName.endsWith("_REJECTED"))) {
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

    PrismRole(PrismRoleCategory roleCategory, boolean directlyAssignable, PrismScope scope) {
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

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_ROLE_" + name());
    }

    public int compareWith(PrismRole other) {
        int compare = compare(getScope().ordinal(), other.getScope().ordinal());
        return compare == 0 ? compare(name(), other.name()) : compare;
    }

    public enum PrismRoleCategory {

        ADMINISTRATOR, //
        RECRUITER, //
        STUDENT, //
        APPLICANT, //
        REFEREE

    }

}
