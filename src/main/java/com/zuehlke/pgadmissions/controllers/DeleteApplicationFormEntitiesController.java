package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.EmploymentPositionService;
import com.zuehlke.pgadmissions.services.FundingService;
import com.zuehlke.pgadmissions.services.QualificationService;

@Controller
@RequestMapping("/deleteentity")
public class DeleteApplicationFormEntitiesController {

	private final ApplicationsService applicationsService;
	private final QualificationService qualificationService;
	private final EmploymentPositionService employmentService;
	private final FundingService fundingService;

	DeleteApplicationFormEntitiesController() {
		this(null, null, null, null);
	}

	@Autowired
	public DeleteApplicationFormEntitiesController(ApplicationsService applicationsService, QualificationService qualificationService, EmploymentPositionService employmentService, FundingService fundingService) {
		this.applicationsService = applicationsService;
		this.qualificationService = qualificationService;
		this.employmentService = employmentService;
		this.fundingService = fundingService;

	}

	@RequestMapping(value = "/address", method = RequestMethod.POST)
	public ModelAndView deleteAddress(@RequestParam Integer id) {
		Address addressById = applicationsService.getAddressById(id);
		Integer applicationFormId = addressById.getApplication().getId();
		applicationsService.deleteAddress(addressById);
		return new ModelAndView("redirect:/application", "id", applicationFormId);
	}

	@RequestMapping(value = "/qualification", method = RequestMethod.POST)
	public String deleteQualification(@RequestParam Integer id) {
		Qualification qualification = qualificationService.getQualificationById(id);
		Integer applicationFormId = qualification.getApplication().getId();
		qualificationService.delete(qualification);
		
		return "redirect:/update/getQualification?applicationId=" +applicationFormId + "&message=deleted";
	}
	
	@RequestMapping(value = "/funding", method = RequestMethod.POST)
	public String deleteFunding(@RequestParam Integer id) {
		Funding funding = fundingService.getFundingById(id);
		Integer applicationFormId = funding.getApplication().getId();
		fundingService.delete(funding);
		return "redirect:/update/getFunding?applicationId=" +applicationFormId + "&message=deleted";
	}

	@RequestMapping(value = "/employment", method = RequestMethod.POST)
	public String deleteEmployment(@RequestParam Integer id) {
		EmploymentPosition position = employmentService.getEmploymentPositionById(id);
		Integer applicationFormId = position.getApplication().getId();
		employmentService.delete(position);
		return "redirect:/update/getEmploymentPosition?applicationId=" +applicationFormId 
				 + "&message=deleted";
	}

	@RequestMapping(value = "/referee", method = RequestMethod.POST)
	public ModelAndView deleteReferee(@RequestParam Integer id) {
		Referee referee = applicationsService.getRefereeById(id);
		Integer applicationFormId = referee.getApplication().getId();
		applicationsService.deleteReferee(referee);
		return new ModelAndView("redirect:/application", "id", applicationFormId);
	}

}
