package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DigestNotificationType;

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

    public void save(RegisteredUser user) {
        sessionFactory.getCurrentSession().saveOrUpdate(user);
    }

    public RegisteredUser get(Integer id) {
        return (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, id);
    }

    public RegisteredUser getUserByUsername(String username) {
        return (RegisteredUser) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
                .add(Restrictions.eq("username", username)).add(Restrictions.eq("enabled", true)).uniqueResult();
    }

    public List<RegisteredUser> getAllUsers() {
        return sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<RegisteredUser> getUsersInRole(Role role) {
        return sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).createCriteria("roles")
                .add(Restrictions.eq("id", role.getId())).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public RegisteredUser getUserByActivationCode(String activationCode) {
        return (RegisteredUser) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
                .add(Restrictions.eq("activationCode", activationCode)).uniqueResult();
    }
    
    public Long getNumberOfActiveApplicationsForApplicant(final RegisteredUser applicant) {
        return (Long) sessionFactory.getCurrentSession().createCriteria(ApplicationForm.class)
                .add(Restrictions.eq("applicant", applicant))
                .add(Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.APPROVED)))
                .add(Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.REJECTED)))
                .add(Restrictions.not(Restrictions.eq("status", ApplicationFormStatus.WITHDRAWN)))
                .setProjection(Projections.rowCount()).uniqueResult();
    }

    public List<RegisteredUser> getUsersForProgram(Program program) {
        final Map<Integer, RegisteredUser> users = new HashMap<Integer, RegisteredUser>();

        Criteria superAdminRoleCriteria = sessionFactory.getCurrentSession().createCriteria(Role.class)
                .add(Restrictions.eq("authorityEnum", Authority.SUPERADMINISTRATOR));

        Criteria programsOfWhichAdministratorCriteria = sessionFactory.getCurrentSession()
                .createCriteria(RegisteredUser.class).createCriteria("programsOfWhichAdministrator")
                .add(Restrictions.eq("id", program.getId()));

        Criteria programsOfWhichApprover = sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
                .createCriteria("programsOfWhichApprover").add(Restrictions.eq("id", program.getId()));

        Criteria programsOfWhichReviewer = sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
                .createCriteria("programsOfWhichReviewer").add(Restrictions.eq("id", program.getId()));

        Criteria programsOfWhichInterviewer = sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
                .createCriteria("programsOfWhichInterviewer").add(Restrictions.eq("id", program.getId()));

        Criteria programsOfWhichSupervisor = sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
                .createCriteria("programsOfWhichSupervisor").add(Restrictions.eq("id", program.getId()));

        Criteria programsOfWhichViewer = sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
                .createCriteria("programsOfWhichViewer").add(Restrictions.eq("id", program.getId()));

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

        CollectionUtils.forAllDo(programsOfWhichReviewer.list(), new Closure() {
            @Override
            public void execute(Object target) {
                RegisteredUser user = (RegisteredUser) target;
                users.put(user.getId(), user);
            }
        });

        CollectionUtils.forAllDo(programsOfWhichInterviewer.list(), new Closure() {
            @Override
            public void execute(Object target) {
                RegisteredUser user = (RegisteredUser) target;
                users.put(user.getId(), user);
            }
        });

        CollectionUtils.forAllDo(programsOfWhichSupervisor.list(), new Closure() {
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
        return (RegisteredUser) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
                .add(Restrictions.eq("enabled", true)).add(Restrictions.eq("email", email)).uniqueResult();
    }

    public RegisteredUser getDisabledUserByEmail(String email) {
        return (RegisteredUser) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
                .add(Restrictions.eq("enabled", false)).add(Restrictions.eq("email", email)).uniqueResult();
    }

    public RegisteredUser getUserByEmailIncludingDisabledAccounts(String email) {
        return (RegisteredUser) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
                .add(Restrictions.eq("email", email)).uniqueResult();
    }

    public List<RegisteredUser> getInternalUsers() {
        return sessionFactory
                .getCurrentSession()
                .createCriteria(RegisteredUser.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .createAlias("roles", "role")
                .add(Restrictions.and(Restrictions.not(Restrictions.eq("role.authorityEnum", Authority.APPLICANT)),
                        Restrictions.not(Restrictions.eq("role.authorityEnum", Authority.REFEREE))))
                .addOrder(Order.asc("firstName")).addOrder(Order.asc("lastName"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<RegisteredUser> getUsersWithPendingRoleNotifications() {
        return sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class, "user")
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).add(Restrictions.eq("enabled", false))
                .createAlias("pendingRoleNotifications", "pendingRoleNotification")
                .add(Restrictions.isNull("pendingRoleNotification.notificationDate"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }
    
    public List<Integer> getUsersIdsWithPendingRoleNotifications() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class, "user").setProjection(Projections.distinct(Projections.property("id")))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).add(Restrictions.eq("enabled", false))
                .createAlias("pendingRoleNotifications", "pendingRoleNotification")
                .add(Restrictions.isNull("pendingRoleNotification.notificationDate"))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<RegisteredUser> getAllPreviousInterviewersOfProgram(Program program) {
        List<Interviewer> interviewers = sessionFactory.getCurrentSession().createCriteria(Interviewer.class)
                .createAlias("interview", "interview").createAlias("interview.application", "application")
                .add(Restrictions.eq("application.program", program))
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
        List<Reviewer> reviewers = sessionFactory.getCurrentSession().createCriteria(Reviewer.class)
                .createAlias("reviewRound", "reviewRound").createAlias("reviewRound.application", "application")
                .add(Restrictions.eq("application.program", program))
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
        List<ReviewComment> reviews = sessionFactory.getCurrentSession().createCriteria(ReviewComment.class)
                .createAlias("reviewer", "reviewer").createAlias("reviewer.reviewRound", "reviewRound")
                .createAlias("reviewRound.application", "application")
                .add(Restrictions.eq("application", applicationForm))
                .add(Restrictions.eqProperty("application.latestReviewRound", "reviewer.reviewRound"))
                .add(Restrictions.eq("willingToInterview", true)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
        List<RegisteredUser> users = new ArrayList<RegisteredUser>();
        for (ReviewComment reviewComment : reviews) {
            if (!listContainsId(reviewComment.getUser(), users)) {
                users.add(reviewComment.getUser());
            }
        }
        return users;
    }

    public List<RegisteredUser> getAllPreviousSupervisorsOfProgram(Program program) {
        List<Supervisor> supervisors = sessionFactory.getCurrentSession().createCriteria(Supervisor.class)
                .createAlias("approvalRound", "approvalRound").createAlias("approvalRound.application", "application")
                .add(Restrictions.eq("application.program", program))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        List<RegisteredUser> users = new ArrayList<RegisteredUser>();
        for (Supervisor supervisor : supervisors) {
            if (!listContainsId(supervisor.getUser(), users)) {
                users.add(supervisor.getUser());
            }
        }
        return users;
    }

    public void delete(final RegisteredUser user) {
        sessionFactory.getCurrentSession().delete(user);
    }
    
    public List<Integer> getAllUserIdsInNeedOfADigestNotification() {
        return (List<Integer>) sessionFactory
                .getCurrentSession()
                .createCriteria(RegisteredUser.class)
                .setProjection(Projections.property("id"))
                .add(Restrictions.or(
                        Restrictions.eq("digestNotificationType", DigestNotificationType.UPDATE_NOTIFICATION),
                        Restrictions.eq("digestNotificationType", DigestNotificationType.TASK_NOTIFICATION),
                        Restrictions.eq("digestNotificationType", DigestNotificationType.TASK_REMINDER)))
                        .list();
    }
    
    public void resetDigestNotificationsForAllUsers() {
        String hqlUpdate = "UPDATE com.zuehlke.pgadmissions.domain.RegisteredUser u SET u.digestNotificationType = :newType";
        sessionFactory.getCurrentSession().createQuery(hqlUpdate)
             .setString("newType", DigestNotificationType.NONE.toString())
             .executeUpdate();
    }

    private boolean listContainsId(RegisteredUser user, List<RegisteredUser> users) {
        for (RegisteredUser entry : users) {
            if (entry.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }
}
