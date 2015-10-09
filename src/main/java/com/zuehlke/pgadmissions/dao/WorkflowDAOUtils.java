package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_STUDENT;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;

public class WorkflowDAOUtils {

    public static Junction getUserRoleConstraint(Resource resource) {
        return Restrictions.disjunction() //
                .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                .add(Restrictions.eq("userRole.project", resource.getProject())) //
                .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                .add(Restrictions.eq("userRole.department", resource.getDepartment())) //
                .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                .add(Restrictions.eq("userRole.system", resource.getSystem()));
    }

    public static Junction getUserRoleWithPartnerConstraint(Resource resource) {
        return Restrictions.disjunction() //
                .add(Restrictions.conjunction() //
                        .add(getUserRoleConstraint(resource)) //
                        .add(Restrictions.eq("stateActionAssignment.externalMode", false))) //
                .add(getTargetUserRoleConstraint()) //
                .add(getResourceStateActionConstraint());
    }

    public static Junction getUserRoleWithPartnerConstraint(Resource resource, User user) {
        return Restrictions.conjunction() //
                .add(getUserRoleWithPartnerConstraint(resource)) //
                .add(getUserEnabledConstraint(user));
    }

    public static Junction getTargetUserRoleConstraintNew() {
        return Restrictions.conjunction() //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eqProperty("departmentTargetDepartment.id", "userRole.department.id")) //
                        .add(Restrictions.eqProperty("departmentTargetInstitution.id", "userRole.institution.id")) //
                        .add(Restrictions.eqProperty("institutionTargetDepartment.id", "userRole.department.id")) //
                        .add(Restrictions.eqProperty("institutionTargetInstitution.id", "userRole.institution.id"))) //
                .add(Restrictions.eq("stateActionAssignment.externalMode", true));
    }

    public static Junction getTargetUserRoleConstraint() {
        return Restrictions.conjunction() //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eqProperty("targetAdvert.department", "userRole.department"))
                        .add(Restrictions.eqProperty("targetAdvert.institution", "userRole.institution"))
                        .add(Restrictions.eqProperty("targetAdvert.system", "userRole.system")))
                .add(Restrictions.eq("stateActionAssignment.externalMode", true));
    }

    public static Junction getUserEnabledConstraint(User user) {
        return Restrictions.conjunction() //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eq("userAccount.enabled", true));
    }

    public static Junction getResourceStateActionConstraint() {
        return Restrictions.disjunction() //
                .add(Restrictions.isNull("stateAction.actionCondition")) //
                .add(Restrictions.eqProperty("resourceCondition.actionCondition", "stateAction.actionCondition"));
    }

    public static Junction getSimilarUserConstraint(String searchTerm) {
        return getSimilarUserConstraint(null, searchTerm);
    }

    public static Junction getSimilarUserConstraint(String alias, String searchTerm) {
        alias = StringUtils.isEmpty(alias) ? "" : alias + ".";
        return Restrictions.disjunction() //
                .add(Restrictions.like(alias + "firstName", searchTerm, MatchMode.START)) //
                .add(Restrictions.like(alias + "lastName", searchTerm, MatchMode.START)) //
                .add(Restrictions.like(alias + "fullName", searchTerm, MatchMode.START)) //
                .add(Restrictions.like(alias + "email", searchTerm, MatchMode.START));
    }

    public static Criterion getEndorsementActionJoinConstraint() {
        return Restrictions.eq("ownerRole.role.id", DEPARTMENT_STUDENT);
    }

    public static Junction getEndorsementActionFilterConstraintNew() {
        return Restrictions.disjunction() //
                .add(Restrictions.isNull("action.partnershipState")) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.isNull("resourceTarget.partnershipState"))
                                .add(Restrictions.eqProperty("action.partnershipState", "resourceTarget.partnershipState"))) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.isNull("advertDepartmentTarget.targetAdvertUser")) //
                                .add(Restrictions.eqProperty("advertDepartmentTarget.targetAdvertUser", "userRole.user")) //
                                .add(Restrictions.isNull("advertInstitutionTarget.targetAdvertUser")) //
                                .add(Restrictions.eqProperty("advertInstitutionTarget.targetAdvertUser", "userRole.user")))
                        .add(getEndorsementActionVisibilityConstraintNew()));
    }

    public static Junction getEndorsementActionFilterConstraint() {
        return Restrictions.disjunction() //
                .add(Restrictions.isNull("action.partnershipState")) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eqProperty("action.partnershipState", "target.partnershipState")) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.isNull("target.targetAdvertUser")) //
                                .add(Restrictions.eqProperty("target.targetAdvertUser", "userRole.user")))
                        .add(getEndorsementActionVisibilityConstraint()));
    }

    public static Junction getEndorsementActionVisibilityConstraintNew() {
        return Restrictions.disjunction() //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("scope.defaultShared", true)) //
                        .add(Restrictions.eq("resource.shared", true))) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.eqProperty("ownerDepartment.id", "departmentTargetDepartment.id"))
                                .add(Restrictions.eqProperty("ownerDepartment.institution.id", "departmentTargetInstitution.id"))
                                .add(Restrictions.eqProperty("ownerDepartment.id", "institutionTargetDepartment.id"))
                                .add(Restrictions.eqProperty("ownerDepartment.institution.id", "institutionTargetInstitution.id")))
                        .add(Restrictions.eq("resource.shared", true)));
    }

    public static Junction getEndorsementActionVisibilityConstraint() {
        return Restrictions.disjunction() //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.eq("scope.defaultShared", true)) //
                        .add(Restrictions.eq("resource.shared", true))) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.eqProperty("ownerDepartment.id", "targetAdvert.department.id"))
                                .add(Restrictions.eqProperty("ownerDepartment.institution.id", "targetAdvert.institution.id")))
                        .add(Restrictions.eq("resource.shared", true)));
    }

    public static Junction getOpportunityCategoryConstraint(PrismOpportunityCategory opportunityCategory) {
        String opportunityCategoryName = opportunityCategory.name();
        return Restrictions.disjunction() //
                .add(Restrictions.eq("opportunityCategories", opportunityCategoryName))
                .add(Restrictions.like("opportunityCategories", opportunityCategoryName + "|", MatchMode.START))
                .add(Restrictions.like("opportunityCategories", "|" + opportunityCategoryName + "|", MatchMode.ANYWHERE))
                .add(Restrictions.like("opportunityCategories", "|" + opportunityCategoryName, MatchMode.END));
    }

    public static Junction getResourceParentManageableConstraint(PrismScope resourceScope) {
        String resourceReferenceUpper = resourceScope.name();
        return getResourceParentManageableStateConstraint(resourceReferenceUpper)
                .add(Restrictions.eq("userRole.role.id", PrismRole.valueOf(resourceReferenceUpper + "_ADMINISTRATOR")));
    }

    public static Junction getResourceParentManageableStateConstraint(String resourceReferenceUpper) {
        return Restrictions.conjunction() //
                .add(Restrictions.ne("state.id", PrismState.valueOf(resourceReferenceUpper + "_UNSUBMITTED")))
                .add(Restrictions.ne("state.id", PrismState.valueOf(resourceReferenceUpper + "_DISABLED_COMPLETED")));
    }

    public static Junction getResourceParentManageableConstraint(PrismScope resourceScope, User user) {
        return getResourceParentManageableConstraint(resourceScope)
                .add(Restrictions.eq("userRole.user", user));
    }

}
