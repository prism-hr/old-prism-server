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
    
    APPLICATION_ADMINISTRATOR(RECRUITER, APPLICATION), //
    APPLICATION_CREATOR(APPLICANT, APPLICATION), //
    APPLICATION_HIRING_MANAGER(RECRUITER, APPLICATION), //
    APPLICATION_INTERVIEWEE(APPLICANT, APPLICATION), //
    APPLICATION_INTERVIEWER(RECRUITER, APPLICATION), //
    APPLICATION_POTENTIAL_INTERVIEWEE(APPLICANT, APPLICATION), //
    APPLICATION_POTENTIAL_INTERVIEWER(RECRUITER, APPLICATION), //
    APPLICATION_REFEREE(REFEREE, APPLICATION), //
    APPLICATION_REVIEWER(RECRUITER, APPLICATION), //
    APPLICATION_VIEWER_RECRUITER(RECRUITER, APPLICATION), //
    APPLICATION_VIEWER_REFEREE(REFEREE, APPLICATION), //

    PROJECT_ADMINISTRATOR(ADMINISTRATOR, PROJECT), //
    PROJECT_APPROVER(RECRUITER, PROJECT), //
    PROJECT_VIEWER(RECRUITER, PROJECT), //

    PROGRAM_ADMINISTRATOR(ADMINISTRATOR, PROGRAM), //
    PROGRAM_APPROVER(RECRUITER, PROGRAM), //
    PROGRAM_VIEWER(RECRUITER, PROGRAM), //

    DEPARTMENT_ADMINISTRATOR(ADMINISTRATOR, DEPARTMENT), //
    DEPARTMENT_APPROVER(RECRUITER, DEPARTMENT), //
    DEPARTMENT_VIEWER(RECRUITER, DEPARTMENT), //
    DEPARTMENT_VIEWER_UNVERIFIED(RECRUITER, DEPARTMENT), //
    DEPARTMENT_STUDENT(STUDENT, DEPARTMENT), //
    DEPARTMENT_STUDENT_UNVERFIED(STUDENT, DEPARTMENT), //
    DEPARTMENT_EMPLOYEE(EMPLOYEE, DEPARTMENT), //
    DEPARTMENT_EMPLOYEE_UNVERIFIED(EMPLOYEE, DEPARTMENT), //

    INSTITUTION_ADMINISTRATOR(ADMINISTRATOR, INSTITUTION), //
    INSTITUTION_APPROVER(RECRUITER, INSTITUTION), //
    INSTITUTION_VIEWER(RECRUITER, INSTITUTION), //
    INSTITUTION_VIEWER_UNVERIFIED(RECRUITER, INSTITUTION), //
    INSTITUTION_STUDENT(STUDENT, INSTITUTION), //
    INSTITUTION_STUDENT_UNVERFIED(STUDENT, INSTITUTION), //
    INSTIUTTION_EMPLOYEE(EMPLOYEE, INSTITUTION), //
    INSTITUTION_EMPLOYEE_UNVERIFIED(EMPLOYEE, INSTITUTION), //

    SYSTEM_ADMINISTRATOR(ADMINISTRATOR, SYSTEM);

    private PrismRoleCategory roleCategory;

    private PrismScope scope;

    private PrismRole(PrismRoleCategory roleCategory, PrismScope scope) {
        this.roleCategory = roleCategory;
        this.scope = scope;
    }

    public PrismRoleCategory getRoleCategory() {
        return roleCategory;
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
