package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_REVOKED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_STUDENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import java.util.Collection;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;

@Component
public class WorkflowDAO {

    @Inject
    private SessionFactory sessionFactory;

    public static PrismScope[] targetScopes = new PrismScope[] { DEPARTMENT, INSTITUTION };

    public static PrismScope[] advertScopes = new PrismScope[] { PROJECT, PROGRAM, DEPARTMENT, INSTITUTION };

    public Criteria getWorklflowCriteria(PrismScope resourceScope, Projection projection) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(projection) //
                .createAlias(resourceScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.advert", "advert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.targets", "target", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("target.targetAdvert", "targetAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("targetAdvert.department", "targetDepartment", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.eqProperty("targetAdvert.id", "targetDepartment.advert.id")) //
                .createAlias("targetAdvert.institution", "targetInstitution", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.eqProperty("targetAdvert.id", "targetInstitution.advert.id"));

        for (PrismScope targeterScope : targetScopes) {
            String targeterScopeLower = targeterScope.getLowerCamelName();
            String targeterScopeUpper = targeterScope.getUpperCamelName();

            String targeterResource = "advert" + targeterScopeUpper;
            String targeterResourceAdvert = targeterResource + "Advert";
            String targeterResourceTarget = targeterResource + "Target";
            String targeterAdvert = "target" + targeterScopeUpper + "Advert";
            criteria.createAlias("advert." + targeterScopeLower, targeterResource, JoinType.LEFT_OUTER_JOIN) //
                    .createAlias(targeterResource + ".advert", targeterResourceAdvert, JoinType.LEFT_OUTER_JOIN) //
                    .createAlias(targeterResourceAdvert + ".targets", targeterResourceTarget, JoinType.LEFT_OUTER_JOIN) //
                    .createAlias(targeterResourceTarget + ".targetAdvert", targeterAdvert, JoinType.LEFT_OUTER_JOIN);

            for (PrismScope targetScope : targetScopes) {
                String targetResource = targeterScopeLower + "Target" + targetScope.getUpperCamelName();
                criteria.createAlias(targeterAdvert + "." + targetScope.getLowerCamelName(), targetResource, JoinType.LEFT_OUTER_JOIN,
                        Restrictions.eqProperty(targeterAdvert + ".id", targetResource + ".advert.id"));
            }
        }

        return criteria.createAlias("resource.user", "owner", JoinType.INNER_JOIN) //
                .createAlias("owner.userRoles", "ownerRole", JoinType.LEFT_OUTER_JOIN,
                        getEndorsementActionJoinConstraint()) //
                .createAlias("ownerRole.department", "ownerDepartment", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("action.systemInvocationOnly", false));
    }

    public Criteria getWorkflowCriteriaList(PrismScope resourceScope, Projection projection) {
        return sessionFactory.getCurrentSession().createCriteria(ResourceState.class)
                .setProjection(projection) //
                .createAlias(resourceScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.externalMode", false)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN,
                        Restrictions.isNull("stateAction.actionCondition")) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .add(Restrictions.eqProperty("state", "stateAction.state")) //
                .add(Restrictions.isNull("state.hidden")) //
                .add(Restrictions.eq("action.systemInvocationOnly", false));
    }

    public Criteria getWorkflowCriteriaList(PrismScope resourceScope, PrismScope parentScope, Projection projection) {
        return sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(projection) //
                .createAlias(resourceScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource." + parentScope.getLowerCamelName(), "parentResource", JoinType.INNER_JOIN) //
                .createAlias("parentResource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.externalMode", false)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN,
                        Restrictions.isNull("stateAction.actionCondition")) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .add(Restrictions.eqProperty("state", "stateAction.state")) //
                .add(Restrictions.isNull("state.hidden")) //
                .add(Restrictions.eq("action.systemInvocationOnly", false));
    }

    public Criteria getWorkflowCriteriaList(PrismScope resourceScope, PrismScope targeterScope, PrismScope targetScope, Projection projection) {
        return sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(projection) //
                .createAlias(resourceScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("resource." + targeterScope.getLowerCamelName(), "targeterResource", JoinType.INNER_JOIN) //
                .createAlias("targeterResource.advert", "targeterAdvert", JoinType.INNER_JOIN) //
                .createAlias("targeterAdvert.targets", "target", JoinType.INNER_JOIN) //
                .createAlias("target.targetAdvert", "targetAdvert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert." + targetScope.getLowerCamelName(), "targetResource", JoinType.INNER_JOIN,
                        Restrictions.eqProperty("targetAdvert.id", "targetResource.advert.id")) //
                .createAlias("targetResource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.externalMode", true)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN,
                        Restrictions.isNull("stateAction.actionCondition")) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .createAlias("resource.user", "owner", JoinType.INNER_JOIN) //
                .createAlias("owner.userRoles", "ownerRole", JoinType.LEFT_OUTER_JOIN,
                        getEndorsementActionJoinConstraint()) //
                .createAlias("ownerRole.department", "ownerDepartment", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("ownerDepartment.advert", "ownerAdvert", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eqProperty("state", "stateAction.state")) //
                .add(Restrictions.disjunction()//
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.disjunction() //
                                        .add(Restrictions.eqProperty("ownerAdvert.department", "targetAdvert.department"))
                                        .add(Restrictions.conjunction() //
                                                .add(Restrictions.eq("role.scope.id", INSTITUTION)) //
                                                .add(Restrictions.eqProperty("ownerAdvert.institution", "targetAdvert.institution")))) //
                                .add(Restrictions.disjunction() //
                                        .add(Restrictions.eq("resource.shared", true))))
                        .add(Restrictions.eq("scope.defaultShared", true))) //
                .add(Restrictions.isNull("state.hidden")) //
                .add(Restrictions.eq("action.systemInvocationOnly", false));
    }

    public static Junction getUserRoleConstraint(PrismScope resourceScope, Collection<PrismScope> parentScopes) {
        String resourceReference = resourceScope.getLowerCamelName();
        Junction userRoleConstraint = Restrictions.disjunction() //
                .add(Restrictions.eqProperty("resource.id", "userRole." + resourceReference + ".id"));
        parentScopes.forEach(parentScope -> {
            String parentReference = parentScope.getLowerCamelName();
            userRoleConstraint.add(Restrictions.eqProperty("resource." + parentReference + ".id", "userRole." + parentReference + ".id"));
        });
        return userRoleConstraint;
    }

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
                .add(getTargetUserRoleConstraint());
    }

    public static Junction getUserRoleWithPartnerConstraint(Resource resource, User user) {
        return Restrictions.conjunction() //
                .add(getUserRoleWithPartnerConstraint(resource)) //
                .add(getUserEnabledConstraint(user));
    }

    public static Junction getTargetUserRoleConstraint() {
        return Restrictions.conjunction() //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eqProperty("targetDepartment.id", "userRole.department.id")) //
                        .add(Restrictions.eqProperty("targetInstitution.id", "userRole.institution.id")) //
                        .add(Restrictions.eqProperty("departmentTargetDepartment.id", "userRole.department.id")) //
                        .add(Restrictions.eqProperty("departmentTargetInstitution.id", "userRole.institution.id")) //
                        .add(Restrictions.eqProperty("institutionTargetDepartment.id", "userRole.department.id")) //
                        .add(Restrictions.eqProperty("institutionTargetInstitution.id", "userRole.institution.id"))) //
                .add(Restrictions.eq("stateActionAssignment.externalMode", true));
    }

    public static Junction getUserEnabledConstraint(User user) {
        return Restrictions.conjunction() //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.eq("userAccount.enabled", true));
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

    public static Junction getEndorsementActionFilterConstraint() {
        return Restrictions.disjunction() //
                .add(Restrictions.isNull("action.partnershipState")) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.conjunction() //
                                        .add(Restrictions.isNull("target.id")) //
                                        .add(Restrictions.eq("action.partnershipTransitionState", ENDORSEMENT_REVOKED))) //
                                .add(Restrictions.eqProperty("action.partnershipState", "target.partnershipState"))) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.isNull("advertDepartmentTarget.targetAdvertUser")) //
                                .add(Restrictions.eqProperty("advertDepartmentTarget.targetAdvertUser", "userRole.user")) //
                                .add(Restrictions.isNull("advertInstitutionTarget.targetAdvertUser")) //
                                .add(Restrictions.eqProperty("advertInstitutionTarget.targetAdvertUser", "userRole.user")))
                        .add(getEndorsementActionVisibilityConstraint()));
    }

    public static Junction getEndorsementActionVisibilityConstraint() {
        return Restrictions.disjunction() //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.eqProperty("ownerDepartment.id", "departmentTargetDepartment.id"))
                                .add(Restrictions.conjunction() //
                                        .add(Restrictions.eq("role.scope.id", INSTITUTION))
                                        .add(Restrictions.eqProperty("ownerDepartment.institution.id", "departmentTargetInstitution.id")))
                                .add(Restrictions.eqProperty("ownerDepartment.id", "institutionTargetDepartment.id"))
                                .add(Restrictions.conjunction() //
                                        .add(Restrictions.eq("role.scope.id", INSTITUTION))
                                        .add(Restrictions.eqProperty("ownerDepartment.institution.id", "institutionTargetInstitution.id"))))
                        .add(Restrictions.eq("resource.shared", true)))
                .add(Restrictions.eq("scope.defaultShared", true));
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

    public static Junction getResourceRecentlyActiveConstraint(DateTime baseline) {
        return Restrictions.disjunction() //
                .add(Restrictions.eq("stateAction.raisesUrgentFlag", true)) //
                .add(Restrictions.gt("resource.updatedTimestamp", baseline));
    }

    public static Junction getUserDueNotificationConstraint(LocalDate baseline) {
        return Restrictions.disjunction() //
                .add(Restrictions.isNull("userNotification.id")) //
                .add(Restrictions.lt("userNotification.lastNotifiedDate", baseline));
    }

}
