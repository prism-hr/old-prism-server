package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.PrismScope;
import com.zuehlke.pgadmissions.domain.PrismSystem;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.RoleTransition;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.Authority;
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

    public PrismSystem getPrismSystem() {
        return (PrismSystem) sessionFactory.getCurrentSession().createCriteria(PrismSystem.class).uniqueResult();
    }

    public Role getById(final Authority id) {
        return (Role) sessionFactory.getCurrentSession().createCriteria(Role.class).add(Restrictions.eq("id", id)).uniqueResult();
    }

    public int save(UserRole userRole) {
        return (Integer) sessionFactory.getCurrentSession().save(userRole);
    }

    public List<User> getUsersInRole(PrismScope scope, Authority[] authorities) {
        // TODO Auto-generated method stub, sort by first and last names
        return null;
    }

    public User getUserInRole(PrismScope scope, Authority[] authorities) {
        // TODO Auto-generated method stub
        return null;
    }

    public UserRole get(User user, PrismScope scope, Authority authority) {
        return (UserRole) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.eq("role.id", authority)) //
                .add(Restrictions.eq(scope.getScopeName(), scope)) //
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<RoleTransition> getRoleTransitions(StateTransition stateTransition, Role invokingRole) {
        return (List<RoleTransition>) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .add(Restrictions.eq("stateTransition", stateTransition)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("role", invokingRole)) //
                                .add(Restrictions.eq("restrictToInvoker", true))) //
                        .add(Restrictions.ne("restrictToInvoker", true))) //
                .addOrder(Order.asc("role")) //
                .addOrder(Order.asc("processingOrder")) //
                .list();
    }
    
    public Role getCreatorRole(ApplicationFormAction action, PrismScope resource) {
        return (Role) sessionFactory.getCurrentSession().createCriteria(RoleTransition.class) //
                .setProjection(Projections.groupProperty("transitionRole"))
                .createAlias("stateTransition", "stateTransition", JoinType.INNER_JOIN)
                .createAlias("stateTransition.stateAction", "stateAction", JoinType.INNER_JOIN)
                .add(Restrictions.eq("stateAction.state", resource.getState())) //
                .add(Restrictions.eq("stateAction.action.id", action)) //
                .add(Restrictions.eq("type", RoleTransitionType.UPDATE)) //
                .add(Restrictions.eq("restrictToInvoker", true)).uniqueResult();
    }

    public Role canExecute(User user, PrismScope scope, ApplicationFormAction action) {
        // TODO reimplement using query
        switch (action) {
        case PROGRAM_CREATE_APPLICATION:
            return getById(Authority.PROGRAM_APPLICATION_CREATOR);
        case APPLICATION_COMPLETE:
            return getById(Authority.APPLICATION_CREATOR);
        case APPLICATION_ASSIGN_REVIEWERS:
            return getById(Authority.APPLICATION_ADMINISTRATOR);
        default:
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<User> getBy(Role role, PrismScope scope) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .setProjection(Projections.distinct(Projections.property("user"))) //
                .add(Restrictions.eq("role", role)) //
                .add(Restrictions.eq(scope.getScopeName(), scope)) //
                .list();
    }

    public UserRole getUserRole(User user, Authority authority) {
        return (UserRole) sessionFactory.getCurrentSession().createCriteria(UserRole.class) //
                .add(Restrictions.eq("role.id", authority)) //
                .add(Restrictions.eq("user", user)) //
                .uniqueResult();
    }

}
