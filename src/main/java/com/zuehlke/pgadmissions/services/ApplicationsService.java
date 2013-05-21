package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.enums.NotificationType.APPLICANT_SUBMISSION_NOTIFICATION;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormListDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationsPreFilter;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service("applicationsService")
@Transactional
public class ApplicationsService {

    private final Logger log = LoggerFactory.getLogger(ApplicationsService.class);

    public static final int APPLICATION_BLOCK_SIZE = 50;

    private final ApplicationFormDAO applicationFormDAO;

    private final ApplicationFormListDAO applicationFormListDAO;

    private final MailSendingService mailService;

    private final StateTransitionViewResolver stateTransitionViewResolver;

    public ApplicationsService() {
        this(null, null, null, null);
    }

    @Autowired
    public ApplicationsService(final ApplicationFormDAO applicationFormDAO, final ApplicationFormListDAO applicationFormListDAO,
            final MailSendingService mailService, final StateTransitionViewResolver stateTransitionViewResolver) {
        this.applicationFormDAO = applicationFormDAO;
        this.applicationFormListDAO = applicationFormListDAO;
        this.mailService = mailService;
        this.stateTransitionViewResolver = stateTransitionViewResolver;
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

    public ApplicationForm createOrGetUnsubmittedApplicationForm(final RegisteredUser user, final Program program, final Date programDeadline,
            final String projectTitle, final String researchHomePage) {

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
        applicationForm.setApplicationNumber(program.getCode() + "-" + thisYear + "-" + String.format("%06d", ++runningCount));
        applicationFormDAO.save(applicationForm);
        return applicationForm;
    }

    private ApplicationForm findMostRecentApplication(final RegisteredUser user, final Program program) {
        List<ApplicationForm> applications = applicationFormDAO.getApplicationsByApplicantAndProgram(user, program);

        Iterable<ApplicationForm> filteredApplications = Iterables.filter(applications, new Predicate<ApplicationForm>() {
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

    public List<ApplicationForm> getAllVisibleAndMatchedApplications(final RegisteredUser user, final ApplicationsFiltering filtering) {
        if (filtering.getPreFilter() == ApplicationsPreFilter.URGENT) {
            return applicationFormListDAO.getApplicationsWorthConsideringForAttentionFlag(user, filtering, APPLICATION_BLOCK_SIZE, this);
        } else {
            return applicationFormListDAO.getVisibleApplications(user, filtering, APPLICATION_BLOCK_SIZE);
        }
    }

    public void delegateInterviewAdministration(final ApplicationForm applicationForm, final RegisteredUser delegate) {
        applicationForm.setSuppressStateChangeNotifications(true);
        applicationForm.setApplicationAdministrator(delegate);
        applicationFormDAO.save(applicationForm);
    }

    public void sendSubmissionConfirmationToApplicant(final ApplicationForm applicationForm) {
        try {
            mailService.sendSubmissionConfirmationToApplicant(applicationForm);
            NotificationRecord notificationRecord = applicationForm.getNotificationForType(APPLICANT_SUBMISSION_NOTIFICATION);
            if (notificationRecord == null) {
                notificationRecord = new NotificationRecord(APPLICANT_SUBMISSION_NOTIFICATION);
                applicationForm.addNotificationRecord(notificationRecord);
            }
            notificationRecord.setDate(new Date());
            applicationFormDAO.save(applicationForm);
        } catch (Exception e) {
            log.warn("{}", e);
        }
    }

    public List<Integer> getApplicationsIdsDueRegistryNotification() {
        return applicationFormDAO.getApplicationsIdsDueRegistryNotification();
    }

    public void refresh(final ApplicationForm applicationForm) {
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

    public List<ApplicationForm> getAllApplicationsByStatus(final ApplicationFormStatus status) {
        return applicationFormDAO.getAllApplicationsByStatus(status);
    }

    public ActionsDefinitions calculateActions(final RegisteredUser user, final ApplicationForm application) {
        Interview interview = application.getLatestInterview();
        ApplicationFormStatus nextStatus = stateTransitionViewResolver.getNextStatus(application);

        ActionsDefinitions actions = new ActionsDefinitions();

        if (user.canEditAsAdministrator(application) || user.canEditAsApplicant(application)) {
            actions.addAction("view", "View / Edit");
        } else if (user.canSee(application)) {
            actions.addAction("view", "View");
        }

        if (application.isInState(ApplicationFormStatus.VALIDATION)) {
            if (user.hasAdminRightsOnApplication(application)) {
                actions.addAction("validate", "Validate");
            }
        }

        if (application.isInState(ApplicationFormStatus.REVIEW)) {
            if (user.hasAdminRightsOnApplication(application)) {
                actions.addAction("validate", "Evaluate reviews");
            }
        }

        if (application.isInState(ApplicationFormStatus.INTERVIEW) && nextStatus == null) {
            if (user.hasAdminRightsOnApplication(application)) {
                actions.addAction("validate", "Evaluate interview feedback");
            }
            if (interview.isScheduling() && (user.isApplicationAdministrator(application) || user.hasAdminRightsOnApplication(application))) {
                actions.addAction("interviewConfirm", "Confirm interview time");
                actions.setRequiresAttention(true);
            }
        }

        if (user.isApplicationAdministrator(application) && nextStatus == ApplicationFormStatus.INTERVIEW) {
            // application not yet in interview stage, interview is next
            actions.addAction("validate", "Administer Interview");
        }

        if (user.isInRole(Authority.ADMITTER) && !application.hasConfirmElegibilityComment()) {
            actions.addAction("validate", "Confirm Eligibility");
            if (application.getAdminRequestedRegistry() != null
                    && (application.isNotInState(ApplicationFormStatus.WITHDRAWN) && application.isNotInState(ApplicationFormStatus.REJECTED))) {
                actions.setRequiresAttention(true);
            } else {
                actions.setRequiresAttention(false);
            }
        }

        if (user.hasAdminRightsOnApplication(application) || user.isViewerOfProgramme(application) || user.isInRole(Authority.ADMITTER)) {
            actions.addAction("comment", "Comment");
        }

        if (user.isReviewerInLatestReviewRoundOfApplicationForm(application) && application.isInState(ApplicationFormStatus.REVIEW)
                && !user.hasRespondedToProvideReviewForApplicationLatestRound(application)) {
            actions.addAction("review", "Add review");
            actions.setRequiresAttention(true);
        }

        if (application.isInState(ApplicationFormStatus.INTERVIEW) && nextStatus == null && interview.isScheduling() && interview.isParticipant(user)
                && !interview.getParticipant(user).getResponded()) {
            actions.addAction("interviewVote", "Provide Availability For Interview");
            actions.setRequiresAttention(true);
        }

        if (user.isInterviewerOfApplicationForm(application) && application.isInState(ApplicationFormStatus.INTERVIEW) && interview.isScheduled()
                && !user.hasRespondedToProvideInterviewFeedbackForApplicationLatestRound(application)) {
            actions.addAction("interviewFeedback", "Add interview feedback");
            actions.setRequiresAttention(true);
        }

        if (user.isRefereeOfApplicationForm(application) && application.isSubmitted() && !application.isTerminated()
                && !user.getRefereeForApplicationForm(application).hasResponded()) {
            actions.addAction("reference", "Add reference");
            actions.setRequiresAttention(true);
        }

        if (user == application.getApplicant() && !application.isTerminated()) {
            actions.addAction("withdraw", "Withdraw");
        }

        if (user.hasAdminRightsOnApplication(application) && application.isPendingApprovalRestart()) {
            actions.addAction("restartApproval", "Revise Approval");
            actions.setRequiresAttention(true);
        }

        if (application.isInState(ApplicationFormStatus.APPROVAL)
                && (user.isInRoleInProgram(Authority.APPROVER, application.getProgram()) || user.isInRole(Authority.SUPERADMINISTRATOR))) {
            actions.addAction("validate", "Approve");
            if (user.isNotInRole(Authority.SUPERADMINISTRATOR) && !application.isPendingApprovalRestart()) {
                actions.setRequiresAttention(true);
            }
        }

        if (application.isInState(ApplicationFormStatus.APPROVAL) && !application.isPendingApprovalRestart()
                && user.isInRoleInProgram(Authority.ADMINISTRATOR, application.getProgram())
                && user.isNotInRoleInProgram(Authority.APPROVER, application.getProgram()) && user.isNotInRole(Authority.SUPERADMINISTRATOR)) {
            actions.addAction("restartApprovalAsAdministrator", "Revise Approval");
            actions.setRequiresAttention(true);
        }

        if (application.isInState(ApplicationFormStatus.APPROVAL)) {
            Supervisor primarySupervisor = application.getLatestApprovalRound().getPrimarySupervisor();
            if (primarySupervisor != null && user == primarySupervisor.getUser() && !primarySupervisor.hasResponded()) {
                actions.addAction("confirmSupervision", "Confirm supervision");
                actions.setRequiresAttention(true);
            }
        }

        actions.addAction("emailApplicant", "Email applicant");

        return actions.sort();
    }
}
