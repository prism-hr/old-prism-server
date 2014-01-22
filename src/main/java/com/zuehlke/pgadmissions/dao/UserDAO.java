package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.NotificationMethod;
import com.zuehlke.pgadmissions.domain.enums.ReminderType;

@Repository
@SuppressWarnings("unchecked")
public class UserDAO {

    private final SessionFactory sessionFactory;

    private final ReminderIntervalDAO reminderIntervalDAO;

    private final NotificationsDurationDAO notificationsDurationDAO;

    public UserDAO() {
        this(null, null, null);
    }

    @Autowired
    public UserDAO(SessionFactory sessionFactory, ReminderIntervalDAO reminderIntervalDAO, NotificationsDurationDAO notificationsDurationDAO) {
        this.sessionFactory = sessionFactory;
        this.reminderIntervalDAO = reminderIntervalDAO;
        this.notificationsDurationDAO = notificationsDurationDAO;
    }

    public void save(RegisteredUser user) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(user);
        session.flush();
    }

    public RegisteredUser get(Integer id) {
        return (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id);
    }

    public RegisteredUser getUserByUsername(String username) {
        return (RegisteredUser) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).add(Restrictions.eq("username", username))
                .add(Restrictions.eq("enabled", true)).uniqueResult();
    }

    public List<RegisteredUser> getAllUsers() {
        return sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<RegisteredUser> getUsersInRole(Role role) {
        return sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).createCriteria("roles").add(Restrictions.eq("id", role.getId()))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public RegisteredUser getUserByActivationCode(String activationCode) {
        return (RegisteredUser) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).add(Restrictions.eq("activationCode", activationCode))
                .uniqueResult();
    }

    public Long getNumberOfActiveApplicationsForApplicant(final RegisteredUser applicant) {
        return (Long) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class).add(Restrictions.eq("applicant", applicant))
                .add(Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.APPROVED)))
                .add(Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.REJECTED)))
                .add(Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.WITHDRAWN))).setProjection(Projections.rowCount()).uniqueResult();
    }

    public List<RegisteredUser> getUsersWithUpi(final String upi) {
        return (List<RegisteredUser>) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).add(Restrictions.eq("upi", upi)).list();
    }

    public List<RegisteredUser> getUsersForProgram(Program program) {
        final Map<Integer, RegisteredUser> users = new HashMap<Integer, RegisteredUser>();

        Criteria superAdminRoleCriteria = sessionFactory.getCurrentSession().createCriteria(Role.class)
                .add(Restrictions.eq("id", Authority.SUPERADMINISTRATOR));

        Criteria programsOfWhichAdministratorCriteria = sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
                .createCriteria("programsOfWhichAdministrator").add(Restrictions.eq("id", program.getId()));

        Criteria programsOfWhichApprover = sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).createCriteria("programsOfWhichApprover")
                .add(Restrictions.eq("id", program.getId()));

        Criteria programsOfWhichViewer = sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).createCriteria("programsOfWhichViewer")
                .add(Restrictions.eq("id", program.getId()));

        CollectionUtils.forAllDo(getUsersInRole((Role) superAdminRoleCriteria.uniqueResult()), new Closure() {
            @Override
            public void execute(Object target) {
                RegisteredUser user = (RegisteredUser) target;
                users.put(user.getId(), user);
            }
        });

        CollectionUtils.forAllDo(programsOfWhichAdministratorCriteria.list(), new Closure() {
            @Override
            public void execute(Object target) {
                RegisteredUser user = (RegisteredUser) target;
                users.put(user.getId(), user);
            }
        });

        CollectionUtils.forAllDo(programsOfWhichApprover.list(), new Closure() {
            @Override
            public void execute(Object target) {
                RegisteredUser user = (RegisteredUser) target;
                users.put(user.getId(), user);
            }
        });

        CollectionUtils.forAllDo(programsOfWhichViewer.list(), new Closure() {
            @Override
            public void execute(Object target) {
                RegisteredUser user = (RegisteredUser) target;
                users.put(user.getId(), user);
            }
        });

        return new ArrayList<RegisteredUser>(users.values());
    }

    public RegisteredUser getUserByEmail(String email) {
        return (RegisteredUser) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).add(Restrictions.eq("enabled", true))
                .add(Restrictions.eq("email", email)).uniqueResult();
    }

    public RegisteredUser getDisabledUserByEmail(String email) {
        return (RegisteredUser) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).add(Restrictions.eq("enabled", false))
                .add(Restrictions.eq("email", email)).uniqueResult();
    }

    public RegisteredUser getUserByEmailIncludingDisabledAccounts(String email) {
        return (RegisteredUser) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).add(Restrictions.eq("email", email)).uniqueResult();
    }

    public List<RegisteredUser> getInternalUsers() {
        return sessionFactory
                .getCurrentSession()
                .createCriteria(RegisteredUser.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .createAlias("roles", "role")
                .add(Restrictions.and(Restrictions.not(Restrictions.eq("role.id", Authority.APPLICANT)),
                        Restrictions.not(Restrictions.eq("role.id", Authority.REFEREE)))).addOrder(Order.asc("firstName")).addOrder(Order.asc("lastName"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<Integer> getUsersIdsWithPendingRoleNotifications() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class, "user")
                .setProjection(Projections.distinct(Projections.property("id"))).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .add(Restrictions.eq("enabled", false)).createAlias("pendingRoleNotifications", "pendingRoleNotification")
                .add(Restrictions.isNull("pendingRoleNotification.notificationDate")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<RegisteredUser> getSuperadministrators() {
        return sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).createAlias("roles", "role")
                .add(Restrictions.eq("role.id", Authority.SUPERADMINISTRATOR)).list();
    }

    public List<RegisteredUser> getAdmitters() {
        return sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).createAlias("roles", "role")
                .add(Restrictions.eq("role.id", Authority.ADMITTER)).list();
    }
    
    public void setApplicationFormListLastAccessTimestamp(RegisteredUser user) {
        user.setApplicationListLastAccessTimestamp(new Date());
        save(user);
    }
    
    public List<RegisteredUser> getUsersDueTaskReminder() {
        Date baseline = new Date();
        Date reminderBaseline = getReminderBaseline(baseline);
        Date expiryBaseline = getExpiryBaseline(baseline);
        
        HashSet<RegisteredUser> deduplicatedUsers = new HashSet<RegisteredUser>();
        
        List<RegisteredUser> applicationUsers = (List<RegisteredUser>) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.groupProperty("user"))
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .createAlias("actions", "applicationFormActionRequired", JoinType.INNER_JOIN)
                .createAlias("applicationFormActionRequired.action", "action", JoinType.INNER_JOIN)
                .add(Restrictions.eq("action.notification", NotificationMethod.SYNDICATED))
                .add(Restrictions.eq("raisesUrgentFlag", true))
                .add(Restrictions.eq("user.latestTaskNotificationDate", reminderBaseline))
                .add(Restrictions.gt("applicationFormActionRequired.deadlineTimestamp", expiryBaseline)).list();
        
        deduplicatedUsers.addAll(applicationUsers);
        return new ArrayList<RegisteredUser>(deduplicatedUsers);
    }
    
    public List<RegisteredUser> getUsersDueTaskNotification() {
        Date baseline = new Date();
        Date reminderBaseline = getReminderBaseline(baseline);
        Date expiryBaseline = getExpiryBaseline(baseline);
        
        HashSet<RegisteredUser> deduplicatedUsers = new HashSet<RegisteredUser>();
        
        List<RegisteredUser> applicationUsers = (List<RegisteredUser>) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.groupProperty("user"))
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .createAlias("actions", "applicationFormActionRequired", JoinType.INNER_JOIN)
                .createAlias("applicationFormActionRequired.action", "action", JoinType.INNER_JOIN)
                .add(Restrictions.eq("action.notification", NotificationMethod.SYNDICATED))
                .add(Restrictions.eq("raisesUrgentFlag", true))
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("user.latestTaskNotificationDate", null))
                        .add(Restrictions.lt("user.latestTaskNotificationDate", reminderBaseline)))
                .add(Restrictions.gt("applicationFormActionRequired.deadlineTimestamp", expiryBaseline)).list();
        
        deduplicatedUsers.addAll(applicationUsers);
        return new ArrayList<RegisteredUser>(deduplicatedUsers);
    }
    
    public List<RegisteredUser> getUsersDueUpdateNotification() {
        Date baseline = new Date();
        
        HashSet<RegisteredUser> deduplicatedUsers = new HashSet<RegisteredUser>();
        
        List<RegisteredUser> applicationUsers = (List<RegisteredUser>) sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
                .setProjection(Projections.groupProperty("user"))
                .createAlias("user", "registeredUser", JoinType.INNER_JOIN)
                .createAlias("role", "role", JoinType.INNER_JOIN)
                .add(Restrictions.eq("role.doSendUpdateNotification", true))
                .add(Restrictions.eq("raisesUpdateFlag", true))
                .add(Restrictions.eq("updateTimestamp", baseline))
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("user.latestUpdateNotificationDate", null))
                        .add(Restrictions.lt("user.latestUpdateNotificationDate", baseline))).list();
        
        deduplicatedUsers.addAll(applicationUsers);
        return new ArrayList<RegisteredUser>(deduplicatedUsers);
    }
    
    private Date getReminderBaseline(Date baseline) {
        int reminderInterval = reminderIntervalDAO.getReminderInterval(ReminderType.TASK).getDurationInDays();
        return DateUtils.addDays((Date) baseline.clone(), -reminderInterval);
    }
    
    private Date getExpiryBaseline(Date baseline) {
        int expiryInterval = notificationsDurationDAO.getNotificationsDuration().getDurationInDays();
        return DateUtils.addDays((Date) baseline.clone(), -expiryInterval);
    }

}