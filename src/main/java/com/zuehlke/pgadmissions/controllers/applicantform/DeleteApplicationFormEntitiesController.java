package com.zuehlke.pgadmissions.controllers.applicantform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.EmploymentPositionService;
import com.zuehlke.pgadmissions.services.FundingService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.services.RefereeService;

@Controller
@RequestMapping("/deleteentity")
public class DeleteApplicationFormEntitiesController {

	private final QualificationService qualificationService;
	private final EmploymentPositionService employmentService;
	private final FundingService fundingService;
	private final RefereeService refereeService;
	private final EncryptionHelper encryptionHelper;

	DeleteApplicationFormEntitiesController() {
		this(null, null, null, null, null);
	}

	@Autowired
	public DeleteApplicationFormEntitiesController(QualificationService qualificationService, EmploymentPositionService employmentService,
			FundingService fundingService, RefereeService refereeService, EncryptionHelper encryptionHelper) {

		this.qualificationService = qualificationService;
		this.employmentService = employmentService;
		this.fundingService = fundingService;
		this.refereeService = refereeService;
		this.encryptionHelper = encryptionHelper;

	}

	@RequestMapping(value = "/qualification", method = RequestMethod.POST)
	public String deleteQualification(@RequestParam("id") String encryptedQualificationId) {
		Qualification qualification = qualificationService.getQualificationById(encryptionHelper.decryptToInteger(encryptedQualificationId));
		qualificationService.delete(qualification);

		return "redirect:/update/getQualification?applicationId=" + qualification.getApplication().getApplicationNumber() + "&message=deleted";
	}

	@RequestMapping(value = "/funding", method = RequestMethod.POST)
	public String deleteFunding(@RequestParam("id") String encryptedFundingId) {
		Funding funding = fundingService.getFundingById(encryptionHelper.decryptToInteger(encryptedFundingId));
		fundingService.delete(funding);
		return "redirect:/update/getFunding?applicationId=" + funding.getApplication().getApplicationNumber() + "&message=deleted";
	}

	@RequestMapping(value = "/employment", method = RequestMethod.POST)
	public String deleteEmployment(@RequestParam("id") String encryptedEmploymentId) {
		EmploymentPosition position = employmentService.getEmploymentPositionById(encryptionHelper.decryptToInteger(encryptedEmploymentId));
		employmentService.delete(position);
		return "redirect:/update/getEmploymentPosition?applicationId=" + position.getApplication().getApplicationNumber() + "&message=deleted";
	}

	@RequestMapping(value = "/referee", method = RequestMethod.POST)
	public String deleteReferee(@RequestParam("id") String encrypedRefereeId) {
		Integer id = encryptionHelper.decryptToInteger(encrypedRefereeId);
		Referee referee = refereeService.getRefereeById(id);
		refereeService.delete(referee);
		return "redirect:/update/getReferee?applicationId=" + referee.getApplication().getApplicationNumber() + "&message=deleted";
	}

}
