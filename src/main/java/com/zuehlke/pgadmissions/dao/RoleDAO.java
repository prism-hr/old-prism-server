package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.dao.WorkflowDAO.getEndorsementActionFilterConstraint;
import static com.zuehlke.pgadmissions.dao.WorkflowDAO.getUserEnabledConstraint;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.ActionRedaction;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.domain.workflow.RoleTransition;
import com.zuehlke.pgadmissions.domain.workflow.StateAction;
import com.zuehlke.pgadmissions.domain.workflow.StateActionAssignment;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.dto.ResourceRoleDTO;
import com.zuehlke.pgadmissions.dto.UserRoleDTO;

@Repository
@SuppressWarnings("unchecked")
public class RoleDAO {

    @Inject
    private WorkflowDAO workflowDAO;

    @Inject
    private SessionFactory sessionFactory;

    public List<PrismRole> getRolesOverridingRedactions(User user, PrismScope scope, Collection<Integer> resourceIds) {
        return workflowDAO.getWorkflowCriteriaList(scope, Projections.groupProperty("role.id")) //
                .add(getRolesOverridingRedactionsConstraint(user, resourceIds)) //
                .list();
    }

    public List<PrismRole> getRolesOverridingRedactions(User user, PrismScope scope, PrismScope parentScope, Collection<Integer> resourceIds) {
        return workflowDAO.getWorkflowCriteriaList(scope, parentScope, Projections.groupProperty("role.id")) //
                .add(getRolesOverridingRedactionsConstraint(user, resourceIds)) //
                .list();
    }

    public List<PrismRole> getRolesOverridingRedactions(User user, PrismScope scope, PrismScope targeterScope, PrismScope targetScope, Collection<Integer> targeterEntities,
            Collection<Integer> resourceIds) {
        return workflowDAO.getWorkflowCriteriaList(scope, targeterScope, targetScope, targeterEntities, Projections.groupProperty("role.id"))
                .add(getRolesOverridingRedactionsConstraint(user, resourceIds)) //
                .add(getEndorsementActionFilterConstraint()) //
                .list();
    }

    public List<PrismRole> getRolesForResource(Resource resource, User user) {
        return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class, "userRole") //
                .setProjection(Projections.groupProperty("role.id")) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.department", resource.getDepartment())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                .list();
    }

    public List<PrismRole> getRolesForResourceStrict(Resource resource, User user) {
        return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("role.id")) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
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

    public List<PrismRole> getRolesByVisibleScope(User user, PrismScope prismScope) {
        return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("role.id")) //
                .createAlias(prismScope.getLowerCamelName(), "resource", JoinType.INNER_JOIN) //
                .createAlias("resource.resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("resourceState.state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.isNotNull("resource.id")) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("state.scope.id", SYSTEM)) //
                        .add(Restrictions.isNull("state.hidden"))) //
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

    private static Junction getRolesOverridingRedactionsConstraint(User user, Collection<Integer> resourceIds) {
        return Restrictions.conjunction() //
                .add(Restrictions.in("resource.id", resourceIds)) //
                .add(Restrictions.isEmpty("role.actionRedactions")) //
                .add(getUserEnabledConstraint(user));
    }

}
