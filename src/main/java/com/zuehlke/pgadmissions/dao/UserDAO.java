package com.zuehlke.pgadmissions.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Repository
public class UserDAO {

	private final SessionFactory sessionFactory;

	UserDAO() {
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
		return (RegisteredUser) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).add(Restrictions.eq("username", username))
				.add(Restrictions.eq("enabled", true)).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<RegisteredUser> getAllUsers() {
		return sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	public List<RegisteredUser> getUsersInRole(Role role) {
		return sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).createCriteria("roles").add(Restrictions.eq("id", role.getId())).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

	}

	public RegisteredUser getUserByActivationCode(String activationCode) {
		return (RegisteredUser) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).add(Restrictions.eq("activationCode", activationCode))
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<RegisteredUser> getUsersForProgram(Program program) {
		List<RegisteredUser> users = new ArrayList<RegisteredUser>();
		users.addAll(getUsersInRole((Role) sessionFactory.getCurrentSession().createCriteria(Role.class)
				.add(Restrictions.eq("authorityEnum", Authority.SUPERADMINISTRATOR)).uniqueResult()));

		List<RegisteredUser> administrators = sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
				.createCriteria("programsOfWhichAdministrator").add(Restrictions.eq("id", program.getId())).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		for (RegisteredUser admin : administrators) {
			if (!users.contains(admin)) {
				users.add(admin);
			}
		}
		List<RegisteredUser> approvers = sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).createCriteria("programsOfWhichApprover")
				.add(Restrictions.eq("id", program.getId())).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		for (RegisteredUser approver : approvers) {
			if (!users.contains(approver)) {
				users.add(approver);
			}
		}
		List<RegisteredUser> reviewers = sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).createCriteria("programsOfWhichReviewer")
				.add(Restrictions.eq("id", program.getId())).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		for (RegisteredUser reviewer : reviewers) {
			if (!users.contains(reviewer)) {
				users.add(reviewer);
			}
		}
		List<RegisteredUser> interviewers = sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
				.createCriteria("programsOfWhichInterviewer").add(Restrictions.eq("id", program.getId())).list();
		for (RegisteredUser interviewer : interviewers) {
			if (!users.contains(interviewer)) {
				users.add(interviewer);
			}
		}
		List<RegisteredUser> supervisors = sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class)
				.createCriteria("programsOfWhichSupervisor").add(Restrictions.eq("id", program.getId())).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		for (RegisteredUser supervisor : supervisors) {
			if (!users.contains(supervisor)) {
				users.add(supervisor);
			}
		}
		return users;
	}

	public RegisteredUser getUserByEmail(String email) {
		return (RegisteredUser) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).add(Restrictions.eq("enabled", true))
				.add(Restrictions.eq("email", email)).uniqueResult();
	}

	public RegisteredUser getUserByEmailIncludingDisabledAccounts(String email) {
		return (RegisteredUser) sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class).add(Restrictions.eq("email", email)).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<RegisteredUser> getInternalUsers() {
		return sessionFactory
				.getCurrentSession()
				.createCriteria(RegisteredUser.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.createAlias("roles", "role")
				.add(Restrictions.and(Restrictions.not(Restrictions.eq("role.authorityEnum", Authority.APPLICANT)),
						Restrictions.not(Restrictions.eq("role.authorityEnum", Authority.REFEREE)))).addOrder(Order.asc("firstName"))
				.addOrder(Order.asc("lastName")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	public List<RegisteredUser> getUsersWithPendingRoleNotifications() {	
		
		return sessionFactory.getCurrentSession().createCriteria(RegisteredUser.class, "user").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).add(Restrictions.eq("enabled", false))
				.createAlias("pendingRoleNotifications", "pendingRoleNotification").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	public List<RegisteredUser> getAllPreviousInterviewersOfProgram(Program program) {
		List<Interviewer> interviewers = sessionFactory.getCurrentSession().createCriteria(Interviewer.class).createAlias("interview", "interview").createAlias("interview.application", "application")
				.add(Restrictions.eq("application.program", program)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		List<RegisteredUser> users = new ArrayList<RegisteredUser>();
		for (Interviewer interviewer : interviewers) {
			if(!users.contains(interviewer.getUser())){
				users.add(interviewer.getUser());
			}
		}
		return users;
	}
	
	@SuppressWarnings("unchecked")
	public List<RegisteredUser> getAllPreviousReviewersOfProgram(Program program) {
		List<Reviewer> reviewers = sessionFactory.getCurrentSession().createCriteria(Reviewer.class).createAlias("reviewRound", "reviewRound").createAlias("reviewRound.application", "application")
				.add(Restrictions.eq("application.program", program)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		List<RegisteredUser> users = new ArrayList<RegisteredUser>();
		for (Reviewer reviewer : reviewers) {
			if(!users.contains(reviewer.getUser())){
				users.add(reviewer.getUser());
			}
		}
		return users;
	}
	@SuppressWarnings("unchecked")
	public List<RegisteredUser> getReviewersWillingToInterview(ApplicationForm applicationForm) {
		List<ReviewComment> reviews = sessionFactory.getCurrentSession().createCriteria(ReviewComment.class).createAlias("reviewer", "reviewer")
				.createAlias("reviewer.reviewRound", "reviewRound").createAlias("reviewRound.application", "application")
				.add(Restrictions.eq("application", applicationForm))
				.add(Restrictions.eqProperty("application.latestReviewRound","reviewer.reviewRound"))
				.add(Restrictions.eq("willingToInterview", true)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		List<RegisteredUser> users = new ArrayList<RegisteredUser>();
		for (ReviewComment reviewComment : reviews) {
			if(!users.contains(reviewComment.getUser())){
				users.add(reviewComment.getUser());
			}
		}
		return users;
	}

	@SuppressWarnings("unchecked")
	public List<RegisteredUser> getAllPreviousSupervisorsOfProgram(Program program) {
		List<Supervisor> supervisors = sessionFactory.getCurrentSession().createCriteria(Supervisor.class).createAlias("approvalRound", "approvalRound").createAlias("approvalRound.application", "application")
				.add(Restrictions.eq("application.program", program)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		List<RegisteredUser> users = new ArrayList<RegisteredUser>();
		for (Supervisor supervisor : supervisors) {
			if(!users.contains(supervisor.getUser())){
				users.add(supervisor.getUser());
			}
		}
		return users;
	}

}
