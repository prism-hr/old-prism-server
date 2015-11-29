package uk.co.alumeni.prism.dao;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState.ENDORSEMENT_REVOKED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.RECRUITER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROJECT;

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
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.resource.ResourceState;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.utils.PrismEnumUtils;

@Component
public class WorkflowDAO {

    @Inject
    private SessionFactory sessionFactory;

    public static PrismScope[] targetScopes = new PrismScope[] { DEPARTMENT, INSTITUTION };

    public static PrismScope[] advertScopes = new PrismScope[] { PROJECT, PROGRAM, DEPARTMENT, INSTITUTION };

    public Criteria getWorkflowCriteriaList(PrismScope scope, Projection projection) {
        return getWorkflowCriteriaListResource(scope, projection)
                .createAlias("resource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.externalMode", false)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN,
                        Restrictions.isNull("stateAction.actionCondition")) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.eqProperty("state", "stateAction.state")) //
                .add(Restrictions.isNull("state.hidden")) //
                .add(Restrictions.eq("action.systemInvocationOnly", false));
    }

    public Criteria getWorkflowCriteriaList(PrismScope scope, PrismScope parentScope, Projection projection) {
        return getWorkflowCriteriaListResource(scope, projection)
                .createAlias("resource." + parentScope.getLowerCamelName(), "parentResource", JoinType.INNER_JOIN) //
                .createAlias("parentResource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.externalMode", false)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN,
                        Restrictions.isNull("stateAction.actionCondition")) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.eqProperty("state", "stateAction.state")) //
                .add(Restrictions.isNull("state.hidden")) //
                .add(Restrictions.eq("action.systemInvocationOnly", false));
    }

    public Criteria getWorkflowCriteriaList(PrismScope scope, PrismScope targeterScope, PrismScope targetScope,
            Collection<Integer> targeterEntities, Projection projection) {
        return getWorkflowCriteriaListResource(scope, projection)
                .createAlias("resource.advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.targets", "target", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert." + targeterScope.getLowerCamelName(), "targeterResource", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("targeterResource.advert", "targeterAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("targeterAdvert.targets", "targeterTarget", JoinType.INNER_JOIN) //
                .createAlias("targeterTarget.targetAdvert", "targetAdvert", JoinType.INNER_JOIN) //
                .createAlias("targetAdvert." + targetScope.getLowerCamelName(), "targetResource", JoinType.INNER_JOIN) //
                .createAlias("targetResource.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("userRole.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN,
                        Restrictions.eq("stateActionAssignment.externalMode", true)) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN,
                        Restrictions.isNull("stateAction.actionCondition")) //
                .createAlias("stateAction.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.in(scope.equals(APPLICATION) ? "resource.id" : "targeterResource.advert.id", targeterEntities)) //
                .add(Restrictions.eqProperty("state", "stateAction.state")) //
                .add(Restrictions.isNull("state.hidden")) //
                .add(Restrictions.eq("action.systemInvocationOnly", false));
    }

    public static Junction getTargetActionConstraint() {
        return Restrictions.disjunction() //
                .add(Restrictions.isNull("action.partnershipState")) //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.conjunction() //
                                        .add(Restrictions.isNull("target.id")) //
                                        .add(Restrictions.eq("action.partnershipTransitionState", ENDORSEMENT_REVOKED))) //
                                .add(Restrictions.eqProperty("action.partnershipState", "target.partnershipState"))) //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.eq("resource.shared", true)) //
                                .add(Restrictions.eq("scope.defaultShared", true))));
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

    public static Junction getOpportunityCategoryConstraint(PrismOpportunityCategory opportunityCategory) {
        String opportunityCategoryName = opportunityCategory.name();
        return Restrictions.disjunction() //
                .add(Restrictions.eq("opportunityCategories", opportunityCategoryName))
                .add(Restrictions.like("opportunityCategories", opportunityCategoryName + "|", MatchMode.START))
                .add(Restrictions.like("opportunityCategories", "|" + opportunityCategoryName + "|", MatchMode.ANYWHERE))
                .add(Restrictions.like("opportunityCategories", "|" + opportunityCategoryName, MatchMode.END));
    }

    public static Junction getResourceParentManageableConstraint(PrismScope resourceScope) {
        return Restrictions.conjunction() //
                .add(getResourceParentManageableStateConstraint(resourceScope))
                .add(Restrictions.eq("userRole.role.id", PrismRole.valueOf(resourceScope.name() + "_ADMINISTRATOR")));
    }

    public static Criterion getResourceParentManageableStateConstraint(PrismScope resourceScope) {
        return Restrictions.not( //
                Restrictions.in("state.id", PrismEnumUtils.values(PrismState.class, resourceScope, new String[]{"UNSUBMITTED", "WITHDRAWN", "REJECTED", "DISABLED_COMPLETED"})));
    }

    public static Junction getResourceParentManageableConstraint(PrismScope resourceScope, User user) {
        return getResourceParentManageableConstraint(resourceScope)
                .add(Restrictions.eq("userRole.user", user));
    }

    public static Junction getResourceParentConnectableConstraint(PrismScope resourceScope, User user) {
        return Restrictions.conjunction() //
                .add(getResourceParentManageableStateConstraint(resourceScope))
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("role.roleCategory", ADMINISTRATOR)) //
                        .add(Restrictions.eq("role.roleCategory", RECRUITER))) //
                .add(Restrictions.eq("userRole.user", user));
    }

    public static Junction getResourceRecentlyActiveConstraint(DateTime baseline) {
        return Restrictions.disjunction() //
                .add(Restrictions.eq("stateAction.raisesUrgentFlag", true)) //
                .add(Restrictions.gt("resource.updatedTimestamp", baseline));
    }

    public static Criterion getLikeConstraint(String property, String query) {
        return Restrictions.like(property, query, MatchMode.ANYWHERE);
    }

    private Criteria getWorkflowCriteriaListResource(PrismScope scope, Projection projection) {
        return sessionFactory.getCurrentSession().createCriteria(ResourceState.class)
                .setProjection(projection) //
                .createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN);
    }

}