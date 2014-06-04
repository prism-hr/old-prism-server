package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.StateTransition;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.mail.NotificationDescriptor;

@Repository
@SuppressWarnings("unchecked")
public class UserDAO {

    private final SessionFactory sessionFactory;

    public UserDAO() {
        this(null);
    }

    @Autowired
    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    
    public User getParentUserByUserId(Integer userId) {
       return (User) sessionFactory.getCurrentSession().createCriteria(User.class) //
        .setProjection(Property.forName("parentUser")) //
        .add(Restrictions.eq("id", userId)) //
        .uniqueResult();
    }
    
    public void save(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(user);
        session.flush();
    }

    public User getById(Integer id) {
        return (User) sessionFactory.getCurrentSession().createCriteria(User.class).add(Restrictions.eq("id", id)).uniqueResult();
    }

    public User getPrimaryById(Integer id) {
        return (User) sessionFactory.getCurrentSession().createCriteria(User.class).createAlias("primaryAccount", "registeredUser", JoinType.INNER_JOIN)
                .add(Restrictions.disjunction().add(Restrictions.eq("id", id)).add(Restrictions.eq("registeredUser.id", id))).uniqueResult();
    }

    public List<User> getAllUsers() {
        return sessionFactory.getCurrentSession().createCriteria(User.class) //
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public User getUserByActivationCode(String activationCode) {
        return (User) sessionFactory.getCurrentSession().createCriteria(User.class)//
                .add(Restrictions.eq("activationCode", activationCode)) //
                .uniqueResult();
    }

    public Long getNumberOfActiveApplicationsForApplicant(final User applicant) {
        return (Long) sessionFactory.getCurrentSession().createCriteria(Application.class).add(Restrictions.eq("applicant", applicant))
                .add(Restrictions.not(Restrictions.eq("status", PrismState.APPLICATION_APPROVED)))
                .add(Restrictions.not(Restrictions.eq("status", PrismState.APPLICATION_REJECTED)))
                .add(Restrictions.not(Restrictions.eq("status", PrismState.APPLICATION_WITHDRAWN))).setProjection(Projections.rowCount()).uniqueResult();
    }

    public List<User> getUsersWithUpi(final String upi) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(User.class).add(Restrictions.eq("upi", upi)).list();
    }
    
    public List<User> getUsersForResource(PrismResource resource) {
        return sessionFactory.getCurrentSession().createCriteria(UserRole.class)
                .setProjection(Projections.groupProperty("user.parentUser")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("application", resource.getApplication())) //
                        .add(Restrictions.eq("project", resource.getProject())) //
                        .add(Restrictions.eq("program", resource.getProgram())) //
                        .add(Restrictions.eq("institution", resource.getInstitution())) //
                        .add(Restrictions.eq("system", resource.getSystem()))) //
                .list();
    }

    public List<User> getUsersForResourceAndRole(PrismResource resource, Authority authority) {
        return sessionFactory.getCurrentSession().createCriteria(UserRole.class)
                .setProjection(Projections.groupProperty("user.parentUser")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("application", resource.getApplication())) //
                        .add(Restrictions.eq("project", resource.getProject())) //
                        .add(Restrictions.eq("program", resource.getProgram())) //
                        .add(Restrictions.eq("institution", resource.getInstitution())) //
                        .add(Restrictions.eq("system", resource.getSystem()))) //
                .add(Restrictions.eq("role.id", authority)) //
                .list();
    }
    
    public User getUserByEmail(String email) {
        return (User) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .add(Restrictions.eq("email", email)).uniqueResult();
    }

    public User getDisabledUserByEmail(String email) {
        return (User) sessionFactory.getCurrentSession().createCriteria(User.class) //
                .createAlias("account", "account", JoinType.LEFT_OUTER_JOIN).add(Restrictions.disjunction() //
                        .add(Restrictions.isNull("account")).add(Restrictions.eq("account.enabled", false))) //
                .add(Restrictions.eq("email", email)).uniqueResult();
    }

    public User getUserByEmailIncludingDisabledAccounts(String email) {
        return (User) sessionFactory.getCurrentSession().createCriteria(User.class).add(Restrictions.eq("email", email)).uniqueResult();
    }

    public List<User> getSuperadministrators() {
        return sessionFactory.getCurrentSession().createCriteria(User.class).createAlias("roles", "role")
                .add(Restrictions.eq("role.id", Authority.SYSTEM_ADMINISTRATOR)).list();
    }

    public List<User> getAdmitters() {
        return sessionFactory.getCurrentSession().createCriteria(User.class).createAlias("roles", "role")
                .add(Restrictions.eq("role.id", Authority.INSTITUTION_ADMITTER)).list();
    }

    public void setApplicationFormListLastAccessTimestamp(User user) {
        user.getUserAccount().setApplicationListLastAccessTimestamp(new Date());
        save(user);
    }

    //TODO rewrite the query - HQL
    public List<User> getUsersInterestedInApplication(Application application) {
        return new ArrayList<User>();
    }

    //TODO rewrite the query - HQL
    public List<User> getUsersPotentiallyInterestedInApplication(Application application) {
        return new ArrayList<User>();
    }

    public List<NotificationDescriptor> getUseDueTaskNotification() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<NotificationDescriptor> getUserStateTransitionNotifications(StateTransition stateTransition) {
        // TODO Auto-generated method stub
        return null;
    }

}
