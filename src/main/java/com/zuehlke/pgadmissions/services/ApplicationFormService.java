package com.zuehlke.pgadmissions.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
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
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationFilterGroup;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.ReportFormat;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.CannotExecuteActionException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;

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

    public Application getById(Integer id) {
        return applicationFormDAO.getById(id);
    }

    public void save(Application application) {
        applicationFormDAO.save(application);
    }

    public void refresh(final Application applicationForm) {
        applicationFormDAO.refresh(applicationForm);
    }

    public Application getByApplicationNumber(String applicationNumber) {
        return applicationFormDAO.getByApplicationNumber(applicationNumber);
    }

    public List<ApplicationDescriptor> getApplicationsForList(final User user, final ApplicationFilterGroup filtering) {
        List<ApplicationDescriptor> applications = applicationFormListDAO.getVisibleApplicationsForList(user, filtering, APPLICATION_BLOCK_SIZE);
        for (ApplicationDescriptor application : applications) {
            application.getActionDefinitions().addAll(actionService.getUserActions(user.getId(), application.getApplicationFormId()));
        }
        return applications;
    }

    public List<Application> getApplicationsForReport(final User user, final ApplicationFilterGroup filtering, final ReportFormat reportType) {
        return applicationFormListDAO.getVisibleApplicationsForReport(user, filtering);
    }

    public List<Application> getApplicationsByStatus(final PrismState status) {
        return applicationFormDAO.getAllApplicationsByStatus(status);
    }

    public List<Application> getApplicationsForProject(final Project project) {
        return applicationFormDAO.getApplicationsByProject(project);
    }

    public void submitApplication(Application application) {
        // TODO set IP
        
        setApplicationStatus(application, PrismState.APPLICATION_VALIDATION);
        workflowService.applicationSubmitted(application);
        workflowService.applicationUpdated(application, userService.getCurrentUser());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void setApplicationStatus(Application application, PrismState newStatus) {
        application.setState(stateService.getById(newStatus));

        // TODO add referee roles and send referee notifications
        // TODO add admitter roles
        switch (newStatus) {
        case APPLICATION_UNSUBMITTED:
            application.setDueDate(getDueDateForApplication(application));
            application.setClosingDate(getClosingDateForApplication(application));
        case APPLICATION_VALIDATION:
            application.setSubmittedTimestamp(new DateTime());
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
        }
        

    }

    public Application getOrCreateApplication(final User applicant, final Integer advertId) {
        Advert advert = programService.getValidProgramProjectAdvert(advertId);
        Application applicationForm = applicationFormDAO.getInProgressApplication(applicant, advert);
        if (applicationForm != null) {
            return applicationForm;
        }
        applicationForm = createApplication(applicant, advert);
        autoPopulateApplication(applicationForm);
        log.info("New application form created: " + applicationForm.getApplicationNumber());
        return applicationForm;
    }

    public Application getSecuredApplication(final String applicationId, final ApplicationFormAction... actions) {
        Application application = getByApplicationNumber(applicationId);
        if (application == null) {
            throw new ResourceNotFoundException();
        }
        User user = userService.getCurrentUser();
        for (ApplicationFormAction action : actions) {
            if (actionService.checkActionAvailable(application, user, action)) {
                return application;
            }
        }
        throw new CannotExecuteActionException(application);
    }

    public void saveOrUpdateApplicationSection(Application application) {
        User currentUser = userService.getCurrentUser();
        workflowService.applicationUpdated(application, userService.getCurrentUser());
    }

    public void openApplicationForEdit(Application application, User user) {
        openApplicationForView(application, user);
    }

    public void openApplicationForView(Application application, User user) {
        applicationFormDAO.deleteApplicationUpdate(application, user);
    }

    public void queueApplicationForExport(Application application) {
        if (application.getState().getId() == PrismState.APPLICATION_WITHDRAWN_PENDING_EXPORT) {
            exportQueueService.createOrReturnExistingApplicationFormTransfer(application);
        }
    }

    public Date getDefaultStartDateForApplication(Application application) {
        Program program = application.getProgram();
        StudyOption studyOption = application.getProgramDetails().getStudyOption();
        if (program != null && studyOption != null) {
            return programService.getDefaultStartDate(program, studyOption);
        }
        return null;
    }

    public void applicationCreated(Application application) {
        User applicant = application.getUser();
        Role role = roleService.getById(Authority.APPLICATION_CREATOR);
        // TODO save action
        roleService.getOrCreateUserRole(application, applicant, Authority.APPLICATION_CREATOR);
    }

    public ApplicationDescriptor getApplicationDescriptorForUser(final Application application, final User user) {
        ApplicationDescriptor applicationDescriptor = new ApplicationDescriptor();
        applicationDescriptor.getActionDefinitions().addAll(actionService.getUserActions(user.getId(), application.getId()));
        applicationDescriptor.setNeedsToSeeUrgentFlag(applicationFormDAO.getRaisesUrgentFlagForUser(application, user));
//        applicationDescriptor.setNeedsToSeeUpdateFlag(applicationFormDAO.getRaisesUpdateFlagForUser(application, user));
        return applicationDescriptor;
    }

    public Comment getLatestStateChangeComment(Application applicationForm, ApplicationFormAction action) {
        return applicationFormDAO.getLatestStateChangeComment(applicationForm);
    }

    private void autoPopulateApplication(Application applicationForm) {
        User user = userService.getCurrentUser();
        if (user != null) {
            Application previousApplication = applicationFormDAO.getPreviousApplicationForApplicant(applicationForm, user);
            if (previousApplication != null) {
                applicationFormCopyHelper.copyApplicationFormData(applicationForm, previousApplication);
            }
        }
    }

    private Application createApplication(User applicant, Advert advert) {
        String applicationNumber = generateApplicationNumber(advert.getProgram());
        Application application = new Application();
        application.setCreatedTimestamp(new DateTime());
        application.setUser(applicant);
        application.setProgram(advert.getProgram());
        application.setProject(advert.getProject());
        application.setApplicationNumber(applicationNumber);
        return application;
    }

    private String generateApplicationNumber(final Program program) {
        String thisYear = new SimpleDateFormat("yyyy").format(new Date());
        Long runningCount = applicationFormDAO.getApplicationsInProgramThisYear(program, thisYear);
        String applicationNumber = program.getCode() + "-" + thisYear + "-" + String.format("%06d", runningCount + 1);
        return applicationNumber;
    }

    private void addSuggestedSupervisorsFromProject(Application application) {
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

    private LocalDate getClosingDateForApplication(Application application) {
        return application.getAdvert().getClosingDate().getClosingDate();
    }

    private LocalDate getDueDateForApplication(Application application) {
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
