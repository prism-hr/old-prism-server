package com.zuehlke.pgadmissions.services;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormListDAO;
import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.ReportFormat;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service
@Transactional
public class ApplicationsService {

    private final Logger log = LoggerFactory.getLogger(ApplicationsService.class);

    public static final int APPLICATION_BLOCK_SIZE = 50;

    @Autowired
    private ApplicationFormDAO applicationFormDAO;

    @Autowired
    private ApplicationFormListDAO applicationFormListDAO;

    @Autowired
    private MailSendingService mailService;

    @Autowired
    private ProgramDAO programDAO;

    @Autowired
    private CountriesDAO countriesDAO;

    @Autowired
    private DomicileDAO domicileDAO;
    
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
    private AdditionalInformationService additionalInformationService;

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
            application.getActionDefinitions().addAll(
                    applicationFormUserRoleService.selectUserActions(user.getId(), application.getApplicationFormId()));
        }
        return applications;
    }

    public List<ApplicationForm> getAllVisibleAndMatchedApplicationsForReport(final RegisteredUser user, final ApplicationsFiltering filtering, final ReportFormat reportType) {
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
        applicationFormUserRoleService.insertApplicationUpdate(application, application.getApplicant(), ApplicationUpdateScope.ALL_USERS);
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
        }
        
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
    
    public void saveAdditionalInformationSection(ApplicationForm application, AdditionalInformation additionalInformation) {
        application.setAdditionalInformation(additionalInformation);
        additionalInformationService.save(additionalInformation);
        save(application);
        registerApplicationUpdate(application);
    }
    
    public void saveAddressSection(ApplicationForm application) {
        save(application);
        registerApplicationUpdate(application);
    }
    
    public void saveDocumentSection(ApplicationForm application) {
        save(application);
        registerApplicationUpdate(application);
    }
    
    private void registerApplicationUpdate(ApplicationForm application) {
        applicationFormUserRoleService.insertApplicationUpdate(application, userService.getCurrentUser(), ApplicationUpdateScope.ALL_USERS);
    }
    
    private Date getApplicationFormClosingDate(ApplicationForm application) {
        if (application.getProject() != null) {
            return application.getProject().getClosingDate();
        } 
        return programDAO.getNextClosingDate(application.getProgram());
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
    
    private void setApplicantIpAddress (ApplicationForm application, HttpServletRequest request) {
        try {
            application.setIpAddressAsString(request.getRemoteAddr());
        } catch (UnknownHostException e) {
            log.error("Error while setting ip address of: " + request.getRemoteAddr(), e);
        }
    }

}
