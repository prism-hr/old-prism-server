package com.zuehlke.pgadmissions.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationFormCopyHelper;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormListDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.ActionRequired;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ActionType;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.ReportFormat;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;

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
    private UserService userService;

    @Autowired
    private StateService stateService;

    @Autowired
    private ProgramDetailsService programDetailsService;

    @Autowired
    private ApplicationFormCopyHelper applicationFormCopyHelper;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ExportQueueService exportQueueService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private WorkflowService workflowService;

    public ApplicationForm getById(Integer id) {
        return applicationFormDAO.getById(id);
    }

    public void save(ApplicationForm application) {
        applicationFormDAO.save(application);
    }

    public void refresh(final ApplicationForm applicationForm) {
        applicationFormDAO.refresh(applicationForm);
    }

    public ApplicationForm getByApplicationNumber(String applicationNumber) {
        return applicationFormDAO.getByApplicationNumber(applicationNumber);
    }

    public List<ApplicationDescriptor> getApplicationsForList(final User user, final ApplicationsFiltering filtering) {
        List<ApplicationDescriptor> applications = applicationFormListDAO.getVisibleApplicationsForList(user, filtering, APPLICATION_BLOCK_SIZE);
        for (ApplicationDescriptor application : applications) {
            application.getActionDefinitions().addAll(actionService.getUserActions(user.getId(), application.getApplicationFormId()));
        }
        return applications;
    }

    public List<ApplicationForm> getApplicationsForReport(final User user, final ApplicationsFiltering filtering, final ReportFormat reportType) {
        return applicationFormListDAO.getVisibleApplicationsForReport(user, filtering);
    }

    public List<ApplicationForm> getApplicationsByStatus(final ApplicationFormStatus status) {
        return applicationFormDAO.getAllApplicationsByStatus(status);
    }

    public List<ApplicationForm> getApplicationsForProject(final Project project) {
        return applicationFormDAO.getApplicationsByProject(project);
    }

    public void submitApplication(ApplicationForm application) {
        // TODO set IP
        
        setApplicationStatus(application, ApplicationFormStatus.VALIDATION);
        workflowService.applicationSubmitted(application);
        workflowService.applicationUpdated(application, userService.getCurrentUser());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void setApplicationStatus(ApplicationForm application, ApplicationFormStatus newStatus) {
        application.setLastState(application.getState());
        application.setState(stateService.getById(newStatus));
        application.setNextState(null);

        // TODO add referee roles and send referee notifications
        // TODO add admitter roles
        switch (newStatus) {
        case UNSUBMITTED:
            application.setDueDate(getDueDateForApplication(application));
            application.setClosingDate(getClosingDateForApplication(application));
        case VALIDATION:
            application.setSubmittedTimestamp(new LocalDate().toDate());
            application.setDueDate(getDueDateForApplication(application));
        case INTERVIEW:
            application.setDueDate(getDueDateForApplication(application));
            application.setClosingDate(null);
        case APPROVAL:
            application.setDueDate(getDueDateForApplication(application));
            application.setClosingDate(null);
        case REVIEW:
            application.setDueDate(getDueDateForApplication(application));
            if (application.getLastState().getId() == newStatus) {
                application.setClosingDate(null);
            }
        case APPROVED:
        case REJECTED:
        case WITHDRAWN:
            application.setDueDate(null);
            application.setClosingDate(null);
            actionService.deleteApplicationActions(application);
        }
        

    }

    public ApplicationForm getOrCreateApplication(final User applicant, final Integer advertId) {
        Advert advert = programService.getValidProgramProjectAdvert(advertId);
        ApplicationForm applicationForm = applicationFormDAO.getInProgressApplication(applicant, advert);
        if (applicationForm != null) {
            return applicationForm;
        }
        applicationForm = createApplication(applicant, advert);
        autoPopulateApplication(applicationForm);
        addSuggestedSupervisorsFromProject(applicationForm);
        workflowService.applicationCreated(applicationForm);
        log.info("New application form created: " + applicationForm.getApplicationNumber());
        return applicationForm;
    }

    public ApplicationForm getSecuredApplication(final String applicationId, final ApplicationFormAction... actions) {
        ApplicationForm application = getByApplicationNumber(applicationId);
        if (application == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        User user = userService.getCurrentUser();
        for (ApplicationFormAction action : actions) {
            if (actionService.checkActionAvailable(application, user, action)) {
                return application;
            }
        }
        throw new ActionNoLongerRequiredException(application.getApplicationNumber());
    }

    public void saveOrUpdateApplicationSection(ApplicationForm application) {
        User currentUser = userService.getCurrentUser();
        Action action = actionService.getById(actionService.getPrecedentAction(application, currentUser, ActionType.APPLICATION_VIEW_EDIT));
        workflowService.applicationUpdated(application, userService.getCurrentUser());
    }

    public void openApplicationForEdit(ApplicationForm application, User user) {
        openApplicationForView(application, user);
    }

    public void openApplicationForView(ApplicationForm application, User user) {
        applicationFormDAO.deleteApplicationUpdate(application, user);
    }

    public void queueApplicationForExport(ApplicationForm application) {
        if (application.getLastState().isSubmitted() && application.getProgram().getProgramFeed() != null) {
            exportQueueService.createOrReturnExistingApplicationFormTransfer(application);
        }
    }

    public Date getDefaultStartDateForApplication(ApplicationForm application) {
        Program program = application.getProgram();
        StudyOption studyOption = application.getProgramDetails().getStudyOption();
        if (program != null && studyOption != null) {
            return programService.getDefaultStartDate(program, studyOption);
        }
        return null;
    }

    public void applicationCreated(ApplicationForm application) {
        User applicant = application.getApplicant();
        Action action = actionService.getById(ApplicationFormAction.APPLICATION_COMPLETE_APPLICATION);
        Role role = roleService.getById(Authority.APPLICANT);
        ActionRequired completeApplicationAction = new ActionRequired().withApplication(application).withRole(role).withAction(action).withDeadlineDate(application.getDueDate())
                .withBindDeadlineToDueDate(false).withRaisesUrgentFlag(true);
        // TODO save action
        roleService.createUserRole(application, applicant, Authority.APPLICANT);
    }

    public ApplicationDescriptor getApplicationDescriptorForUser(final ApplicationForm application, final User user) {
        ApplicationDescriptor applicationDescriptor = new ApplicationDescriptor();
        applicationDescriptor.getActionDefinitions().addAll(actionService.getUserActions(user.getId(), application.getId()));
        applicationDescriptor.setNeedsToSeeUrgentFlag(applicationFormDAO.getRaisesUrgentFlagForUser(application, user));
        applicationDescriptor.setNeedsToSeeUpdateFlag(applicationFormDAO.getRaisesUpdateFlagForUser(application, user));
        return applicationDescriptor;
    }

    public Comment getLatestStateChangeComment(ApplicationForm applicationForm, ActionType applicationCompleteApprovalStage) {
        return applicationFormDAO.getLatestStateChangeComment(applicationForm, applicationCompleteApprovalStage);
    }

    private void autoPopulateApplication(ApplicationForm applicationForm) {
        User user = userService.getCurrentUser();
        if (user != null) {
            ApplicationForm previousApplication = applicationFormDAO.getPreviousApplicationForApplicant(applicationForm, user);
            if (previousApplication != null) {
                applicationFormCopyHelper.copyApplicationFormData(applicationForm, previousApplication);
            }
        }
    }

    private ApplicationForm createApplication(User applicant, Advert advert) {
        String applicationNumber = generateApplicationNumber(advert.getProgram());
        ApplicationForm application = new ApplicationForm();
        application.setApplicant(applicant);
        application.setAdvert(advert);
        application.setApplicationNumber(applicationNumber);
        ProgramDetails programDetails = programDetailsService.getOrCreate(application);
        programDetails.setApplication(application);
        application.setProgramDetails(programDetails);
        applicationFormDAO.save(application);
        return application;
    }

    private String generateApplicationNumber(final Program program) {
        String thisYear = new SimpleDateFormat("yyyy").format(new Date());
        Long runningCount = applicationFormDAO.getApplicationsInProgramThisYear(program, thisYear);
        String applicationNumber = program.getCode() + "-" + thisYear + "-" + String.format("%06d", runningCount + 1);
        return applicationNumber;
    }

    private void addSuggestedSupervisorsFromProject(ApplicationForm application) {
        Project project = application.getProject();
        if (project != null) {
            List<SuggestedSupervisor> suggestedSupervisors = application.getProgramDetails().getSuggestedSupervisors();
            // FIXME add sugested supervisors
//            suggestedSupervisors.add(createSuggestedSupervisor(project.getPrimarySupervisor()));
//            User secondarySupervisor = project.getSecondarySupervisor();
//            if (secondarySupervisor != null) {
//                suggestedSupervisors.add(createSuggestedSupervisor(project.getSecondarySupervisor()));
//            }
        }
    }

    private SuggestedSupervisor createSuggestedSupervisor(User user) {
        SuggestedSupervisor supervisor = new SuggestedSupervisor();
        supervisor.setEmail(user.getEmail());
        supervisor.setFirstname(user.getFirstName());
        supervisor.setLastname(user.getLastName());
        supervisor.setAware(true);
        return supervisor;
    }

    private Date getClosingDateForApplication(ApplicationForm application) {
        return application.getAdvert().getClosingDate().getClosingDate();
    }

    private Date getDueDateForApplication(ApplicationForm application) {
        LocalDate baselineDate = new LocalDate();
        Date closingDate = application.getClosingDate();
        State status = application.getState();
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

}
