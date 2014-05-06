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
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
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

    public List<ApplicationForm> getApplicationsByStatus(final PrismState status) {
        return applicationFormDAO.getAllApplicationsByStatus(status);
    }

    public List<ApplicationForm> getApplicationsForProject(final Project project) {
        return applicationFormDAO.getApplicationsByProject(project);
    }

    public void submitApplication(ApplicationForm application) {
        // TODO set IP
        
        setApplicationStatus(application, PrismState.APPLICATION_VALIDATION);
        workflowService.applicationSubmitted(application);
        workflowService.applicationUpdated(application, userService.getCurrentUser());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void setApplicationStatus(ApplicationForm application, PrismState newStatus) {
        application.setState(stateService.getById(newStatus));

        // TODO add referee roles and send referee notifications
        // TODO add admitter roles
        switch (newStatus) {
        case APPLICATION_UNSUBMITTED:
            application.setDueDate(getDueDateForApplication(application));
            application.setClosingDate(getClosingDateForApplication(application));
        case APPLICATION_VALIDATION:
            application.setSubmittedTimestamp(new LocalDate().toDate());
            application.setDueDate(getDueDateForApplication(application));
        case APPLICATION_INTERVIEW:
            application.setDueDate(getDueDateForApplication(application));
            application.setClosingDate(null);
        case APPLICATION_APPROVAL:
            application.setDueDate(getDueDateForApplication(application));
            application.setClosingDate(null);
        case APPLICATION_REVIEW:
            application.setDueDate(getDueDateForApplication(application));
            // TODO check the history in order tyo set closing date
//            if (application.getLastState().getId() == newStatus) {
                application.setClosingDate(null);
//            }
        case APPLICATION_APPROVED:
        case APPLICATION_REJECTED:
        case APPLICATION_WITHDRAWN:
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
        if (application.getState().getId() == PrismState.APPLICATION_WITHDRAWN_PENDING_EXPORT) {
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
        User applicant = application.getUser();
        Action action = actionService.getById(ApplicationFormAction.APPLICATION_COMPLETE);
        Role role = roleService.getById(Authority.APPLICATION_CREATOR);
        ActionRequired completeApplicationAction = new ActionRequired().withApplication(application).withRole(role).withAction(action).withDeadlineDate(application.getDueDate())
                .withBindDeadlineToDueDate(false).withRaisesUrgentFlag(true);
        // TODO save action
        roleService.createUserRole(application, applicant, Authority.APPLICATION_CREATOR);
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
        application.setUser(applicant);
        application.setProgram(advert.getProgram());
        application.setProject(advert.getProject());
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
        supervisor.setUser(user);
        supervisor.setAware(true);
        return supervisor;
    }

    private LocalDate getClosingDateForApplication(ApplicationForm application) {
        return application.getAdvert().getClosingDate().getClosingDate();
    }

    private LocalDate getDueDateForApplication(ApplicationForm application) {
        LocalDate baselineDate = new LocalDate();
        LocalDate closingDate = application.getClosingDate();
        State status = application.getState();
        if (status.getId() == PrismState.APPLICATION_REVIEW && closingDate != null) {
            if (closingDate.isAfter(baselineDate)) {
                baselineDate = new LocalDate(closingDate);
            }
        }
        // TODO write query to get duration
        Integer daysToAdd = 0; //status.getDurationInDays();
        if (daysToAdd != null) {
            application.setDueDate(baselineDate.plusDays(daysToAdd));
        }
        return null;
    }

}
