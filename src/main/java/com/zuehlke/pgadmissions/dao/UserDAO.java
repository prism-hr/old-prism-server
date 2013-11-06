package com.zuehlke.pgadmissions.dao;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.google.common.io.CharStreams;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.NotificationsDuration;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
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
        sessionFactory.getCurrentSession().saveOrUpdate(user);
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

    public List<RegisteredUser> getAllPreviousInterviewersOfProgram(Program program) {
        List<Interviewer> interviewers = sessionFactory.getCurrentSession().createCriteria(Interviewer.class).createAlias("interview", "interview")
                .createAlias("interview.application", "application").add(Restrictions.eq("application.program", program))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        List<RegisteredUser> users = new ArrayList<RegisteredUser>();
        for (Interviewer interviewer : interviewers) {
            if (!listContainsId(interviewer.getUser(), users)) {
                users.add(interviewer.getUser());
            }
        }
        return users;
    }

    public List<RegisteredUser> getAllPreviousReviewersOfProgram(Program program) {
        List<Reviewer> reviewers = sessionFactory.getCurrentSession().createCriteria(Reviewer.class).createAlias("reviewRound", "reviewRound")
                .createAlias("reviewRound.application", "application").add(Restrictions.eq("application.program", program))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        List<RegisteredUser> users = new ArrayList<RegisteredUser>();
        for (Reviewer reviewer : reviewers) {
            if (!listContainsId(reviewer.getUser(), users)) {
                users.add(reviewer.getUser());
            }
        }
        return users;
    }

    public List<RegisteredUser> getReviewersWillingToInterview(ApplicationForm applicationForm) {
        List<ReviewComment> reviews = sessionFactory.getCurrentSession().createCriteria(ReviewComment.class).createAlias("reviewer", "reviewer")
                .createAlias("reviewer.reviewRound", "reviewRound").createAlias("reviewRound.application", "application")
                .add(Restrictions.eq("application", applicationForm)).add(Restrictions.eqProperty("application.latestReviewRound", "reviewer.reviewRound"))
                .add(Restrictions.eq("willingToInterview", true)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        List<RegisteredUser> users = new ArrayList<RegisteredUser>();
        for (ReviewComment reviewComment : reviews) {
            if (!listContainsId(reviewComment.getUser(), users)) {
                users.add(reviewComment.getUser());
            }
        }
        return users;
    }

    public List<RegisteredUser> getAllPreviousSupervisorsOfProgram(Program program) {
        List<Supervisor> supervisors = sessionFactory.getCurrentSession().createCriteria(Supervisor.class).createAlias("approvalRound", "approvalRound")
                .createAlias("approvalRound.application", "application").add(Restrictions.eq("application.program", program))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        List<RegisteredUser> users = new ArrayList<RegisteredUser>();
        for (Supervisor supervisor : supervisors) {
            if (!listContainsId(supervisor.getUser(), users)) {
                users.add(supervisor.getUser());
            }
        }
        return users;
    }

    public List<Integer> getPotentialUsersForTaskNotification() {
    	updateApplicationFormActionUrgentFlag();
        ReminderInterval reminderInterval = reminderIntervalDAO.getReminderInterval(ReminderType.TASK);
        int interval = reminderInterval.getDuration();
        DurationUnitEnum unit = reminderInterval.getUnit();
        String sqlQuery = StringUtils.replace(getPotentialUsersDueToTaskNotificationSql, "${TIME_UNIT}", unit.sqlValue());
        return sessionFactory.getCurrentSession().createSQLQuery(sqlQuery).setParameter("interval", interval).list();
    }

    public List<Integer> getPotentialUsersForTaskReminder() {
    	updateApplicationFormActionUrgentFlag();
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
    	updateApplicationFormActionUrgentFlag();
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

    private boolean listContainsId(RegisteredUser user, List<RegisteredUser> users) {
        for (RegisteredUser entry : users) {
            if (entry.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }
    
    private void updateApplicationFormActionUrgentFlag() {
    	sessionFactory.getCurrentSession().createSQLQuery(
    			"UPDATE APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE " +
    			"ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id " +
    			"SET APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag = 1, " +
    			"APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 1 " +
    			"WHERE deadline_timestamp < CURRENT_DATE()");
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

}
