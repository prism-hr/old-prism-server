package com.zuehlke.pgadmissions.controllers.applicantform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.security.ContentAccessProvider;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.EmploymentPositionService;
import com.zuehlke.pgadmissions.services.FundingService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/deleteentity")
public class DeleteApplicationFormEntitiesController {

    private final QualificationService qualificationService;
    private final EmploymentPositionService employmentService;
    private final FundingService fundingService;
    private final RefereeService refereeService;
    private final EncryptionHelper encryptionHelper;
    private final ApplicationsService applicationsService;
    private final UserService userService;
    private final ApplicationFormUserRoleService applicationFormUserRoleService;
    private final ContentAccessProvider contentAccessProvider;

    DeleteApplicationFormEntitiesController() {
        this(null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public DeleteApplicationFormEntitiesController(QualificationService qualificationService, EmploymentPositionService employmentService,
            FundingService fundingService, RefereeService refereeService, EncryptionHelper encryptionHelper, ApplicationsService applicationsService,
            UserService userService, final ApplicationFormUserRoleService applicationFormUserRoleService, ContentAccessProvider contentAccessProvider) {
        this.qualificationService = qualificationService;
        this.employmentService = employmentService;
        this.fundingService = fundingService;
        this.refereeService = refereeService;
        this.encryptionHelper = encryptionHelper;
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
        this.contentAccessProvider = contentAccessProvider;
    }

    @RequestMapping(value = "/qualification", method = RequestMethod.POST)
    public String deleteQualification(@RequestParam("id") String encryptedQualificationId) {
        Qualification qualification = qualificationService.getQualificationById(encryptionHelper.decryptToInteger(encryptedQualificationId));
        ApplicationForm application = qualification.getApplication();
        RegisteredUser user = userService.getCurrentUser();
        contentAccessProvider.validateCanEditAsApplicant(application, user);
        qualificationService.delete(qualification);
        updateLastAccessAndLastModified(user, application);
        return "redirect:/update/getQualification?applicationId=" + application.getApplicationNumber() + "&message=deleted";
    }

    @RequestMapping(value = "/funding", method = RequestMethod.POST)
    public String deleteFunding(@RequestParam("id") String encryptedFundingId) {
        Funding funding = fundingService.getFundingById(encryptionHelper.decryptToInteger(encryptedFundingId));
        ApplicationForm application = funding.getApplication();
        RegisteredUser user = userService.getCurrentUser();
        contentAccessProvider.validateCanEditAsApplicant(application, user);
        fundingService.delete(funding);
        updateLastAccessAndLastModified(user, application);
        return "redirect:/update/getFunding?applicationId=" + application.getApplicationNumber() + "&message=deleted";
    }

    @RequestMapping(value = "/employment", method = RequestMethod.POST)
    public String deleteEmployment(@RequestParam("id") String encryptedEmploymentId) {
        EmploymentPosition position = employmentService.getEmploymentPositionById(encryptionHelper.decryptToInteger(encryptedEmploymentId));
        ApplicationForm application = position.getApplication();
        RegisteredUser user = userService.getCurrentUser();
        contentAccessProvider.validateCanEditAsApplicant(application, user);
        employmentService.delete(position);
        updateLastAccessAndLastModified(user, application);
        return "redirect:/update/getEmploymentPosition?applicationId=" + application.getApplicationNumber() + "&message=deleted";
    }

    @RequestMapping(value = "/referee", method = RequestMethod.POST)
    public String deleteReferee(@RequestParam("id") String encrypedRefereeId) {
        Referee referee = refereeService.getRefereeById(encryptionHelper.decryptToInteger(encrypedRefereeId));
        ApplicationForm application = referee.getApplication();
        RegisteredUser user = userService.getCurrentUser();
        contentAccessProvider.validateCanEditAsApplicant(application, user);
        refereeService.delete(referee);
        updateLastAccessAndLastModified(user, application);
        return "redirect:/update/getReferee?applicationId=" + application.getApplicationNumber() + "&message=deleted";
    }
    
    private void updateLastAccessAndLastModified(RegisteredUser currentUser, ApplicationForm applicationForm) {
        applicationFormUserRoleService.applicationEdited(applicationForm, currentUser);
        applicationsService.save(applicationForm);
    }

}