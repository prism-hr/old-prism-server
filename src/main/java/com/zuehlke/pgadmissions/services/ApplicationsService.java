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
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormListDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationsPreFilter;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service("applicationsService")
@Transactional
public class ApplicationsService {

    private final Logger log = LoggerFactory.getLogger(ApplicationsService.class);

    public static final int APPLICATION_BLOCK_SIZE = 50;

    private ApplicationFormDAO applicationFormDAO;

    private ApplicationFormListDAO applicationFormListDAO;

    private MailSendingService mailService;

    private ProgrammeDetailsService programmeDetailsService;
    
    private ProgramDAO programDAO;

    public ApplicationsService() {
        this(null, null, null, null, null);
    }

    @Autowired
    public ApplicationsService(final ApplicationFormDAO applicationFormDAO, final ApplicationFormListDAO applicationFormListDAO,
            final MailSendingService mailService, final ProgrammeDetailsService programmeDetailsService, final ProgramDAO programDAO) {
        this.applicationFormDAO = applicationFormDAO;
        this.applicationFormListDAO = applicationFormListDAO;
        this.mailService = mailService;
        this.programmeDetailsService = programmeDetailsService;
        this.programDAO = programDAO;
    }

    public Date getBatchDeadlineForApplication(ApplicationForm form) {
        Date closingDate = programDAO.getNextClosingDateForProgram(form.getProgram(), new Date());
        if (closingDate == null) {
            return new Date();
        }
        return closingDate;
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

    public ApplicationForm createOrGetUnsubmittedApplicationForm(final RegisteredUser user, final Program program, Project project) {

        ApplicationForm applicationForm = findMostRecentApplication(user, program, project);
        if (applicationForm != null) {
            return applicationForm;
        }

        String thisYear = new SimpleDateFormat("yyyy").format(new Date());

        applicationForm = new ApplicationForm();
        applicationForm.setApplicant(user);
        applicationForm.setProgram(program);
        applicationForm.setProject(project);


        Long runningCount = applicationFormDAO.getApplicationsInProgramThisYear(program, thisYear);
        applicationForm.setApplicationNumber(program.getCode() + "-" + thisYear + "-" + String.format("%06d", ++runningCount));
        applicationFormDAO.save(applicationForm);

        
        if (project != null) {
            List<SuggestedSupervisor> suggestedSupervisors = createSuggestedSupervisors(project);
            ProgrammeDetails programmeDetails = new ProgrammeDetails();
            programmeDetails.getSuggestedSupervisors().addAll(suggestedSupervisors);
            programmeDetails.setProgrammeName(applicationForm.getProgram().getTitle());
            
            applicationForm.setProgrammeDetails(programmeDetails);
            programmeDetails.setApplication(applicationForm);
            programmeDetailsService.save(programmeDetails);
        }
        
        return applicationForm;
    }

    private List<SuggestedSupervisor> createSuggestedSupervisors(Project project) {
        List<SuggestedSupervisor> suggestedSupervisors = Lists.newArrayListWithCapacity(2);
        List<RegisteredUser> projectSupervisors = Lists.newArrayListWithCapacity(2);
        projectSupervisors.add(project.getPrimarySupervisor());
        if (project.getSecondarySupervisor() != null) {
            projectSupervisors.add(project.getSecondarySupervisor());
        }

        for (RegisteredUser projectSupervisor : projectSupervisors) {
            SuggestedSupervisor supervisor = new SuggestedSupervisor();
            supervisor.setEmail(projectSupervisor.getEmail());
            supervisor.setFirstname(projectSupervisor.getFirstName());
            supervisor.setLastname(projectSupervisor.getLastName());
            supervisor.setAware(true);
            suggestedSupervisors.add(supervisor);
        }
        return suggestedSupervisors;
    }

    private ApplicationForm findMostRecentApplication(final RegisteredUser user, final Program program, Project project) {
        List<ApplicationForm> applications = project == null ? applicationFormDAO.getApplicationsByApplicantAndProgram(user, program) : applicationFormDAO
                .getApplicationsByApplicantAndProgramAndProject(user, program, project);

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
            return applicationFormListDAO.getApplicationsWorthConsideringForAttentionFlag(user, filtering, APPLICATION_BLOCK_SIZE);
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

    public void fastTrackApplication(final String applicationNumber) {
        ApplicationForm form = applicationFormDAO.getApplicationByApplicationNumber(applicationNumber);
        form.setBatchDeadline(null);
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

}
