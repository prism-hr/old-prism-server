package com.zuehlke.pgadmissions.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.ProgrammeDetailsService;
import com.zuehlke.pgadmissions.services.SupervisorService;

@Controller
@RequestMapping("/programme/updateSupervisor")
public class UpdateSupervisorController {

	private final ProgrammeDetailsService programmeDetailsService;
	private final SupervisorService supervisorService;
	
	UpdateSupervisorController() {
		this(null, null);
	}
	
	@Autowired
	public UpdateSupervisorController(ProgrammeDetailsService programmeDetailsService, SupervisorService supervisorService) {
		this.programmeDetailsService = programmeDetailsService;
		this.supervisorService = supervisorService;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String editSupervisor(@ModelAttribute("programmeDetails") ProgrammeDetails programmeDetails, 
			@ModelAttribute("supervisor") Supervisor supervisor, BindingResult result) {
		
		if (programmeDetails.getApplication() != null && programmeDetails.getApplication().isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}
		
//		//supervisor validation 
		supervisorService.save(supervisor);
		
		if (programmeDetails.getApplication() != null) {
			programmeDetails.getApplication().setProgrammeDetails(programmeDetails);
		}
		return "redirect:/update/getProgrammeDetails?applicationId=" + programmeDetails.getApplication().getId();
	}
	
	@ModelAttribute("programmeDetails")
	public ProgrammeDetails getProgrammeDetails(Integer programmeDetailsId) {
		if (programmeDetailsId == null) {
			return newProgrammeDetail();
		}
		ProgrammeDetails programmeDetails = programmeDetailsService.getProgrammeDetailsById(programmeDetailsId);
		if (programmeDetails == null) {
			throw new ResourceNotFoundException();
		}
		
		return programmeDetails;
	}
	
	@ModelAttribute("supervisor")
	public Supervisor getSupervisor(Integer supervisorId) {
		Supervisor supervisor = supervisorService.getSupervisorWithId(supervisorId);
		if (supervisor == null) {
			throw new ResourceNotFoundException();
		}
		return supervisor;
	}
	
	ProgrammeDetails newProgrammeDetail() {
		return new ProgrammeDetails();
	}
	
}
