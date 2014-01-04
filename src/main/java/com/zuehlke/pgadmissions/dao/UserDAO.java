package com.zuehlke.pgadmissions.dao;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.google.common.io.CharStreams;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.NotificationsDuration;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityGroup;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.ReminderType;

@Repository
@SuppressWarnings("unchecked")
public class UserDAO {

    private final SessionFactory sessionFactory;

    private final ReminderIntervalDAO reminderIntervalDAO;

    private final NotificationsDurationDAO notificationsDurationDAO;

    private final ApplicationContext applicationContext;

    private String getPotentialUsersDueToTaskReminderSql;

    private String getPotentialUsersDueToTaskNotificationSql;

    private String getUsersDueToUpdateNotificationSql;

    public UserDAO() {
        this(null, null, null, null);
    }

    @Autowired
    public UserDAO(SessionFactory sessionFactory, ReminderIntervalDAO reminderIntervalDAO, NotificationsDurationDAO notificationsDurationDAO,
            ApplicationContext applicationContext) {
        this.sessionFactory = sessionFactory;
        this.reminderIntervalDAO = reminderIntervalDAO;
        this.notificationsDurationDAO = notificationsDurationDAO;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void setup() throws IOException {
        InputStreamReader reader = new InputStreamReader(applicationContext.getResource("classpath:sql/get_potential_users_due_to_task_reminder.sql")
                .getInputStream());
        getPotentialUsersDueToTaskReminderSql = CharStreams.toString(reader);
        reader = new InputStreamReader(applicationContext.getResource("classpath:sql/get_potential_users_due_to_task_notification.sql").getInputStream());
        getPotentialUsersDueToTaskNotificationSql = CharStreams.toString(reader);
        reader = new InputStreamReader(applicationContext.getResource("classpath:sql/get_users_due_to_update_notification.sql").getInputStream());
        getUsersDueToUpdateNotificationSql = CharStreams.toString(reader);
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

    public List<Integer> getPotentialUsersForTaskNotification() {
        ReminderInterval reminderInterval = reminderIntervalDAO.getReminderInterval(ReminderType.TASK);
        int interval = reminderInterval.getDuration();
        DurationUnitEnum unit = reminderInterval.getUnit();
        String sqlQuery = StringUtils.replace(getPotentialUsersDueToTaskNotificationSql, "${TIME_UNIT}", unit.sqlValue());
        return sessionFactory.getCurrentSession().createSQLQuery(sqlQuery).setParameter("interval", interval).list();
    }

    public List<Integer> getPotentialUsersForTaskReminder() {
        ReminderInterval reminderInterval = reminderIntervalDAO.getReminderInterval(ReminderType.TASK);
        NotificationsDuration notificationsDurationObj = notificationsDurationDAO.getNotificationsDuration();
        int notificationsDuration = notificationsDurationObj.getDurationInDays();
        int interval = reminderInterval.getDuration();
        DurationUnitEnum unit = reminderInterval.getUnit();
        String sqlQuery = StringUtils.replace(getPotentialUsersDueToTaskReminderSql, "${TIME_UNIT}", unit.sqlValue());
        return sessionFactory.getCurrentSession().createSQLQuery(sqlQuery).setParameter("interval", interval)
                .setParameter("notificationsDuration", notificationsDuration).list();
    }

    public List<Integer> getUsersForUpdateNotification() {
        return sessionFactory.getCurrentSession().createSQLQuery(getUsersDueToUpdateNotificationSql).list();
    }

    public List<RegisteredUser> getSuperadministrators() {
        return sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).createAlias("roles", "role")
                .add(Restrictions.eq("role.id", Authority.SUPERADMINISTRATOR)).list();
    }

    public List<RegisteredUser> getAdmitters() {
        return sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).createAlias("roles", "role")
                .add(Restrictions.eq("role.id", Authority.ADMITTER)).list();
    }

    /* package */void setGetPotentialUsersDueToTaskReminderSql(String getPotentialUsersDueToTaskReminderSql) {
        this.getPotentialUsersDueToTaskReminderSql = getPotentialUsersDueToTaskReminderSql;
    }

    /* package */void setGetPotentialUsersDueToTaskNotificationSql(String getPotentialUsersDueToTaskNotificationSql) {
        this.getPotentialUsersDueToTaskNotificationSql = getPotentialUsersDueToTaskNotificationSql;
    }

    /* package */void setGetUsersDueToUpdateNotificationSql(String getUsersDueToUpdateNotificationSql) {
        this.getUsersDueToUpdateNotificationSql = getUsersDueToUpdateNotificationSql;
    }
    
	public List<RegisteredUser> findUsersInterestedInApplication(ApplicationForm applicationForm) {
		return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
				.setProjection(Projections.projectionList()
					.add(Projections.groupProperty("user"), "user"))
				.createAlias("user", "registeredUser", JoinType.INNER_JOIN)
				.add(Restrictions.eq("applicationForm", applicationForm))
				.add(Restrictions.eq("interestedInApplicant", true))
				.add(Restrictions.disjunction()
						.add(Restrictions.eq("registeredUser.enabled", true))
						.add(Restrictions.conjunction()
							.add(Restrictions.eq("registeredUser.enabled", false))
							.add(Restrictions.in("role.id", Arrays.asList(Authority.SUGGESTEDSUPERVISOR)))))
				.addOrder(Order.asc("registeredUser.lastName"))
				.addOrder(Order.asc("registeredUser.firstName"))
				.addOrder(Order.asc("registeredUser.id")).list();
	}

	public List<RegisteredUser> findUsersPotentiallyInterestedInApplication(ApplicationForm applicationForm) {
		DetachedCriteria usersInterestedInApplicant = DetachedCriteria.forClass(ApplicationFormUserRole.class)
				.setProjection(Projections.projectionList()
					.add(Projections.groupProperty("user")))
				.add(Restrictions.eq("applicationForm", applicationForm))
				.add(Restrictions.eq("interestedInApplicant", true));
		
		return sessionFactory.getCurrentSession().createCriteria(ApplicationFormUserRole.class)
				.setProjection(Projections.projectionList()
					.add(Projections.groupProperty("user"), "user"))
				.createAlias("applicationForm", "applicationForm", JoinType.INNER_JOIN)
				.createAlias("applicationForm.program", "program", JoinType.INNER_JOIN)
				.createAlias("user", "registeredUser", JoinType.INNER_JOIN)
				.add(Restrictions.eq("program.id", applicationForm.getProgram().getId()))
				.add(Restrictions.in("role.id", AuthorityGroup.POTENTIALSUPERVISOR.authorities()))
				.add(Restrictions.isNull("registeredUser.primaryAccount"))
				.add(Restrictions.eq("registeredUser.enabled", true))
				.add(Property.forName("user").notIn(usersInterestedInApplicant))
				.addOrder(Order.asc("registeredUser.lastName"))
				.addOrder(Order.asc("registeredUser.firstName"))
				.addOrder(Order.asc("registeredUser.id")).list();
	}

}