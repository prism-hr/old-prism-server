package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getEndorsementActionResolution;
import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getUserRoleConstraint;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PrismActionGroup.RESOURCE_ENDORSE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.RoleTransition;
import com.zuehlke.pgadmissions.domain.workflow.StateAction;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;

@Repository
@SuppressWarnings("unchecked")
public class RoleDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<PrismRole> getRoles(User user) {
        return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("role.id")) //
                .add(Restrictions.eq("user", user)) //
                .list();
    }

    public List<PrismRole> getRolesOverridingRedactions(PrismScope resourceScope, User user) {
        return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("role.id")) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("role.actionRedactions", "actionRedaction", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("action.scope.id", resourceScope)) //
                        .add(Restrictions.eq("action.creationScope.id", resourceScope))) //
                .add(Restrictions.isNull("actionRedaction.id")) //
                .list();
    }

    public List<PrismRole> getRolesOverridingRedactions(Resource<?> resource, User user) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.groupProperty("role.id")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + ".advert", "advert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("advert.targets.adverts", "advertTarget", JoinType.LEFT_OUTER_JOIN,
                        Restrictions.eq("advertTarget.selected", true)) //
                .createAlias("advertTarget.value", "targetAdvert", JoinType.LEFT_OUTER_JOIN) //
                .createAlias(resourceReference + ".comments", "comment", JoinType.LEFT_OUTER_JOIN,
                        Restrictions.conjunction() //
                                .add(Restrictions.eq("comment.user", user)) //
                                .add(Restrictions.in("comment.action.id", RESOURCE_ENDORSE.getActions())))
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("userRole.user", user)) //
                .createAlias("role.actionRedactions", "actionRedaction", JoinType.LEFT_OUTER_JOIN) //
                .add(getUserRoleConstraint(resource, "stateActionAssignment")) //
                .add(getEndorsementActionResolution("action.id", "comment.id"))
                .add(Restrictions.isNull("actionRedaction.id")) //
                .list();
    }

    public List<PrismRole> getRolesForResource(Resource<?> resource, User user) {
        return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("role.id")) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .list();
    }

    public UserRole getUserRole(Resource<?> resource, User user, Role role) {
        return (UserRole) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("role", role)) //
                .uniqueResult();
    }

    public UserRole getUserRole(Resource<?> resource, User user, PrismRole prismRole) {
        return (UserRole) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("role.id", prismRole)) //
                .uniqueResult();
    }

    public List<User> getRoleUsers(Resource<?> resource, Role... roles) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.in("role", roles)) //
                .list();
    }

    public List<User> getRoleUsers(Resource<?> resource, PrismRole... prismRoles) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.in("role.id", prismRoles)) //
                .list();
    }

    public Role getCreatorRole(Resource<?> resource) {
        return (Role) sessionFactory.getCurrentSession().createCriteria(Role.class) //
                .add(Restrictions.eq("scope.id", resource.getResourceScope())) //
                .add(Restrictions.isNotNull("scopeCreator")) //
                .uniqueResult();
    }

    public List<RoleTransition> getRoleTransitions(StateTransition stateTransition, PrismRoleTransitionType roleTransitionType) {
        return (List<RoleTransition>) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .add(Restrictions.eq("stateTransition", stateTransition)) //
                .add(Restrictions.eq("roleTransitionType", roleTransitionType)) //
                .list();
    }

    public List<User> getUnspecifiedRoleTransitionUsers(Resource<?> resource, RoleTransition roleTransition, User actionOwner) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .setProjection(Projections.groupProperty("userRole.user")) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", roleTransition.getId())) //
                .add(Restrictions.eq("userRole." + resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("restrictToActionOwner", true)) //
                                .add(Restrictions.eq("userRole.user", actionOwner))) //
                        .add(Restrictions.eq("restrictToActionOwner", false))) //
                .list();
    }

    public List<Role> getActiveRoles() {
        return sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .setProjection(Projections.groupProperty("role")) //
                .list();
    }

    public void deleteObsoleteUserRoles() {
        sessionFactory.getCurrentSession().createQuery( //
                "delete UserRole " //
                        + "where role not in ( " //
                        + "select role " //
                        + "from RoleTransition " //
                        + "group by role) " //
                        + "and role not in ( " //
                        + "select transitionRole " //
                        + "from RoleTransition " //
                        + "group by transitionRole)") //
                .executeUpdate();
    }

    public void deleteUserRoles(Resource<?> resource, User user) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete UserRole " //
                        + "where :resourceReference = :resource " //
                        + "and user = :user") //
                .setParameter("resourceReference", resource.getResourceScope().getLowerCamelName()) //
                .setParameter("resource", resource) //
                .setParameter("user", user) //
                .executeUpdate();
    }

    public List<PrismRole> getCreatableRoles(PrismScope scopeId) {
        return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .setProjection(Projections.groupProperty("transitionRole.id")) //
                .createAlias("transitionRole", "transitionRole", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("roleTransitionType", PrismRoleTransitionType.CREATE)) //
                .add(Restrictions.eq("transitionRole.scope.id", scopeId)) //
                .list();
    }

    public PrismScope getPermissionScope(User user) {
        return (PrismScope) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("scope.id")) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .addOrder(Order.asc("scope.ordinal")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public PrismScope getPermissionScopePartner(User user, PrismScope partnerScope) {
        String partnerScopeReference = partnerScope.getLowerCamelName();
        return (PrismScope) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("scope.id")) //
                .createAlias(partnerScopeReference, partnerScopeReference, JoinType.INNER_JOIN) //
                .createAlias("institution.advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("advert.targets.selectedResources", "selectedResources", JoinType.INNER_JOIN) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.stateAction", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("action.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("stateActionAssignment.partnerMode", true)) //
                .add(Restrictions.eq("user", user)) //
                .addOrder(Order.asc("scope.ordinal")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<UserRole> getUserRoleByRoleCategory(User user, PrismRoleCategory prismRoleCategory, PrismScope... excludedPrismScopes) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("role.roleCategory", prismRoleCategory)); //

        for (PrismScope excludedPrismScope : excludedPrismScopes) {
            criteria.add(Restrictions.isNull(excludedPrismScope.getLowerCamelName()));
        }

        return (List<UserRole>) criteria.list();
    }

    public List<PrismRole> getRolesByScopes(PrismScope prismScope) {
        return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(Role.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("scope.id", prismScope)) //
                .list();
    }

    public List<Role> getCreatorRoles() {
        return (List<Role>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("roleTransition.transitionRole")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateTransitions", "stateTransition", JoinType.INNER_JOIN) //
                .createAlias("stateTransition.roleTransitions", "roleTransition", JoinType.INNER_JOIN) //
                .add(Restrictions.isNotNull("action.creationScope")) //
                .add(Restrictions.eq("roleTransition.roleTransitionType", CREATE)) //
                .add(Restrictions.eq("roleTransition.restrictToActionOwner", true)) //
                .list();
    }

}
