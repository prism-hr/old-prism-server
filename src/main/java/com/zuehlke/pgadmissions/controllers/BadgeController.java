package com.zuehlke.pgadmissions.controllers;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.dao.BadgeDAO;
import com.zuehlke.pgadmissions.domain.Badge;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.Environment;
import com.zuehlke.pgadmissions.validators.BadgeValidator;

@Controller
@RequestMapping(value = { "/badge" })
public class BadgeController {

	private static final String BADGE = "private/staff/superAdmin/badge";
	private static final String BADGE_MANAGEMENT = "private/staff/superAdmin/badge_management";
	private static final String CONFIGURATION_VIEW_NAME = "/private/staff/superAdmin/configuration";
	private final UserService userService;
	private final ProgramsService programsService;
    private BadgeDAO badgeDAO;
    private final ProgramPropertyEditor programFormPropertyEditor;
    private final DatePropertyEditor datePropertyEditor;
    private final BadgeValidator badgeValidator;

	BadgeController() {
		this(null, null, null, null, null, null);
	}

	@Autowired
	public BadgeController(UserService userService, ProgramsService programsService, BadgeDAO badgeDAO, 
	        DatePropertyEditor datePropertyEditor, ProgramPropertyEditor programFormPropertyEditor,
	        BadgeValidator badgeValidator) {

	    this.badgeValidator = badgeValidator;
		this.userService = userService;
		this.programsService = programsService;
        this.badgeDAO = badgeDAO;
        this.datePropertyEditor = datePropertyEditor;
        this.programFormPropertyEditor = programFormPropertyEditor;
	}

	@InitBinder("badge")
    public void registerPropertyEditors(WebDataBinder binder) {
	    binder.setValidator(badgeValidator);
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(Program.class, programFormPropertyEditor);
    }
	
	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}
	
	@ModelAttribute("badge")
	public Badge getNewBadge() {
	    return new Badge();
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

	@RequestMapping (value ="/saveBadge", method = RequestMethod.POST)
	public String saveBadgeDetails(@ModelAttribute(value="badge") @Valid Badge badge, BindingResult result) {
	    if (!result.hasErrors()) {
	        badgeDAO.save(badge);
	    }
	    return CONFIGURATION_VIEW_NAME;
	}
}
