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
import com.zuehlke.pgadmissions.services.QualificationService;

@Controller
@RequestMapping("/deleteentity")
public class DeleteApplicationFormEntitiesController {

	private final ApplicationsService applicationsService;
	private final QualificationService qualificationService;

	DeleteApplicationFormEntitiesController() {
		this(null, null);
	}

	@Autowired
	public DeleteApplicationFormEntitiesController(ApplicationsService applicationsService, QualificationService qualificationService) {
		this.applicationsService = applicationsService;
		this.qualificationService = qualificationService;

	}

	@RequestMapping(value = "/address", method = RequestMethod.POST)
	public ModelAndView deleteAddress(@RequestParam Integer id) {
		Address addressById = applicationsService.getAddressById(id);
		Integer applicationFormId = addressById.getApplication().getId();
		applicationsService.deleteAddress(addressById);
		return new ModelAndView("redirect:/application", "id", applicationFormId);
	}

	@RequestMapping(value = "/qualification", method = RequestMethod.POST)
	public ModelAndView deleteQualification(@RequestParam Integer id) {
		Qualification qualification = qualificationService.getQualificationById(id);
		Integer applicationFormId = qualification.getApplication().getId();
		qualificationService.delete(qualification);
		return new ModelAndView("redirect:/application", "id", applicationFormId);
	}
	
	@RequestMapping(value = "/funding", method = RequestMethod.POST)
	public ModelAndView deleteFunding(@RequestParam Integer id) {
		Funding funding = applicationsService.getFundingById(id);
		Integer applicationFormId = funding.getApplication().getId();
		applicationsService.deleteFunding(funding);
		return new ModelAndView("redirect:/application", "id", applicationFormId);
	}

	@RequestMapping(value = "/employment", method = RequestMethod.POST)
	public ModelAndView deleteEmployment(@RequestParam Integer id) {
		EmploymentPosition position = applicationsService.getEmploymentPositionById(id);
		Integer applicationFormId = position.getApplication().getId();
		applicationsService.deleteEmployment(position);
		return new ModelAndView("redirect:/application", "id", applicationFormId);
	}

	@RequestMapping(value = "/referee", method = RequestMethod.POST)
	public ModelAndView deleteReferee(@RequestParam Integer id) {
		Referee referee = applicationsService.getRefereeById(id);
		Integer applicationFormId = referee.getApplication().getId();
		applicationsService.deleteReferee(referee);
		return new ModelAndView("redirect:/application", "id", applicationFormId);
	}

}
