package uk.co.alumeni.prism.dao;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.ArrayUtils.contains;
import static uk.co.alumeni.prism.dao.WorkflowDAO.advertScopes;
import static uk.co.alumeni.prism.dao.WorkflowDAO.getTargetActionConstraint;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.CREATE;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserRole;
import uk.co.alumeni.prism.domain.workflow.ActionRedaction;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.domain.workflow.RoleTransition;
import uk.co.alumeni.prism.domain.workflow.StateAction;
import uk.co.alumeni.prism.domain.workflow.StateActionAssignment;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.dto.ResourceRoleDTO;
import uk.co.alumeni.prism.dto.UserRoleDTO;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;

@Repository
@SuppressWarnings("unchecked")
public class RoleDAO {

    @Inject
    private WorkflowDAO workflowDAO;

    @Inject
    private SessionFactory sessionFactory;

    public List<PrismRole> getRolesOverridingRedactions(User user, PrismScope scope, Collection<Integer> resourceIds) {
        return (List<PrismRole>) workflowDAO.getWorkflowCriteriaList(scope, Projections.groupProperty("role.id")) //
                .add(getRolesOverridingRedactionsConstraint(user, resourceIds)) //
                .list();
    }

    public List<PrismRole> getRolesOverridingRedactions(User user, PrismScope scope, PrismScope parentScope, Collection<Integer> resourceIds) {
        return (List<PrismRole>) workflowDAO.getWorkflowCriteriaList(scope, parentScope, Projections.groupProperty("role.id")) //
                .add(getRolesOverridingRedactionsConstraint(user, resourceIds)) //
                .list();
    }

    public List<PrismRole> getRolesOverridingRedactions(User user, PrismScope scope, PrismScope targeterScope, PrismScope targetScope,
            Collection<Integer> targeterEntities, Collection<Integer> resourceIds) {
        return (List<PrismRole>) workflowDAO.getWorkflowCriteriaList(scope, targeterScope, targetScope, targeterEntities, Projections.groupProperty("role.id"))
                .add(getRolesOverridingRedactionsConstraint(user, resourceIds)) //
                .add(getTargetActionConstraint()) //
                .list();
    }

    public List<PrismRole> getRolesForResource(Resource resource, User user) {
        return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("role.id")) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .list();
    }

    public List<UserRoleDTO> getUserRoles(Resource resource, User user) {
        return getUserRoles(resource, user, null);
    }

    public List<UserRoleDTO> getUserRoles(Resource resource, List<PrismRole> roles) {
        return getUserRoles(resource, null, roles);
    }

    public List<UserRoleDTO> getUserRoles(Collection<Resource> resources, List<PrismRole> roles) {
        return getUserRoles(resources, null, roles);
    }

    public List<UserRoleDTO> getUserRoles(Resource resource, User user, List<PrismRole> roles) {
        return getUserRoles(newArrayList(resource), user, roles);
    }

    public List<UserRoleDTO> getUserRoles(Collection<Resource> resources, User user, List<PrismRole> roles) {
        Junction constraints = Restrictions.disjunction();
        LinkedHashMultimap<PrismScope, Resource> constrainingResources = LinkedHashMultimap.create();
        resources.stream().forEach(resource -> {
            Map<PrismScope, Resource> enclosingResources = resource.getEnclosingResources();
            enclosingResources.keySet().forEach(enclosingScope -> constrainingResources.put(enclosingScope, enclosingResources.get(enclosingScope)));
        });

        HashMultimap<PrismScope, PrismRole> rolesByScope = HashMultimap.create();
        roles.forEach(role -> rolesByScope.put(role.getScope(), role));

        constrainingResources.keySet().forEach(constrainingScope -> {
            Junction constraint = Restrictions.conjunction() //
                    .add(Restrictions.in(constrainingScope.getLowerCamelName(), constrainingResources.get(constrainingScope)));

            if (user != null) {
                constraint.add(Restrictions.eq("user", user));
            }

            if (isNotEmpty(roles)) {
                constraint.add(Restrictions.in("role.id", rolesByScope.get(constrainingScope)));
            }

            constraints.add(constraint);
        });

        return (List<UserRoleDTO>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("user").as("user")) //
                        .add(Projections.property("role.id").as("role"))) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.scope", "roleScope", JoinType.INNER_JOIN) //
                .add(constraints) //
                .addOrder(Order.asc("roleScope.ordinal")) //
                .addOrder(Order.asc("role.id")) //
                .addOrder(Order.asc("user.fullName")) //
                .setResultTransformer(Transformers.aliasToBean(UserRoleDTO.class)) //
                .list();
    }

    public UserRole getUserRole(Resource resource, User user, Role role) {
        return (UserRole) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("role", role)) //
                .uniqueResult();
    }

    public UserRole getUserRole(Resource resource, User user, PrismRole prismRole) {
        return (UserRole) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("role.id", prismRole)) //
                .uniqueResult();
    }

    public List<UserRoleDTO> getUserRoles(Resource resource) {
        return (List<UserRoleDTO>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("user").as("user")) //
                        .add(Projections.property("role.id").as("role"))) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .setResultTransformer(Transformers.aliasToBean(UserRoleDTO.class))
                .list();
    }

    public List<ResourceRoleDTO> getUserRoles(User user, PrismScope resourceScope) {
        String resourceReference = resourceScope.getLowerCamelName();
        return (List<ResourceRoleDTO>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("role.scope.id").as("scope")) //
                        .add(Projections.property(resourceReference + ".id").as("id")) //
                        .add(Projections.property("role.id").as("role")) //
                        .add(Projections.property("role.verified").as("verified")) //
                        .add(Projections.property("role.directlyAssignable").as("directlyAssignable"))) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.isNotNull(resourceReference)) //
                .add(Restrictions.eq("user", user)) //
                .setResultTransformer(Transformers.aliasToBean(ResourceRoleDTO.class)) //
                .list();
    }

    public List<User> getRoleUsers(Resource resource, Role... roles) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.in("role", roles)) //
                .list();
    }

    public List<User> getRoleUsers(Resource resource, PrismRole... prismRoles) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user")) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.in("role.id", prismRoles)) //
                .list();
    }

    public Role getCreatorRole(Resource resource) {
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

    public List<User> getUnspecifiedRoleTransitionUsers(Resource resource, RoleTransition roleTransition, User actionOwner) {
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

    public List<PrismRole> getCreatableRoles(PrismScope scopeId) {
        return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .setProjection(Projections.groupProperty("transitionRole.id")) //
                .createAlias("transitionRole", "transitionRole", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("roleTransitionType", PrismRoleTransitionType.CREATE)) //
                .add(Restrictions.eq("transitionRole.scope.id", scopeId)) //
                .list();
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

    public List<PrismRole> getRolesByScope(PrismScope prismScope) {
        return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(Role.class) //
                .setProjection(Projections.groupProperty("id")) //
                .add(Restrictions.eq("scope.id", prismScope)) //
                .list();
    }

    public List<PrismRole> getRolesByScope(User user, PrismScope prismScope) {
        return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("role.id")) //
                .add(Restrictions.isNotNull(prismScope.getLowerCamelName())) //
                .add(Restrictions.eq("user", user)) //
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

    public List<PrismRole> getRolesWithRedactions(PrismScope resourceScope) {
        return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(ActionRedaction.class) //
                .setProjection(Projections.groupProperty("role.id").as("id")) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("role.scope.id", resourceScope))
                .list();
    }

    public List<PrismRole> getVerifiedRoles() {
        return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(StateActionAssignment.class) //
                .setProjection(Projections.groupProperty("role.id")) //
                .list();
    }

    public void setVerifiedRoles(List<PrismRole> roles) {
        sessionFactory.getCurrentSession().createQuery( //
                "update Role " //
                        + "set verified = true " //
                        + "where id in (:roles)") //
                .setParameterList("roles", roles) //
                .executeUpdate();
    }

    public List<PrismRole> getVerifiedRoles(User user, ResourceParent resource) {
        PrismScope resourceScope = resource.getResourceScope();
        return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("role.id")) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resourceScope.getLowerCamelName(), resource))
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("role.verified", true)) //
                .list();
    }

    public List<UserRole> getUnverifiedRoles(Resource resource, User user) {
        return (List<UserRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("role.verified", false)) //
                .list();
    }

    public PrismRole getDefaultRoleCategories(PrismScope scope, User user) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("role.id")) //
                .createAlias(scope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.scope", "scope", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user", user));

        if (contains(advertScopes, scope)) {
            criteria.add(Restrictions.ne("resourceState.state.id", PrismState.valueOf(scope.name() + "_UNSUBMITTED")));
        }

        return (PrismRole) criteria.addOrder(Order.desc("assignedTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<UserRole> getUnnacceptedRolesForUser(User user) {
        return (List<UserRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .add(Restrictions.isNull("acceptedTimestamp")) //
                .add(Restrictions.eq("role.verified", true)) //
                .list();
    }

    private static Junction getRolesOverridingRedactionsConstraint(User user, Collection<Integer> resourceIds) {
        return Restrictions.conjunction() //
                .add(Restrictions.in("resource.id", resourceIds)) //
                .add(Restrictions.eq("userRole.user", user)) //
                .add(Restrictions.isEmpty("role.actionRedactions")) //
                .add(Restrictions.eq("userAccount.enabled", true));
    }

}
