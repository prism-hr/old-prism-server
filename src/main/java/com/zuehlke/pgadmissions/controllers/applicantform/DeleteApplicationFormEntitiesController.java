package com.zuehlke.pgadmissions.controllers.applicantform;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
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
	private final ApplicationFormAccessService accessService;

	DeleteApplicationFormEntitiesController() {
		this(null, null, null, null, null, null, null, null);
	}

	@Autowired
	public DeleteApplicationFormEntitiesController(QualificationService qualificationService, EmploymentPositionService employmentService,
			FundingService fundingService, RefereeService refereeService, EncryptionHelper encryptionHelper,
			ApplicationsService applicationsService, UserService userService, final ApplicationFormAccessService accessService) {

		this.qualificationService = qualificationService;
		this.employmentService = employmentService;
		this.fundingService = fundingService;
		this.refereeService = refereeService;
		this.encryptionHelper = encryptionHelper;
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.accessService = accessService;

	}

	@RequestMapping(value = "/qualification", method = RequestMethod.POST)
	public String deleteQualification(@RequestParam("id") String encryptedQualificationId) {
		Qualification qualification = qualificationService.getQualificationById(encryptionHelper.decryptToInteger(encryptedQualificationId));
		qualificationService.delete(qualification);
		updateLastAccessAndLastModified(userService.getCurrentUser(), qualification.getApplication());
		return "redirect:/update/getQualification?applicationId=" + qualification.getApplication().getApplicationNumber() + "&message=deleted";
	}

	@RequestMapping(value = "/funding", method = RequestMethod.POST)
	public String deleteFunding(@RequestParam("id") String encryptedFundingId) {
		Funding funding = fundingService.getFundingById(encryptionHelper.decryptToInteger(encryptedFundingId));
		fundingService.delete(funding);
		updateLastAccessAndLastModified(userService.getCurrentUser(), funding.getApplication());
		return "redirect:/update/getFunding?applicationId=" + funding.getApplication().getApplicationNumber() + "&message=deleted";
	}

	@RequestMapping(value = "/employment", method = RequestMethod.POST)
	public String deleteEmployment(@RequestParam("id") String encryptedEmploymentId) {
		EmploymentPosition position = employmentService.getEmploymentPositionById(encryptionHelper.decryptToInteger(encryptedEmploymentId));
		employmentService.delete(position);
		updateLastAccessAndLastModified(userService.getCurrentUser(), position.getApplication());
		return "redirect:/update/getEmploymentPosition?applicationId=" + position.getApplication().getApplicationNumber() + "&message=deleted";
	}

	@RequestMapping(value = "/referee", method = RequestMethod.POST)
	public String deleteReferee(@RequestParam("id") String encrypedRefereeId) {
		Integer id = encryptionHelper.decryptToInteger(encrypedRefereeId);
		Referee referee = refereeService.getRefereeById(id);
		refereeService.delete(referee);
		updateLastAccessAndLastModified(userService.getCurrentUser(), referee.getApplication());
		return "redirect:/update/getReferee?applicationId=" + referee.getApplication().getApplicationNumber() + "&message=deleted";
	}
	
	private void updateLastAccessAndLastModified(RegisteredUser currentUser, ApplicationForm applicationForm) {
	    //  I don't think we need to say that we saw something here. There must be a get call somewhere to view the page.
		applicationForm.addApplicationUpdate(new ApplicationFormUpdate(applicationForm, ApplicationUpdateScope.ALL_USERS, new Date()));
	    applicationForm.setLastUpdated(new Date());
	    accessService.updateAccessTimestamp(applicationForm, currentUser, new Date());
	    
	    accessService.registerApplicationUpdate(applicationForm, new Date(), ApplicationUpdateScope.ALL_USERS);
        applicationsService.save(applicationForm);
	}

}
