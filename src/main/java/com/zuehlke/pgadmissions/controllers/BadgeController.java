package com.zuehlke.pgadmissions.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.Environment;

@Controller
@RequestMapping(value = { "/badge" })
public class BadgeController {

	private static final String BADGE = "private/staff/superAdmin/badge";
	private static final String BADGE_MANAGEMENT = "private/staff/superAdmin/badge_management";
	private final UserService userService;
	private final ProgramsService programsService;

	BadgeController() {
		this(null, null);
	}

	@Autowired
	public BadgeController(UserService userService, ProgramsService programsService) {

		this.userService = userService;
		this.programsService = programsService;
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

		
	@ModelAttribute("programs")
	public List<Program> getPrograms() {
		if (userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			return programsService.getAllPrograms();
		}
		return userService.getCurrentUser().getProgramsOfWhichAdministrator();

	}
	
	@RequestMapping( method = RequestMethod.GET)
	public String getCreateBadgePage() {
		if (!(userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR) || userService.getCurrentUser().isInRole(Authority.ADMINISTRATOR))) {
			throw new ResourceNotFoundException();
		}
		return BADGE_MANAGEMENT;
	}
	
	@ModelAttribute("program")
	public Program getProgram(@RequestParam(required=false) String program) {
		return programsService.getProgramByCode(program);
	}
	
	@ModelAttribute("host")
	public String getHost() {
		return Environment.getInstance().getApplicationHostName();
	}

	@RequestMapping( value="/html", method = RequestMethod.GET)
	public String getBadge() {
		if (!(userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR) || userService.getCurrentUser().isInRole(Authority.ADMINISTRATOR))) {
			throw new ResourceNotFoundException();
		}
		return BADGE;
	}

	
}
