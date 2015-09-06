package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getEndorsementActionFilterResolution;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getEndorsementActionJoinResolution;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getPartnerUserRoleConstraint;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getResourceStateActionConstraint;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getUserEnabledConstraint;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getUserRoleConstraint;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_STARTUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.ESCALATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.PURGE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.VIEW_EDIT_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.StateAction;
import com.zuehlke.pgadmissions.dto.ActionCreationScopeDTO;
import com.zuehlke.pgadmissions.dto.ActionDTO;
import com.zuehlke.pgadmissions.dto.ActionRedactionDTO;

@Repository
@SuppressWarnings("unchecked")
public class ActionDAO {

    @Inject
    private SessionFactory sessionFactory;

    public Action getUserRedirectAction(Resource resource, User user) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (Action) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.property("stateAction.action")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".user", "owner", JoinType.INNER_JOIN) //
                .createAlias("owner.userAdverts", "ownerAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(resourceReference + ".advert", "advert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.targets.adverts", "advertTarget", JoinType.LEFT_OUTER_JOIN,
                        getEndorsementActionJoinResolution()) //
                .createAlias("advertTarget.value", "targetAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(Restrictions.eq("action.actionCategory", VIEW_EDIT_RESOURCE)) //
                .add(getUserRoleConstraint(resource, user, "stateActionAssignment")) //
                .add(getEndorsementActionFilterResolution())
                .setMaxResults(1) //
                .uniqueResult();
    }

    public Action getSystemRedirectAction(Resource resource) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (Action) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.property("stateAction.action")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action.systemInvocationOnly", true)) //
                .add(Restrictions.eq("action.actionCategory", VIEW_EDIT_RESOURCE)) //
                .add(getResourceStateActionConstraint()) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public Action getPermittedAction(Resource resource, Action action, User user) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (Action) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.property("stateAction.action")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".user", "owner", JoinType.INNER_JOIN) //
                .createAlias("owner.userAdverts", "ownerAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(resourceReference + ".advert", "advert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.targets.adverts", "advertTarget", JoinType.LEFT_OUTER_JOIN,
                        getEndorsementActionJoinResolution()) //
                .createAlias("advertTarget.value", "targetAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(getUserRoleConstraint(resource, user, "stateActionAssignment")) //
                .add(getEndorsementActionFilterResolution())
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<ActionDTO> getPermittedActions(PrismScope resourceScope, Collection<Integer> resourceIds, List<PrismScope> parentScopes, User user) {
        String resourceReference = resourceScope.getLowerCamelName();
        String resourceIdReference = resourceReference + ".id";

        Junction resourceConstraint = Restrictions.disjunction() //
                .add(Restrictions.eqProperty(resourceReference, "userRole." + resourceReference));
        parentScopes.forEach(parentScope -> {
            String parentReference = parentScope.getLowerCamelName();
            resourceConstraint.add(Restrictions.eqProperty(resourceReference + "." + parentReference, "userRole." + parentReference));
        });

        return (List<ActionDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty(resourceReference + ".id"), "resourceId") //
                        .add(Projections.groupProperty("action.id"), "actionId") //
                        .add(Projections.max("stateAction.raisesUrgentFlag"), "raisesUrgentFlag") //
                        .add(Projections.max("primaryState"), "primaryState") //
                        .add(Projections.property("action.declinableAction"), "declinable")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".user", "owner", JoinType.INNER_JOIN) //
                .createAlias("owner.userAdverts", "ownerAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN)
                .createAlias(resourceReference + ".advert", "advert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.targets.adverts", "advertTarget", JoinType.LEFT_OUTER_JOIN,
                        getEndorsementActionJoinResolution()) //
                .createAlias("advertTarget.value", "targetAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("action.systemInvocationOnly", false)) //
                .add(Restrictions.in(resourceIdReference, resourceIds)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(resourceConstraint) //
                                .add(Restrictions.eq("stateActionAssignment.partnerMode", false))) //
                        .add(getPartnerUserRoleConstraint(resourceScope, "stateActionAssignment"))) //
                .add(getResourceStateActionConstraint()) //
                .add(getUserEnabledConstraint(user)) //
                .add(getEndorsementActionFilterResolution())
                .addOrder(Order.asc(resourceIdReference))
                .addOrder(Order.desc("raisesUrgentFlag")) //
                .addOrder(Order.desc("primaryState")) //
                .addOrder(Order.asc("action.id")) //
                .setResultTransformer(Transformers.aliasToBean(ActionDTO.class)) //
                .list();
    }

    public List<ActionDTO> getPermittedUnsecuredActions(PrismScope resourceScope, Collection<Integer> resourceIds, PrismScope... exclusions) {
        String resourceReference = resourceScope.getLowerCamelName();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty(resourceReference + ".id"), "resourceId") //
                        .add(Projections.groupProperty("action.id"), "actionId") //
                        .add(Projections.max("stateAction.raisesUrgentFlag"), "raisesUrgentFlag") //
                        .add(Projections.max("primaryState"), "primaryState") //
                        .add(Projections.property("action.declinableAction"), "declinable")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.in(resourceReference + ".id", resourceIds)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("action.scope.id", PrismScope.SYSTEM)) //
                        .add(Restrictions.isNotNull("resourceCondition.id")))
                .add(Restrictions.eq("action.systemInvocationOnly", false)) //
                .add(Restrictions.isNull("stateActionAssignment.id"));

        for (PrismScope exclusion : exclusions) {
            criteria.add(Restrictions.disjunction() //
                    .add(Restrictions.isNull("action.creationScope")) //
                    .add(Restrictions.ne("action.creationScope.id", exclusion))); //
        }

        return (List<ActionDTO>) criteria.add(getResourceStateActionConstraint()) //
                .addOrder(Order.asc("action.id")) //
                .setResultTransformer(Transformers.aliasToBean(ActionDTO.class)) //
                .list();
    }

    public Action getPermittedUnsecuredAction(Resource resource, Action action, boolean userLoggedIn) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (Action) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("stateAction.action")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .add(Restrictions.isNull("stateActionAssignment.id")) //
                .add(getResourceStateActionConstraint()) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("action.scope.id", PrismScope.SYSTEM)) //
                        .add(Restrictions.eq("resourceCondition.actionCondition", ACCEPT_APPLICATION)) //
                        .add(Restrictions.in("resourceCondition.partnerMode", Lists.newArrayList(new Boolean(true), new Boolean(!userLoggedIn))))) //
                .uniqueResult();
    }

    public List<PrismAction> getCreateResourceActions(PrismScope creationScope) {
        return (List<PrismAction>) sessionFactory.getCurrentSession().createCriteria(Action.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("action.systemInvocationOnly", false)) //
                .add(Restrictions.eq("actionCategory", CREATE_RESOURCE)) //
                .add(Restrictions.eq("creationScope.id", creationScope)) //
                .list();
    }

    public List<ActionRedactionDTO> getRedactions(Resource resource, List<PrismRole> roleIds) {
        return (List<ActionRedactionDTO>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("action.id"), "actionId") //
                        .add(Projections.groupProperty("redaction.redactionType"), "redactionType")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.redactions", "redaction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.in("redaction.role.id", roleIds)) //
                .setResultTransformer(Transformers.aliasToBean(ActionRedactionDTO.class)) //
                .list();
    }

    public List<PrismActionEnhancement> getGlobalActionEnhancements(Resource resource, User user) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (List<PrismActionEnhancement>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("stateAction.actionEnhancement")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".user", "owner", JoinType.INNER_JOIN) //
                .createAlias("owner.userAdverts", "ownerAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(resourceReference + ".advert", "advert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.targets.adverts", "advertTarget", JoinType.LEFT_OUTER_JOIN,
                        getEndorsementActionJoinResolution()) //
                .createAlias("advertTarget.value", "targetAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.isNotNull("stateAction.actionEnhancement")) //
                .add(Restrictions.eq("action.actionCategory", PrismActionCategory.VIEW_EDIT_RESOURCE)) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(getUserRoleConstraint(resource, user, "stateActionAssignment")) //
                .add(getEndorsementActionFilterResolution())
                .list();
    }

    public List<PrismActionEnhancement> getGlobalActionEnhancements(Resource resource, PrismAction actionId, User user) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (List<PrismActionEnhancement>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("stateAction.actionEnhancement")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".user", "owner", JoinType.INNER_JOIN) //
                .createAlias("owner.userAdverts", "ownerAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(resourceReference + ".advert", "advert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.targets.adverts", "advertTarget", JoinType.LEFT_OUTER_JOIN,
                        getEndorsementActionJoinResolution()) //
                .createAlias("advertTarget.value", "targetAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.isNotNull("stateAction.actionEnhancement")) //
                .add(Restrictions.eq("stateAction.action.id", actionId)) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(getUserRoleConstraint(resource, user, "stateActionAssignment")) //
                .add(getEndorsementActionFilterResolution())
                .list();
    }

    public List<PrismActionEnhancement> getCustomActionEnhancements(Resource resource, User user) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (List<PrismActionEnhancement>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("stateActionAssignment.actionEnhancement")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".user", "owner", JoinType.INNER_JOIN) //
                .createAlias("owner.userAdverts", "ownerAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(resourceReference + ".advert", "advert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.targets.adverts", "advertTarget", JoinType.LEFT_OUTER_JOIN,
                        getEndorsementActionJoinResolution()) //
                .createAlias("advertTarget.value", "targetAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.isNotNull("stateActionAssignment.actionEnhancement")) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(getUserRoleConstraint(resource, user, "stateActionAssignment")) //
                .add(getEndorsementActionFilterResolution())
                .list();
    }

    public List<PrismActionEnhancement> getCustomActionEnhancements(Resource resource, PrismAction actionId, User user) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (List<PrismActionEnhancement>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("stateActionAssignment.actionEnhancement")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".user", "owner", JoinType.INNER_JOIN) //
                .createAlias("owner.userAdverts", "ownerAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(resourceReference + ".advert", "advert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.targets.adverts", "advertTarget", JoinType.LEFT_OUTER_JOIN,
                        getEndorsementActionJoinResolution()) //
                .createAlias("advertTarget.value", "targetAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.isNotNull("stateActionAssignment.actionEnhancement")) //
                .add(Restrictions.eq("stateAction.action.id", actionId)) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(getUserRoleConstraint(resource, user, "stateActionAssignment")) //
                .add(getEndorsementActionFilterResolution())
                .list();
    }

    public Action getViewEditAction(Resource resource) {
        return (Action) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("action")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action.actionCategory", PrismActionCategory.VIEW_EDIT_RESOURCE)) //
                .uniqueResult();
    }

    public List<PrismAction> getEscalationActions() {
        return (List<PrismAction>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("action.id")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.in("action.actionCategory", Arrays.asList(ESCALATE_RESOURCE, PURGE_RESOURCE))) //
                .addOrder(Order.desc("scope.ordinal")) //
                .list();
    }

    public List<Action> getCustomizableActions() {
        return (List<Action>) sessionFactory.getCurrentSession().createCriteria(Action.class) //
                .add(Restrictions.eq("customizableAction", true)) //
                .list();
    }

    public List<Action> getConfigurableActions() {
        return (List<Action>) sessionFactory.getCurrentSession().createCriteria(Action.class) //
                .add(Restrictions.eq("configurableAction", true)) //
                .list();
    }

    public List<PrismAction> getStateGroupTransitionActions() {
        return (List<PrismAction>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("action.id")) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("stateTransitions", "stateTransition", JoinType.INNER_JOIN) //
                .createAlias("stateTransition.transitionState", "transitionState", JoinType.INNER_JOIN) //
                .add(Restrictions.neProperty("state.stateGroup", "transitionState.stateGroup")) //
                .list();
    }

    public void setStateGroupTransitionActions(List<PrismAction> actions) {
        sessionFactory.getCurrentSession().createQuery( //
                "update Action " //
                        + "set transitionAction = true " //
                        + "where id in (:actions)")
                .setParameterList("actions", actions) //
                .executeUpdate();
    }

    public List<ActionCreationScopeDTO> getCreationActions() {
        return (List<ActionCreationScopeDTO>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("action"), "action") //
                        .add(Projections.groupProperty("transitionState.scope"), "creationScope"))
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateTransitions", "stateTransition", JoinType.INNER_JOIN) //
                .createAlias("stateTransition.transitionState", "transitionState", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("action.actionCategory", CREATE_RESOURCE)) //
                        .add(Restrictions.eq("action.id", SYSTEM_STARTUP))) //
                .setResultTransformer(Transformers.aliasToBean(ActionCreationScopeDTO.class)) //
                .list();
    }

    public List<PrismActionCondition> getActionConditions(PrismScope prismScope) {
        return (List<PrismActionCondition>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("actionCondition")) //
                .createAlias("action", "action") //
                .add(Restrictions.eq("action.scope.id", prismScope)) //
                .add(Restrictions.isNotNull("actionCondition")) //
                .list();
    }

    public List<PrismAction> getPartnerActions(Resource resource, List<PrismActionCondition> actionConditions) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("stateAction.action.id")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".resourceConditions", "resourceCondition", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN,
                        Restrictions.eqProperty("stateAction.actionCondition", "resourceCondition.actionCondition")) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("resourceCondition.actionCondition", ACCEPT_APPLICATION)) //
                        .add(Restrictions.eq("resourceCondition.partnerMode", true)));

        if (!actionConditions.isEmpty()) {
            criteria.add(Restrictions.in("resourceCondition.actionCondition", actionConditions));
        }

        return (List<PrismAction>) criteria //
                .addOrder(Order.asc("stateAction.action.id")) //
                .list();
    }

    public List<PrismActionEnhancement> getExpectedDefaultActionEnhancements(Resource resource, Action action) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (List<PrismActionEnhancement>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("stateAction.actionEnhancement"))
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .list();
    }

    public List<PrismActionEnhancement> getExpectedCustomActionEnhancements(Resource resource, Action action) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (List<PrismActionEnhancement>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("stateActionAssignment.actionEnhancement"))
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(Restrictions.eq("stateAction.action", action)) //
                .list();
    }

}
