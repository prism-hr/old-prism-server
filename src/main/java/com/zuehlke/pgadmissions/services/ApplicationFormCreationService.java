package com.zuehlke.pgadmissions.services;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.zuehlke.pgadmissions.components.ApplicationFormCopyHelper;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Service("applicationFormCreationService")
@Transactional
public class ApplicationFormCreationService {

    private final Logger log = LoggerFactory.getLogger(ApplicationFormCreationService.class);

    @Autowired
    private ApplicationFormDAO applicationFormDAO;
    
    @Autowired
    private ProgrammeDetailsService programmeDetailsService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationFormUserRoleService applicationFormUserRoleService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ApplicationFormCopyHelper applicationFormCopyHelper;

    public ApplicationForm createOrGetUnsubmittedApplicationForm(final RegisteredUser applicant, final Advert advert) {
        userService.addRoleToUser(applicant, Authority.APPLICANT);
        
        ApplicationFormCreationService thisBean = applicationContext.getBean(ApplicationFormCreationService.class);

        ApplicationForm applicationForm = thisBean.findMostRecentApplication(applicant, advert);
        if (applicationForm != null) {
            return applicationForm;
        }

        applicationForm = thisBean.createNewApplicationForm(applicant, advert);

        thisBean.fillWithDataFromPreviousApplication(applicationForm);

        thisBean.addSuggestedSupervisors(applicationForm, advert);
        
        applicationFormUserRoleService.applicationCreated(applicationForm);

        log.info("New application form created: " + applicationForm.getApplicationNumber());
        return applicationForm;
    }

    protected void fillWithDataFromPreviousApplication(ApplicationForm applicationForm) {
        ApplicationForm previousApplication = applicationFormDAO.getPreviousApplicationForApplicant(applicationForm, userService.getCurrentUser());
        if (previousApplication != null) {
            applicationFormCopyHelper.copyApplicationFormData(applicationForm, previousApplication);
        }
    }

    protected ApplicationForm createNewApplicationForm(RegisteredUser applicant, Advert advert) {
        ApplicationFormCreationService thisBean = applicationContext.getBean(ApplicationFormCreationService.class);
        
        String applicationNumber = thisBean.generateNewApplicationNumber(advert.getProgram());

        ApplicationForm applicationForm = new ApplicationForm();
        applicationForm.setApplicant(applicant);
        applicationForm.setAdvert(advert);
        applicationForm.setApplicationNumber(applicationNumber);

        applicationFormDAO.save(applicationForm);

        ProgrammeDetails programmeDetails = new ProgrammeDetails();
        programmeDetails.setProgrammeName(applicationForm.getAdvert().getProgram().getTitle());
        applicationForm.setProgrammeDetails(programmeDetails);
        programmeDetails.setApplication(applicationForm);

        programmeDetailsService.save(programmeDetails);

        return applicationForm;
    }

    protected String generateNewApplicationNumber(final Program program) {
        String thisYear = new SimpleDateFormat("yyyy").format(new Date());
        Long runningCount = applicationFormDAO.getApplicationsInProgramThisYear(program, thisYear);
        String applicationNumber = program.getCode() + "-" + thisYear + "-" + String.format("%06d", runningCount + 1);
        return applicationNumber;
    }

    protected void addSuggestedSupervisors(ApplicationForm applicationForm, Advert advert) {
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
                
                applicationForm.getProgrammeDetails().getSuggestedSupervisors().addAll(suggestedSupervisors);
            }
        }
    }

    protected ApplicationForm findMostRecentApplication(final RegisteredUser applicant, final Advert advert) {
        Program program = advert.getProgram();
        Project project = advert.getProject();
        
        List<ApplicationForm> applications = project == null ? applicationFormDAO.getApplicationsByApplicantAndProgram(applicant, program) : applicationFormDAO
                .getApplicationsByApplicantAndProgramAndProject(applicant, program, project);

        Iterable<ApplicationForm> filteredApplications = Iterables.filter(applications, new Predicate<ApplicationForm>() {
            @Override
            public boolean apply(ApplicationForm applicationForm) {
                return !applicationForm.isDecided() && !applicationForm.isWithdrawn();
            }
        });

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

}