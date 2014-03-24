package com.zuehlke.pgadmissions.services;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.components.ApplicationFormCopyHelper;
import com.zuehlke.pgadmissions.dao.ActionDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormListDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.ProgramInstance;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.ActionType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.ReportFormat;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service
@Transactional
public class ApplicationFormService {

    private final Logger log = LoggerFactory.getLogger(ApplicationFormService.class);

    public static final int APPLICATION_BLOCK_SIZE = 50;

    @Autowired
    private ApplicationFormDAO applicationFormDAO;

    @Autowired
    private ApplicationFormListDAO applicationFormListDAO;

    @Autowired
    private MailSendingService mailService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActionsProvider actionsProvider;

    @Autowired
    private StateService stateService;

    @Autowired
    private ApplicationFormUserRoleService applicationFormUserRoleService;

    @Autowired
    private EventFactory eventFactory;
    
    @Autowired
    private ProgramDetailsService programDetailsService;
    
    @Autowired
    private ApplicationFormCopyHelper applicationFormCopyHelper;
    
    @Autowired
    private ProgramService programService;
    
    @Autowired
    private ExportQueueService exportQueueService;
    
    @Autowired
    private ActionDAO actionDAO;

    public ApplicationForm getApplicationById(Integer id) {
        return applicationFormDAO.get(id);
    }

    public ApplicationForm getApplicationByApplicationNumber(String applicationNumber) {
        return applicationFormDAO.getApplicationByApplicationNumber(applicationNumber);
    }

    public void save(ApplicationForm application) {
        applicationFormDAO.save(application);
    }

    public List<ApplicationDescriptor> getAllVisibleAndMatchedApplicationsForList(final RegisteredUser user, final ApplicationsFiltering filtering) {
        List<ApplicationDescriptor> applications = applicationFormListDAO.getVisibleApplicationsForList(user, filtering, APPLICATION_BLOCK_SIZE);
        for (ApplicationDescriptor application : applications) {
            application.getActionDefinitions().addAll(applicationFormUserRoleService.selectUserActions(user.getId(), application.getApplicationFormId()));
        }
        return applications;
    }

    public List<ApplicationForm> getAllVisibleAndMatchedApplicationsForReport(final RegisteredUser user, final ApplicationsFiltering filtering,
            final ReportFormat reportType) {
        return applicationFormListDAO.getVisibleApplicationsForReport(user, filtering);
    }

    public void sendSubmissionConfirmationToApplicant(final ApplicationForm applicationForm) {
        try {
            mailService.sendSubmissionConfirmationToApplicant(applicationForm);
            applicationFormDAO.save(applicationForm);
        } catch (Exception e) {
            log.warn("{}", e);
        }
    }

    public void refresh(final ApplicationForm applicationForm) {
        applicationFormDAO.refresh(applicationForm);
    }

    public List<ApplicationForm> getAllApplicationsByStatus(final ApplicationFormStatus status) {
        return applicationFormDAO.getAllApplicationsByStatus(status);
    }

    public List<ApplicationForm> getApplicationsForProject(final Project project) {
        return applicationFormDAO.getApplicationsByProject(project);
    }

    public void submitApplication(ApplicationForm application, HttpServletRequest request) {
        setApplicantIpAddress(application, request);
        setApplicationFormStatus(application, ApplicationFormStatus.VALIDATION);
        sendSubmissionConfirmationToApplicant(application);
        applicationFormUserRoleService.applicationSubmitted(application);
        applicationFormUserRoleService.insertApplicationUpdate(application, userService.getCurrentUser(), ApplicationUpdateScope.ALL_USERS);
    }

    public void setApplicationFormStatus(ApplicationForm application, ApplicationFormStatus newStatus) {
        application.setLastStatus(application.getStatus());
        application.setStatus(stateService.getById(newStatus));
        application.setNextStatus(null);

        switch (newStatus) {
        case UNSUBMITTED:
            application.setDueDate(getApplicationFormDueDate(application));
            application.setClosingDate(getApplicationFormClosingDate(application));
        case VALIDATION:
            application.setSubmittedDate(new LocalDate().toDate());
            application.setDueDate(getApplicationFormDueDate(application));
            application.getEvents().add(eventFactory.createEvent(ApplicationFormStatus.VALIDATION));
        case INTERVIEW:
            application.setDueDate(getApplicationFormDueDate(application));
            application.setClosingDate(null);
        case APPROVAL:
            application.setDueDate(getApplicationFormDueDate(application));
            application.setClosingDate(null);
        case REVIEW:
            application.setDueDate(getApplicationFormDueDate(application));
            if (application.getLastStatus().getId() == newStatus) {
                application.setClosingDate(null);
            }
        case APPROVED:
        case REJECTED:
        case WITHDRAWN:
            application.setDueDate(null);
            application.setClosingDate(null);
            application.getEvents().add(eventFactory.createEvent(ApplicationFormStatus.WITHDRAWN));
            applicationFormUserRoleService.deleteApplicationActions(application);
        }
        
    }
    
    public ApplicationForm createOrGetUnsubmittedApplicationForm(final RegisteredUser applicant, final String programCode, final Integer advertId) {
        Advert advert = programService.getValidProgramProjectAdvert(programCode, advertId);
        userService.addRoleToUser(applicant, Authority.APPLICANT);
        ApplicationForm applicationForm = applicationFormDAO.getPreviousApplicationForApplicantForAdvert(applicant, advert);
        if (applicationForm != null) {
            return applicationForm;
        }

        applicationForm = createNewApplicationForm(applicant, advert);
        fillWithDataFromPreviousApplication(applicationForm);
        addSuggestedSupervisors(applicationForm, advert);
        applicationFormUserRoleService.applicationCreated(applicationForm);

        log.info("New application form created: " + applicationForm.getApplicationNumber());
        return applicationForm;
    }

    public ApplicationForm getSecuredApplicationForm(final String applicationId, final ApplicationFormAction... actions) {
        ApplicationForm application = getApplicationByApplicationNumber(applicationId);
        if (application == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        
        RegisteredUser user = userService.getCurrentUser();
        for (ApplicationFormAction action : actions) {
            if (actionsProvider.checkActionAvailable(application, user, action)) {
                return application;
            }
        }
        throw new ActionNoLongerRequiredException(application.getApplicationNumber());
    }

    public void saveOrUpdateApplicationFormSection(ApplicationForm application) {
        RegisteredUser currentUser = userService.getCurrentUser();
        Action action = actionDAO.getById(actionsProvider.getPrecedentAction(application, currentUser, ActionType.VIEW_EDIT));
        applicationFormUserRoleService.insertApplicationUpdate(application, userService.getCurrentUser(), action.getUpdateVisibility());
    }
    
    public void openApplicationFormForUpdate(ApplicationForm application, RegisteredUser user) {
        programService.getValidProgramProjectAdvert(application.getProgram().getCode(), application.getAdvert().getId());
        openApplicationFormForView(application, user);
    }
    
    public void openApplicationFormForView(ApplicationForm application, RegisteredUser user) {
        applicationFormUserRoleService.deleteApplicationUpdate(application, user);
    }
    
    public void queueApplicationForExport(ApplicationForm application) {
        if (application.getLastStatus().isSubmitted() && application.getProgram().getProgramFeed() != null) {
            exportQueueService.createOrReturnExistingApplicationFormTransfer(application);
        }
    }
    
    public Date getDefaultStartDate(ApplicationForm application) {
        Program program = application.getProgram();
        String studyOption = application.getProgramDetails().getStudyOption();
        if (program != null && studyOption != null) {
            return programService.getDefaultStartDate(program, studyOption);
        }
        return null;
    }
    
    private void fillWithDataFromPreviousApplication(ApplicationForm applicationForm) {
        RegisteredUser user = userService.getCurrentUser();
        if (user != null) {
            ApplicationForm previousApplication = applicationFormDAO.getPreviousApplicationForApplicant(applicationForm, user);
            if (previousApplication != null) {
                applicationFormCopyHelper.copyApplicationFormData(applicationForm, previousApplication);
            }
        }
    }

    private ApplicationForm createNewApplicationForm(RegisteredUser applicant, Advert advert) {
        String applicationNumber = generateNewApplicationNumber(advert.getProgram());
        ApplicationForm applicationForm = new ApplicationForm();
        applicationForm.setApplicant(applicant);
        applicationForm.setAdvert(advert);
        applicationForm.setApplicationNumber(applicationNumber);
        applicationFormDAO.save(applicationForm);
        ProgramDetails programDetails = new ProgramDetails();
        programDetails.setProgrammeName(applicationForm.getAdvert().getProgram().getTitle());
        applicationForm.setProgramDetails(programDetails);
        programDetails.setApplication(applicationForm);
        programDetailsService.saveOrUpdate(programDetails);
        return applicationForm;
    }

    private String generateNewApplicationNumber(final Program program) {
        String thisYear = new SimpleDateFormat("yyyy").format(new Date());
        Long runningCount = applicationFormDAO.getApplicationsInProgramThisYear(program, thisYear);
        String applicationNumber = program.getCode() + "-" + thisYear + "-" + String.format("%06d", runningCount + 1);
        return applicationNumber;
    }

    private void addSuggestedSupervisors(ApplicationForm applicationForm, Advert advert) {
        if (advert != null) {
            Project project = advert.getProject();

            if (project != null) {
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

                applicationForm.getProgramDetails().getSuggestedSupervisors().addAll(suggestedSupervisors);
            }
        }
    }

    private Date getApplicationFormClosingDate(ApplicationForm application) {
        if (application.getProject() != null) {
            return application.getProject().getClosingDate();
        }
        return programService.getNextClosingDate(application.getProgram());
    }

    private Date getApplicationFormDueDate(ApplicationForm application) {
        LocalDate baselineDate = new LocalDate();
        Date closingDate = application.getClosingDate();
        State status = application.getStatus();
        if (status.getId() == ApplicationFormStatus.REVIEW && closingDate != null) {
            if (closingDate.after(baselineDate.toDate())) {
                baselineDate = new LocalDate(closingDate);
            }
        }
        Integer daysToAdd = status.getDurationInDays();
        if (daysToAdd != null) {
            application.setDueDate(baselineDate.plusDays(daysToAdd).toDate());
        }
        return null;
    }

    private void setApplicantIpAddress(ApplicationForm application, HttpServletRequest request) {
        try {
            application.setIpAddressAsString(request.getRemoteAddr());
        } catch (UnknownHostException e) {
            log.error("Error while setting ip address of: " + request.getRemoteAddr(), e);
        }
    }

}
