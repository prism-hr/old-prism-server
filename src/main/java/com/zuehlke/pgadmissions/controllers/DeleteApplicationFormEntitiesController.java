package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
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

	DeleteApplicationFormEntitiesController() {
		this(null, null, null, null);
	}

	@Autowired
	public DeleteApplicationFormEntitiesController(
			QualificationService qualificationService,
			EmploymentPositionService employmentService,
			FundingService fundingService, RefereeService refereeService) {

		this.qualificationService = qualificationService;
		this.employmentService = employmentService;
		this.fundingService = fundingService;
		this.refereeService = refereeService;

	}

	@RequestMapping(value = "/qualification", method = RequestMethod.POST)
	public String deleteQualification(@RequestParam Integer id) {
		Qualification qualification = qualificationService
				.getQualificationById(id);
		Integer applicationFormId = qualification.getApplication().getId();
		qualificationService.delete(qualification);

		return "redirect:/update/getQualification?applicationId="
				+ applicationFormId + "&message=deleted";
	}

	@RequestMapping(value = "/funding", method = RequestMethod.POST)
	public String deleteFunding(@RequestParam Integer id) {
		Funding funding = fundingService.getFundingById(id);
		Integer applicationFormId = funding.getApplication().getId();
		fundingService.delete(funding);
		return "redirect:/update/getFunding?applicationId=" + applicationFormId
				+ "&message=deleted";
	}

	@RequestMapping(value = "/employment", method = RequestMethod.POST)
	public String deleteEmployment(@RequestParam Integer id) {
		EmploymentPosition position = employmentService
				.getEmploymentPositionById(id);
		Integer applicationFormId = position.getApplication().getId();
		employmentService.delete(position);
		return "redirect:/update/getEmploymentPosition?applicationId="
				+ applicationFormId + "&message=deleted";
	}

	@RequestMapping(value = "/referee", method = RequestMethod.POST)
	public String deleteReferee(@RequestParam Integer id) {
		Referee referee = refereeService.getRefereeById(id);
		Integer applicationFormId = referee.getApplication().getId();
		refereeService.delete(referee);
		return "redirect:/update/getReferee?applicationId=" + applicationFormId
				+ "&message=deleted";
	}

}
