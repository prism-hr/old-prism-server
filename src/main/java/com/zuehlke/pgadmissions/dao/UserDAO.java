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
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.ReminderType;

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

    public List<User> getUsersForProgram(Program program) {
        // TODO implement using roleDAO
        return null;
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

    public List<Integer> getUsersDueTaskReminder(Date seedDate) {
        Date baselineDate = getBaselineDate(seedDate);
        Date reminderBaseline = getReminderBaseline(baselineDate, ReminderType.TASK);
        Date expiryBaseline = getExpiryBaseline(baselineDate);

        // TODO reimplement
        return Lists.newArrayList();
//        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(UserRole.class).setProjection(Projections.groupProperty("primaryAccount.id"))
//                .createAlias("user", "registeredUser", JoinType.INNER_JOIN).createAlias("actions", "applicationFormActionRequired", JoinType.INNER_JOIN)
//                .createAlias("applicationFormActionRequired.action", "action", JoinType.INNER_JOIN)
//                .add(Restrictions.eq("action.notification", NotificationMethod.SYNDICATED)).add(Restrictions.eq("raisesUrgentFlag", true))
//                .add(Restrictions.eq("registeredUser.latestTaskNotificationDate", reminderBaseline))
//                .add(Restrictions.gt("applicationFormActionRequired.deadlineTimestamp", expiryBaseline)).add(Restrictions.eq("registeredUser.enabled", true))
//                .add(Restrictions.eq("registeredUser.accountNonExpired", true)).add(Restrictions.eq("registeredUser.accountNonLocked", true))
//                .add(Restrictions.eq("registeredUser.credentialsNonExpired", true)).list();
    }

    public List<Integer> getUsersDueTaskNotification(Date seedDate) {
        Date baselineDate = getBaselineDate(seedDate);
        Date reminderBaseline = getReminderBaseline(baselineDate, ReminderType.TASK);
        Date expiryBaseline = getExpiryBaseline(baselineDate);

        // TODO reimplement
        return Lists.newArrayList();
//        return (List<Integer>) sessionFactory
//                .getCurrentSession()
//                .createCriteria(UserRole.class)
//                .setProjection(Projections.groupProperty("primaryAccount.id"))
//                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
//                .createAlias("actions", "applicationFormActionRequired", JoinType.INNER_JOIN)
//                .createAlias("applicationFormActionRequired.action", "action", JoinType.INNER_JOIN)
//                .add(Restrictions.eq("action.notification", NotificationMethod.SYNDICATED))
//                .add(Restrictions.eq("raisesUrgentFlag", true))
//                .add(Restrictions.disjunction().add(Restrictions.isNull("registeredUser.latestTaskNotificationDate"))
//                        .add(Restrictions.lt("registeredUser.latestTaskNotificationDate", reminderBaseline)))
//                .add(Restrictions.gt("applicationFormActionRequired.deadlineTimestamp", expiryBaseline)).add(Restrictions.eq("registeredUser.enabled", true))
//                .add(Restrictions.eq("registeredUser.accountNonExpired", true)).add(Restrictions.eq("registeredUser.accountNonLocked", true))
//                .add(Restrictions.eq("registeredUser.credentialsNonExpired", true)).list();
    }

    public List<Integer> getUsersDueUpdateNotification(Date seedDate) {
        Date baseline = getBaselineDate(seedDate);

        // TODO reimplement
        return Lists.newArrayList();
//        return (List<Integer>) sessionFactory
//                .getCurrentSession()
//                .createCriteria(UserRole.class)
//                .setProjection(Projections.groupProperty("primaryAccount.id"))
//                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
//                .createAlias("role", "role", JoinType.INNER_JOIN)
//                .add(Restrictions.eq("role.doSendUpdateNotification", true))
//                .add(Restrictions.eq("raisesUpdateFlag", true))
//                .add(Restrictions.disjunction().add(Restrictions.isNull("registeredUser.latestUpdateNotificationDate"))
//                        .add(Restrictions.lt("registeredUser.latestUpdateNotificationDate", baseline))).add(Restrictions.ge("updateTimestamp", baseline))
//                .add(Restrictions.eq("registeredUser.enabled", true)).add(Restrictions.eq("registeredUser.accountNonExpired", true))
//                .add(Restrictions.eq("registeredUser.accountNonLocked", true)).add(Restrictions.eq("registeredUser.credentialsNonExpired", true)).list();
    }

    public List<Integer> getUsersDueOpportunityRequestNotification(Date seedDate) {
        Date baseline = getBaselineDate(seedDate);

        return (List<Integer>) sessionFactory
                .getCurrentSession()
                .createCriteria(User.class)
                .setProjection(Projections.groupProperty("primaryAccount.id"))
                .createAlias("roles", "role")
                .add(Restrictions.disjunction().add(Restrictions.isNull("latestOpportunityRequestNotificationDate"))
                        .add(Restrictions.lt("latestOpportunityRequestNotificationDate", baseline)))
                .add(Restrictions.eq("role.id", Authority.SYSTEM_ADMINISTRATOR)).add(Restrictions.eq("enabled", true))
                .add(Restrictions.eq("accountNonExpired", true)).add(Restrictions.eq("accountNonLocked", true))
                .add(Restrictions.eq("credentialsNonExpired", true)).list();
    }

    public List<User> getInterviewAdministrators(Application application) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(UserRole.class).setProjection(Projections.groupProperty("user"))
                .createAlias("actions", "applicationFormActionRequired", JoinType.INNER_JOIN)
                .createAlias("applicationFormActionRequired.action", "action", JoinType.INNER_JOIN).createAlias("role", "applicationRole", JoinType.INNER_JOIN)
                .add(Restrictions.eq("applicationForm", application)).add(Restrictions.ne("applicationRole.id", Authority.SYSTEM_ADMINISTRATOR))
                .add(Restrictions.eq("action.id", PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS)).list();
    }

    //TODO rewrite the query - HQL
    public List<User> getUsersInterestedInApplication(Application application) {
        return new ArrayList<User>();
    }

    //TODO rewrite the query - HQL
    public List<User> getUsersPotentiallyInterestedInApplication(Application application) {
        return new ArrayList<User>();
    }

    private Date getBaselineDate(Date seedDate) {
        DateTime baseline = new DateTime(seedDate);
        DateTime cleanBaseline = new DateTime(baseline.getYear(), baseline.getMonthOfYear(), baseline.getDayOfMonth(), 0, 0, 0);
        return cleanBaseline.toDate();
    }

    private Date getReminderBaseline(Date baseline, ReminderType type) {
        // TODO implement
        return new Date();
        // int reminderInterval = reminderIntervalDAO.getReminderInterval(ReminderType.TASK).getDurationInDays();
        // return DateUtils.addDays((Date) baseline.clone(), -reminderInterval);
    }

    private Date getExpiryBaseline(Date baseline) {
        // TODO implement
        return new Date();
        // int expiryInterval = notificationsDurationDAO.getNotificationsDuration().getDurationInDays();
        // return DateUtils.addDays((Date) baseline.clone(), -expiryInterval);
    }

}
