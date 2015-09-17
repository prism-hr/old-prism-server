package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory.ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory.APPLICANT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory.EMPLOYEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory.RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory.REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory.STUDENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static java.util.Arrays.asList;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocalizableDefinition;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;

public enum PrismRole implements PrismLocalizableDefinition {

    APPLICATION_ADMINISTRATOR(RECRUITER, APPLICATION, false), //
    APPLICATION_CREATOR(APPLICANT, APPLICATION, false), //
    APPLICATION_HIRING_MANAGER(RECRUITER, APPLICATION, false), //
    APPLICATION_INTERVIEWEE(APPLICANT, APPLICATION, false), //
    APPLICATION_INTERVIEWER(RECRUITER, APPLICATION, false), //
    APPLICATION_POTENTIAL_INTERVIEWEE(APPLICANT, APPLICATION, false), //
    APPLICATION_POTENTIAL_INTERVIEWER(RECRUITER, APPLICATION, false), //
    APPLICATION_REFEREE(REFEREE, APPLICATION, false), //
    APPLICATION_REVIEWER(RECRUITER, APPLICATION, false), //
    APPLICATION_VIEWER_RECRUITER(RECRUITER, APPLICATION, false), //
    APPLICATION_VIEWER_REFEREE(REFEREE, APPLICATION, false), //

    PROJECT_ADMINISTRATOR(ADMINISTRATOR, PROJECT, true), //
    PROJECT_APPROVER(RECRUITER, PROJECT, true), //
    PROJECT_VIEWER(RECRUITER, PROJECT, true), //

    PROGRAM_ADMINISTRATOR(ADMINISTRATOR, PROGRAM, true), //
    PROGRAM_APPROVER(RECRUITER, PROGRAM, true), //
    PROGRAM_VIEWER(RECRUITER, PROGRAM, true), //

    DEPARTMENT_ADMINISTRATOR(ADMINISTRATOR, DEPARTMENT, true), //
    DEPARTMENT_APPROVER(RECRUITER, DEPARTMENT, true), //
    DEPARTMENT_VIEWER(RECRUITER, DEPARTMENT, true), //
    DEPARTMENT_VIEWER_UNVERIFIED(RECRUITER, DEPARTMENT, false), //
    DEPARTMENT_STUDENT(STUDENT, DEPARTMENT, true), //
    DEPARTMENT_STUDENT_UNVERIFIED(STUDENT, DEPARTMENT, false), //
    DEPARTMENT_EMPLOYEE(EMPLOYEE, DEPARTMENT, true), //
    DEPARTMENT_EMPLOYEE_UNVERIFIED(EMPLOYEE, DEPARTMENT, false), //

    INSTITUTION_ADMINISTRATOR(ADMINISTRATOR, INSTITUTION, true), //
    INSTITUTION_APPROVER(RECRUITER, INSTITUTION, true), //
    INSTITUTION_VIEWER(RECRUITER, INSTITUTION, true), //
    INSTITUTION_VIEWER_UNVERIFIED(RECRUITER, INSTITUTION, false), //
    INSTITUTION_STUDENT(STUDENT, INSTITUTION, true), //
    INSTITUTION_STUDENT_UNVERIFIED(STUDENT, INSTITUTION, false), //
    INSTITUTION_EMPLOYEE(EMPLOYEE, INSTITUTION, true), //
    INSTITUTION_EMPLOYEE_UNVERIFIED(EMPLOYEE, INSTITUTION, false), //

    SYSTEM_ADMINISTRATOR(ADMINISTRATOR, SYSTEM, true);

    private PrismRoleCategory roleCategory;

    private boolean directlyAssignable;

    private PrismScope scope;

    private PrismRole(PrismRoleCategory roleCategory, PrismScope scope, boolean directlyAssignable) {
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

    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_ROLE_" + name());
    }

    public enum PrismRoleCategory {

        ADMINISTRATOR, //
        EMPLOYEE, //
        RECRUITER, //
        APPLICANT, //
        REFEREE, //
        STUDENT;

    }

}
