package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormListDAO;
import com.zuehlke.pgadmissions.dao.ApplicationFormUserRoleDAO;
import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationsFiltering;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service("applicationsService")
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
    private ApplicationFormUserRoleDAO applicationFormUserRoleDAO;

    @Autowired
    private CountriesDAO countriesDAO;

    @Autowired
    private DomicileDAO domicileDAO;

    public Date getBatchDeadlineForApplication(ApplicationForm form) {
        Date closingDate = programDAO.getNextClosingDateForProgram(form.getProgram(), new Date());
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

    public List<ApplicationDescriptor> getAllVisibleAndMatchedApplicationsForList(final RegisteredUser user, final ApplicationsFiltering filtering) {
        List<ApplicationDescriptor> applications = applicationFormListDAO.getVisibleApplicationsForList(user, filtering, APPLICATION_BLOCK_SIZE);
        for (ApplicationDescriptor application : applications) {
            application.getActionDefinitions().addAll(
                    applicationFormUserRoleDAO.findActionsByUserIdAndApplicationIdAndApplicationFormStatus(user.getId(), application.getApplicationFormId(),
                            application.getApplicationFormStatus()));
        }
        return applications;
    }

    public List<ApplicationForm> getAllVisibleAndMatchedApplicationsForReport(final RegisteredUser user, final ApplicationsFiltering filtering) {
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

    public void fastTrackApplication(final String applicationNumber) {
        ApplicationForm form = applicationFormDAO.getApplicationByApplicationNumber(applicationNumber);
        form.setBatchDeadline(null);
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

    /**
     * Temporary bodge to help applications with countries or domiciles that are
     * UK municipalities to pass the web service validation.
     * 
     * @author Alastair Knowles
     * @param form
     */
    public void transformUKCountriesAndDomiciles(final ApplicationForm form) {
        String ukCode = "XK";
        Country ukCountry = countriesDAO.getEnabledCountryByCode(ukCode);
        Domicile ukDomicile = domicileDAO.getEnabledDomicileByCode(ukCode);
        List<String> ukTransforms = Arrays.asList("XF", "XI", "XH", "8826");
        Country countryOfBirth = form.getPersonalDetails().getCountry();
        if (ukCountry != null) {
            if (ukTransforms.contains(countryOfBirth.getCode())) {
                form.getPersonalDetails().setCountry(ukCountry);
            }
        }
        if (ukDomicile != null) {
            Domicile countryOfResidence = form.getPersonalDetails().getResidenceCountry();
            if (ukTransforms.contains(countryOfResidence.getCode())) {
                form.getPersonalDetails().setResidenceCountry(ukDomicile);
            }
            Domicile countryOfCurrentAddress = form.getCurrentAddress().getDomicile();
            if (ukTransforms.contains(countryOfCurrentAddress.getCode())) {
                form.getCurrentAddress().setDomicile(ukDomicile);
            }
            Domicile countryOfContactAddress = form.getContactAddress().getDomicile();
            if (ukTransforms.contains(countryOfContactAddress.getCode())) {
                form.getContactAddress().setDomicile(ukDomicile);
            }
            for (Qualification qualification : form.getQualifications()) {
                Domicile countryOfInstitution = qualification.getInstitutionCountry();
                if (ukTransforms.contains(countryOfInstitution.getCode())) {
                    qualification.setInstitutionCountry(ukDomicile);
                }
            }
            for (EmploymentPosition position : form.getEmploymentPositions()) {
                Domicile countryOfEmployer = position.getEmployerAddress().getDomicile();
                if (ukTransforms.contains(countryOfEmployer.getCode())) {
                    position.getEmployerAddress().setDomicile(ukDomicile);
                }
            }
            for (Referee referee : form.getReferees()) {
                Domicile countryOfReferee = referee.getAddressLocation().getDomicile();
                if (ukTransforms.contains(countryOfReferee.getCode())) {
                    referee.getAddressLocation().setDomicile(ukDomicile);
                }
            }
        }
    }

}