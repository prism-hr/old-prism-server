package com.zuehlke.pgadmissions.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.zuehlke.pgadmissions.domain.Badge;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.BadgeService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.Environment;
import com.zuehlke.pgadmissions.validators.BadgeValidator;

@Controller
@RequestMapping(value = { "/badge" })
public class BadgeController {

	private static final String BADGE = "private/staff/superAdmin/badge";
	private static final String BADGE_MANAGEMENT = "private/staff/superAdmin/badge_management";
	private final UserService userService;
	private final ProgramsService programsService;
    private final BadgeService badgeService;
    private final ProgramPropertyEditor programFormPropertyEditor;
    private final DatePropertyEditor datePropertyEditor;
    private final BadgeValidator badgeValidator;

	BadgeController() {
		this(null, null, null, null, null, null);
	}

	@Autowired
	public BadgeController(UserService userService, ProgramsService programsService, DatePropertyEditor datePropertyEditor, 
	        ProgramPropertyEditor programFormPropertyEditor, BadgeValidator badgeValidator, BadgeService badgeService) {
	    this.badgeValidator = badgeValidator;
		this.userService = userService;
		this.programsService = programsService;
        this.datePropertyEditor = datePropertyEditor;
        this.programFormPropertyEditor = programFormPropertyEditor;
        this.badgeService = badgeService;
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
	
    @RequestMapping(value = "/getClosingDates", method = RequestMethod.GET)
    @ResponseBody
    public String getClosingDatesJson(@RequestParam String program) {
        if (!(userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR) || userService.getCurrentUser().isInRole(Authority.ADMINISTRATOR))) {
            throw new ResourceNotFoundException();
        }
        
        List<Date> allClosingDates = new ArrayList<Date>();
        allClosingDates = badgeService.getAllClosingDatesByProgram(programsService.getProgramByCode(program));
        
        allClosingDates.add(org.apache.commons.lang.time.DateUtils.addDays(new Date(), -3));
        
        List<String> convertedDates = new ArrayList<String>();
        DateFormat format = new SimpleDateFormat("MMMM d, yyyy");
        for (Date date : allClosingDates) {
            try {
                convertedDates.add(format.format(date));
            } catch (Exception e) {
                // do nothing
            }
        }
        Gson gson = new Gson();
        return gson.toJson(convertedDates);
    }
    
    @RequestMapping(value = "/getProjectTitles", method = RequestMethod.GET)
    @ResponseBody
    public String getProjectTitlesJson(@RequestParam String program, @RequestParam String term) {
        if (!(userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR) || userService.getCurrentUser().isInRole(Authority.ADMINISTRATOR))) {
            throw new ResourceNotFoundException();
        }
        Program programme = programsService.getProgramByCode(program);
        List<String> projectTitles = badgeService.getAllProjectTitlesByProgramFilteredByNameLikeCaseInsensitive(programme, term);
        Gson gson = new Gson();
        return gson.toJson(projectTitles);
    }    
	
	@ModelAttribute("program")
	public Program getProgram(@RequestParam(required=false) String program) {
		return programsService.getProgramByCode(program);
	}
	
	@ModelAttribute("host")
	public String getHost() {
		return Environment.getInstance().getApplicationHostName();
	}

	@RequestMapping (value ="/saveBadge", method = RequestMethod.POST)
	public String saveBadgeDetails(@ModelAttribute(value="badge") @Valid Badge badge, BindingResult result) {
	    if (!result.hasErrors()) {
	        badgeService.save(badge);
	        return BADGE;
	    } else {
	        return BADGE_MANAGEMENT;
	    }
	}
}
