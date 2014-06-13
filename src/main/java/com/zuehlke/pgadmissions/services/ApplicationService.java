package com.zuehlke.pgadmissions.services;

import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.rest.domain.application.ApplicationListRowRepresentation;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.dao.ApplicationDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormListDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Filter;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.ApplicationSupervisor;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismScope;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.ReportFormat;

@Service
@Transactional
public class ApplicationService {

    public static final int APPLICATION_BLOCK_SIZE = 50;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private ApplicationFormListDAO applicationFormListDAO;
    
    @Autowired
    private EntityService entityService;

    @Autowired
    private UserService userService;

    @Autowired
    private StateService stateService;

    @Autowired
    private ProgramDetailsService programDetailsService;

    @Autowired
    private ApplicationCopyHelper applicationCopyHelper;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private SystemService systemService;

    public Application create(User user, Advert advert) {
        Application application = new Application();
        application.setUser(user);
        application.setParentResource(advert);
        application.setCreatedTimestamp(new DateTime());
        Application previousApplication = applicationDAO.getPreviousApplication(application);
        if (previousApplication != null) {
            applicationCopyHelper.copyApplicationFormData(application, previousApplication);
        }
        return application;
    }
    
    public Application getOrCreate(final User user, final Integer advertId) throws Exception { 
        return getOrCreate(user, programService.getValidProgramProjectAdvert(advertId));
    }
    
    public Application getOrCreate(final User user, final Advert advert) {
        Application transientApplication = create(user, advert);
        return entityService.getOrCreate(transientApplication);
    }
    
    public void save(Application application) {
        entityService.save(application);
    }
    
    // TODO: Rewrite/remove the following

    public Application getById(int id) {
        return applicationDAO.getById(id);
    }

    public void refresh(final Application applicationForm) {
        applicationDAO.refresh(applicationForm);
    }

    public Application getByApplicationNumber(String applicationNumber) {
        return applicationDAO.getByApplicationNumber(applicationNumber);
    }

    public List<Application> getApplicationsForList(final User user, final Filter filtering) {
        Filter userFilter = user.getUserAccount().getFilters().get(PrismScope.APPLICATION);
        if (userFilter.getPage() == 1) {
            userFilter.setLastAccessTimestamp(new DateTime());
        }

        List<Application> applications = applicationFormListDAO.getVisibleApplicationsForList(user, filtering, APPLICATION_BLOCK_SIZE);
        return applications;
    }

    public List<Application> getApplicationsForReport(final User user, final Filter filtering, final ReportFormat reportType) {
        return applicationFormListDAO.getVisibleApplicationsForReport(user, filtering);
    }

    public List<Application> getApplicationsByStatus(final PrismState status) {
        return applicationDAO.getAllApplicationsByStatus(status);
    }

    public List<Application> getApplicationsForProject(final Project project) {
        return applicationDAO.getApplicationsByProject(project);
    }

    public void saveOrUpdateApplicationSection(Application application) {
    }

    public Date getDefaultStartDateForApplication(Application application) {
        Program program = application.getProgram();
        StudyOption studyOption = application.getProgramDetails().getStudyOption();
        if (program != null && studyOption != null) {
            return programService.getDefaultStartDate(program, studyOption);
        }
        return null;
    }

    public Application getApplicationDescriptorForUser(final Application application, final User user) {
        Application applicationDescriptor = new Application();
//        applicationDescriptor.getActionDefinitions().addAll(actionService.getUserActions(user.getId(), application.getId()));
//        applicationDescriptor.setNeedsToSeeUrgentFlag(applicationFormDAO.getRaisesUrgentFlagForUser(application, user));
//        applicationDescriptor.setNeedsToSeeUpdateFlag(applicationFormDAO.getRaisesUpdateFlagForUser(application, user));
        return applicationDescriptor;
    }

    public Comment getLatestStateChangeComment(Application applicationForm, PrismAction action) {
        return applicationDAO.getLatestStateChangeComment(applicationForm);
    }

    private void addSuggestedSupervisorsFromProject(Application application) {
        Project project = application.getProject();
        if (project != null) {
            List<ApplicationSupervisor> suggestedSupervisors = application.getProgramDetails().getSuggestedSupervisors();
            // FIXME add sugested supervisors
//            suggestedSupervisors.add(createSuggestedSupervisor(project.getPrimarySupervisor()));
//            User secondarySupervisor = project.getSecondarySupervisor();
//            if (secondarySupervisor != null) {
//                suggestedSupervisors.add(createSuggestedSupervisor(project.getSecondarySupervisor()));
//            }
        }
    }

    private ApplicationSupervisor createSuggestedSupervisor(User user) {
        ApplicationSupervisor supervisor = new ApplicationSupervisor();
        supervisor.setUser(user);
        supervisor.setAware(true);
        return supervisor;
    }

    public List<ApplicationListRowRepresentation> getApplicationList(User user, Integer page, Integer perPage) {
        return applicationDAO.getApplicationList(user, page, perPage);
    }

    public List<Application> getApplications(User user, Integer page, Integer perPage) {
        return applicationDAO.getApplications(user, page, perPage);
    }
}
