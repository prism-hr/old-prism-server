package com.zuehlke.pgadmissions.services;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormListDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFilter;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;
import com.zuehlke.pgadmissions.dto.ApplicationActionsDefinition;

@Service("applicationsService")
@Transactional
public class ApplicationsService {

	public static final int APPLICATION_BLOCK_SIZE = 50;

	private final ApplicationFormDAO applicationFormDAO;

	private final ApplicationFormListDAO applicationFormListDAO;

	public ApplicationsService() {
		this(null, null);
	}

	@Autowired
	public ApplicationsService(ApplicationFormDAO applicationFormDAO, ApplicationFormListDAO applicationFormListDAO) {
		this.applicationFormDAO = applicationFormDAO;
		this.applicationFormListDAO = applicationFormListDAO;
	}

	public ApplicationForm getApplicationById(Integer id) {
		return applicationFormDAO.get(id);
	}

	public ApplicationForm getApplicationByApplicationNumber(String applicationNumber) {
		return applicationFormDAO.getApplicationByApplicationNumber(applicationNumber);
	}

	public void save(ApplicationForm application) {
		applicationFormDAO.save(application);
	}

	public ApplicationForm createOrGetUnsubmittedApplicationForm(RegisteredUser user, Program program,
			Date programDeadline, String projectTitle, String researchHomePage) {

		ApplicationForm applicationForm = findMostRecentApplication(user, program);
		if (applicationForm != null) {
			return applicationForm;
		}

		String thisYear = new SimpleDateFormat("yyyy").format(new Date());

		applicationForm = new ApplicationForm();
		applicationForm.setApplicant(user);
		applicationForm.setProgram(program);
		applicationForm.setBatchDeadline(programDeadline);

		applicationForm.setProjectTitle(projectTitle);
		applicationForm.setResearchHomePage(researchHomePage);
		Long runningCount = applicationFormDAO.getApplicationsInProgramThisYear(program, thisYear);
		applicationForm.setApplicationNumber(program.getCode() + "-" + thisYear + "-"
				+ String.format("%06d", ++runningCount));
		applicationFormDAO.save(applicationForm);
		return applicationForm;
	}

	private ApplicationForm findMostRecentApplication(RegisteredUser user, Program program) {
		List<ApplicationForm> applications = applicationFormDAO.getApplicationsByApplicantAndProgram(user, program);

		Iterable<ApplicationForm> filteredApplications = Iterables.filter(applications,
				new Predicate<ApplicationForm>() {
					@Override
					public boolean apply(ApplicationForm applicationForm) {
						return !applicationForm.isDecided() && !applicationForm.isWithdrawn();
					}
				});

		// first order by applications status, the by last updated
		@SuppressWarnings("unchecked")
		Ordering<ApplicationForm> ordering = Ordering//
				.from(new BeanComparator("status"))//
				.compound(new Comparator<ApplicationForm>() {
					@Override
					public int compare(ApplicationForm o1, ApplicationForm o2) {
						Date date1 = o1.getLastUpdated() != null ? o1.getLastUpdated() : o1.getApplicationTimestamp();
						Date date2 = o2.getLastUpdated() != null ? o2.getLastUpdated() : o2.getApplicationTimestamp();
						return date1.compareTo(date2);
					}
				});

		List<ApplicationForm> sortedApplications = ordering.sortedCopy(filteredApplications);

		return Iterables.getLast(sortedApplications, null);
	}

	public void makeApplicationNotEditable(ApplicationForm applicationForm) {
		applicationForm.setIsEditableByApplicant(false);
	}

	public List<ApplicationForm> getApplicationsDueUpdateNotification() {
		return applicationFormDAO.getApplicationsDueUpdateNotification();
	}

	public List<ApplicationForm> getAllVisibleAndMatchedApplications(RegisteredUser user,
			SearchCategory searchCategory, String term, SortCategory sort, SortOrder order, Integer page) {
		// default values
		int pageCount = page == null ? 1 : page;
		SortCategory sortCategory = sort == null ? SortCategory.APPLICATION_DATE : sort;
		SortOrder sortOrder = order == null ? SortOrder.ASCENDING : order;
		if (pageCount < 0) {
			pageCount = 0;
		}
		return applicationFormListDAO.getVisibleApplications(user, searchCategory, term, sortCategory, sortOrder,
				pageCount, APPLICATION_BLOCK_SIZE);

    public ApplicationActionsDefinition getActionsDefinition(RegisteredUser user, ApplicationForm application) {
        ApplicationActionsDefinition actions = new ApplicationActionsDefinition();
        if (user.canSee(application)) {
            if (application.isUserAllowedToSeeAndEditAsAdministrator(user) || (user == application.getApplicant() && application.isModifiable())) {
                actions.addAction("view", "View / Edit");
            } else {
                actions.addAction("view", "View");
            }
        }

        if (user.hasAdminRightsOnApplication(application) && application.isInState("VALIDATION")) {
            actions.addAction("validate", "Validation");
        }
        if (user.hasAdminRightsOnApplication(application) && application.isInState("REVIEW")) {
            actions.addAction("validate", "Evaluate reviews");
        }

        if (user.hasAdminRightsOnApplication(application) && application.isInState("INTERVIEW")) {
            actions.addAction("validate", "Evaluate interview feedback");
        }

        if (user.hasAdminRightsOnApplication(application) || user.isViewerOfProgramme(application)) {
            actions.addAction("comment", "Comment");
        }

        if (user.isReviewerInLatestReviewRoundOfApplicationForm(application) && application.isInState("REVIEW")
                && !user.hasRespondedToProvideReviewForApplicationLatestRound(application)) {
            actions.addAction("review", "Add review");
            actions.setRequiresAttention(true);
        }
        if (user.isInterviewerOfApplicationForm(application) && application.isInState("INTERVIEW")
                && !user.hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(application)) {
            actions.addAction("interviewFeedback", "Add interview feedback");
            actions.setRequiresAttention(true);
        }
        if (user.isRefereeOfApplicationForm(application) && application.isSubmitted() && application.isModifiable()
                && !user.getRefereeForApplicationForm(application).hasResponded()) {
            actions.addAction("reference", "Add reference");
            actions.setRequiresAttention(true);
        }
        if (user == application.getApplicant() && !application.isDecided() && !application.isWithdrawn()) {
            actions.addAction("withdraw", "Withdraw");
        }
        if (user.hasAdminRightsOnApplication(application) && application.isPendingApprovalRestart()) {
            actions.addAction("restartApproval", "Approve");
            actions.setRequiresAttention(true);
        }

        if (user.isInRoleInProgram("APPROVER", application.getProgram()) && application.isInState("APPROVAL") && !application.isPendingApprovalRestart()) {
            Supervisor primarySupervisor = application.getLatestApprovalRound().getPrimarySupervisor();
            if (primarySupervisor != null && BooleanUtils.isTrue(primarySupervisor.getConfirmedSupervision())) {
                actions.addAction("validate", "Approve");
                actions.setRequiresAttention(true);
            }
        }

        if (application.isInState("APPROVAL")) {
            Supervisor primarySupervisor = application.getLatestApprovalRound().getPrimarySupervisor();
            if (primarySupervisor != null && user == primarySupervisor.getUser() && BooleanUtils.isNotTrue(primarySupervisor.getConfirmedSupervision())) {
                actions.addAction("confirmSupervision", "Confirm supervision");
                actions.setRequiresAttention(true);
            }
        }

        return actions;
	}

	public void refresh(ApplicationForm applicationForm) {
		applicationFormDAO.refresh(applicationForm);
	}

	public List<ApplicationForm> getApplicationsDueRegistryNotification() {
		return applicationFormDAO.getApplicationsDueRegistryNotification();
	}

	public List<ApplicationForm> getApplicationsDueApprovalRestartRequestNotification() {
		return applicationFormDAO.getApplicationsDueApprovalRequestNotification();
	}

	public List<ApplicationForm> getApplicationsDueApprovalRestartRequestReminder() {
		return applicationFormDAO.getApplicationDueApprovalRestartRequestReminder();
	}

	public List<ApplicationForm> getAllApplicationsByStatus(ApplicationFormStatus status) {
		return applicationFormDAO.getAllApplicationsByStatus(status);
	}

}
