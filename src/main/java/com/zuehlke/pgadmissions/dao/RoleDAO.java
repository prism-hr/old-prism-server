package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.RoleTransitionType;

@Repository
public class RoleDAO {

    private SessionFactory sessionFactory;

    public RoleDAO() {
    }

    @Autowired
    public RoleDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Role getById(final Authority id) {
        return (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("id", id)).uniqueResult();
    }

    public int save(UserRole userRole) {
        return (Integer) sessionFactory.getCurrentSession().save(userRole);
    }

    @SuppressWarnings("unchecked")
    public List<User> getUsersInRole(PrismResource scope, Authority[] authorities) {
        return sessionFactory.getCurrentSession().createCriteria(User.class) //
                .createAlias("userRoles", "userRole") //
                .add(Restrictions.in("userRole.role.id", authorities)) //
                .list();
    }

    public UserRole get(User user, PrismResource resource, Authority authority) {
        return (UserRole) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("role.id", authority)) //
                .add(Restrictions.eq(resource.getResourceType().toString().toLowerCase(), resource)) //
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<RoleTransition> getRoleTransitions(StateTransition stateTransition, List<Role> invokerRoles) {
        Criterion restrictToInvokerCriterion = invokerRoles.isEmpty() ? //
        Restrictions.eq("roleTransitionType", RoleTransitionType.CREATE)
                : Restrictions.in("role", invokerRoles);

        return (List<RoleTransition>) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .add(Restrictions.eq("stateTransition", stateTransition)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("restrictToInvoker", true)) //
                                .add(restrictToInvokerCriterion)) //
                        .add(Restrictions.eq("restrictToInvoker", false))) //
                .addOrder(Order.asc("role")) //
                .addOrder(Order.asc("processingOrder")) //
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public Role getCreatorRole(PrismAction action, PrismResource resource) {
        return (Role) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class)
                //
                .setProjection(Projections.groupProperty("role")).createAlias("stateTransition", "stateTransition", JoinType.INNER_JOIN)
                .createAlias("stateTransition.stateAction", "stateAction", JoinType.INNER_JOIN).add(Restrictions.eq("stateAction.state", resource.getState())) //
                .add(Restrictions.eq("stateAction.action.id", action)) //
                .add(Restrictions.eq("type", RoleTransitionType.CREATE)) //
                .add(Restrictions.eq("restrictToInvoker", true)).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Role> getActionRoles(PrismResource resource, PrismAction action) {
        return (List<Role>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.property("stateActionAssignment.role")) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action.id", action)) //
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<Role> getActionInvokerRoles(User user, PrismResource resource, PrismAction action) {
        return (List<Role>) sessionFactory.getCurrentSession().createCriteria(StateAction.class) //
                .setProjection(Projections.groupProperty("stateActionAssignment.role")) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action.id", action)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                .add(Restrictions.eq("user.parentUser", user)) //
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<UserRoleTransition> getRoleTransitions(StateTransition stateTransition, PrismResource resource, User invoker) {
        return (List<UserRoleTransition>) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class)
                .setProjection(Projections.property("userRole.user")) //
                .setProjection(Projections.property("role")) //
                .setProjection(Projections.property("roleTransitionType")) //
                .setProjection(Projections.property("transitionRole")) //
                .createAlias("role", "role", JoinType.INNER_JOIN)
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN)
                .add(Restrictions.eq("userRole." + resource.getClass().getSimpleName().toLowerCase(), resource))
                .add(Restrictions.disjunction()
                        .add(Restrictions.conjunction()
                                .add(Restrictions.eq("restrictToInvoker", true))
                                .add(Restrictions.eq("userRole.user", invoker)))
                        .add(Restrictions.eq("restrictToInvoker", false)))
                 .setResultTransformer(Transformers.aliasToBean(UserRoleTransition.class))
                 .list();
    }
    
    @SuppressWarnings("unchecked")
    public HashMultimap<Role, RoleTransitionInstruction> getUserRoleCreationInsructions(StateTransition stateTransition) {
        List<RoleTransition> roleTransitions = (List<RoleTransition>) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .add(Restrictions.eq("stateTransition", stateTransition)) //
                .add(Restrictions.eq("roleTransitionType", RoleTransitionType.CREATE)) //
                .list();

        HashMultimap<Role, RoleTransitionInstruction> instructions = HashMultimap.create();
        for (RoleTransition roleTransition : roleTransitions) {
            instructions.put(roleTransition.getRole(), new RoleTransitionInstruction(roleTransition.isRestrictToInvoker(),
                    roleTransition.getMinimumPermitted(), roleTransition.getMaximumPermitted()));
        }
        
        return instructions;
    }
    
    public UserRole getUserRole(User user, Authority authority) {
        return (UserRole) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.eq("role.id", authority)) //
                .add(Restrictions.eq("user", user)) //
                .uniqueResult();
    }

    public class UserRoleTransition {
        
        private User user;
        
        private Role role;
        
        private RoleTransitionType roleTransitionType;
        
        private Role transitionRole;

        public UserRoleTransition(User user, Role role, RoleTransitionType roleTransitionType, Role transitionRole) {
            this.user = user;
            this.role = role;
            this.roleTransitionType = roleTransitionType;
            this.transitionRole = transitionRole;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public RoleTransitionType getRoleTransitionType() {
            return roleTransitionType;
        }

        public void setRoleTransitionType(RoleTransitionType roleTransitionType) {
            this.roleTransitionType = roleTransitionType;
        }

        public Role getTransitionRole() {
            return transitionRole;
        }

        public void setTransitionRole(Role transitionRole) {
            this.transitionRole = transitionRole;
        }
        
    }

    public class RoleTransitionInstruction {

        private boolean restrictToInvoker;

        private Integer minimumPermitted;

        private Integer maximumPermitted;

        public RoleTransitionInstruction(boolean restrictToInvoker, Integer minimumPermitted, Integer maximumPermitted) {
            this.restrictToInvoker = restrictToInvoker;
            this.minimumPermitted = minimumPermitted;
            this.maximumPermitted = maximumPermitted;
        }

        public boolean isRestrictToInvoker() {
            return restrictToInvoker;
        }

        public void setRestrictToInvoker(boolean restrictToInvoker) {
            this.restrictToInvoker = restrictToInvoker;
        }

        public Integer getMinimumPermitted() {
            return minimumPermitted;
        }

        public void setMinimumPermitted(Integer minimumPermitted) {
            this.minimumPermitted = minimumPermitted;
        }

        public Integer getMaximumPermitted() {
            return maximumPermitted;
        }

        public void setMaximumPermitted(Integer maximumPermitted) {
            this.maximumPermitted = maximumPermitted;
        }

    }

}
