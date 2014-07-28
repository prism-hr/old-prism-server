package com.zuehlke.pgadmissions.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

@Repository
@SuppressWarnings("unchecked")
public class RoleDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public UserRole getUserRole(Resource resource, User user, Role role) {
        return (UserRole) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource)) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("role", role)) //
                .uniqueResult();
    }
    
    public List<UserRole> getUserRoles(Resource resource, User user, PrismRole... authorities) {
        return (List<UserRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource)) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.in("role.id", authorities)) //
                .list();
    }
    
    public List<User> getRoleUsers(Resource resource, Role role) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource)) //
                .setProjection(Projections.property("user")) //
                .add(Restrictions.eq("role", role)) //
                .list();
    }

    public List<Role> getExcludingRoles(UserRole userRole, Comment comment) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CommentAssignedUser.class) //
                .setProjection(Projections.property("role"))
                .add(Restrictions.eq("comment", comment)) //
                .add(Restrictions.eq("user", userRole.getUser())) //
                .add(Restrictions.ne("role", userRole.getRole()));
        
        getExcludedRoleDisjunction(userRole, criteria);
                
        return criteria.list();
    }

    public List<UserRole> getExcludingUserRoles(UserRole userRole) {
        Resource resource = userRole.getResource();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("application", resource.getApplication())) //
                        .add(Restrictions.eq("project", resource.getProject())) //
                        .add(Restrictions.eq("program", resource.getProgram())) //
                        .add(Restrictions.eq("institution", resource.getInstitution())) //
                        .add(Restrictions.eq("system", resource.getSystem()))) //
                .add(Restrictions.eq("user", userRole.getUser())) //
                .add(Restrictions.ne("role", userRole.getRole()));
        
        getExcludedRoleDisjunction(userRole, criteria);
                
        return criteria.list();
    }

    public Role getCreatorRole(Resource resource) {
        return (Role) sessionFactory.getCurrentSession().createCriteria(Role.class) //
                .add(Restrictions.eq("scope.id", PrismScope.getResourceScope(resource.getClass()))) //
                .add(Restrictions.eq("scopeCreator", true)) //
                .uniqueResult();
    }

    public List<Role> getActionOwnerRoles(User user, Resource resource, Action action) {
        return (List<Role>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("stateActionAssignment.role")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action", action)) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                .add(Restrictions.eq("user.parentUser", user)) //
                .list();
    }

    public List<RoleTransition> getRoleTransitions(StateTransition stateTransition, PrismRoleTransitionType transitionType) {
        return (List<RoleTransition>) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .add(Restrictions.eq("stateTransition", stateTransition)) //
                .add(Restrictions.eq("roleTransitionType", transitionType)) //
                .list();
    }

    public List<User> getRoleTransitionUsers(Resource resource, RoleTransition roleTransition, User actionOwner) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .setProjection(Projections.property("userRole.user")) //
                .createAlias("role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", roleTransition.getId())) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("restrictToInvoker", true)) //
                                .add(Restrictions.eq("userRole.user", actionOwner))) //
                        .add(Restrictions.eq("restrictToInvoker", false))) //
                .list();
    }

    public List<User> getRoleCreateTransitionUsers(Comment comment, Role role, User user) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CommentAssignedUser.class) //
                .setProjection(Projections.property("user")) //
                .add(Restrictions.eq("comment", comment));

        if (user != null) {
            criteria.add(Restrictions.eq("user", user));
        }

        return (List<User>) criteria.add(Restrictions.eq("role", role)).list();
    }

    public List<User> getUsers(Resource resource) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("user"))
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource))
                .list();
    }

    public List<PrismRole> getUserRoles(Resource resource, User user) {
        return (List<PrismRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.groupProperty("role.id"))
                .add(Restrictions.eq("user", user))
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource))
                .list();
    }

    public List<PrismRole> getRoles(Class<? extends Resource> resourceType) {
        return sessionFactory.getCurrentSession().createCriteria(Role.class)
                .setProjection(Projections.property("id"))
                .add(Restrictions.eq("scope.id", PrismScope.getResourceScope(resourceType)))
                .list();
    }

    public List<Role> getActiveRoles() {
        return sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .setProjection(Projections.groupProperty("role"))
                .list();
    }

    public void deleteObseleteUserRoles(List<Role> activeRoles) {
        sessionFactory.getCurrentSession().createQuery( //
                "delete UserNotification " //
                        + "where userRole not in ( "
                        + "from UserRole " //
                        + "where role not in (:activeRoles))") //
                .setParameterList("activeRoles", activeRoles) //
                .executeUpdate(); //

        sessionFactory.getCurrentSession().createQuery( //
                "delete UserRole " //
                        + "where role not in (:activeRoles)") //
                .setParameterList("activeRoles", activeRoles) //
                .executeUpdate();
    }

    public List<UserRole> getUpdateNotificationRoles(User user, Resource resource, NotificationTemplate template) {
        return (List<UserRole>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .createAlias("userNotifications", "userNotification", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("userNotification.notificationTemplate", template)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                .list();
    }
    
    private void getExcludedRoleDisjunction(UserRole userRole, Criteria criteria) {
        Set<Role> exclusions = userRole.getRole().getExcludedRoles();
        if (!exclusions.isEmpty()) {
            Disjunction disjunction = Restrictions.disjunction();
            for (Role excludedRole : exclusions) {
                disjunction.add(Restrictions.eq("role", excludedRole));
            }
            criteria.add(disjunction);
        }
    }

}
