package com.zuehlke.pgadmissions.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ProgramInstanceDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.InvalidParameterFormatException;
import com.zuehlke.pgadmissions.propertyeditors.PlainTextUserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/apply")
public class ApplicationFormController {

	private final ProgramDAO programDAO;
	private final ApplicationsService applicationService;
	private final PlainTextUserPropertyEditor userPropertyEditor;
	public static final String PROGRAM_DOES_NOT_EXIST = "private/pgStudents/programs/program_does_not_exist";
	private final ProgramInstanceDAO programInstanceDAO;
	private final UserService userService;

	ApplicationFormController() {
		this(null, null, null, null, null);
	}

	@Autowired
	public ApplicationFormController(ProgramDAO programDAO, ApplicationsService applicationService, PlainTextUserPropertyEditor userPropertyEditor,
			ProgramInstanceDAO programInstanceDAO, UserService userService) {
		this.programDAO = programDAO;
		this.applicationService = applicationService;
		this.userPropertyEditor = userPropertyEditor;
		this.programInstanceDAO = programInstanceDAO;
		this.userService = userService;
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public ModelAndView createNewApplicationForm(@RequestParam String program, @RequestParam String programDeadline, @RequestParam String projectTitle, @RequestParam String programhome) {
		Date batchDeadline = parseBatchDeadline(programDeadline);
		String researchHomePage =parseResearchHomePage(programhome);
		RegisteredUser user = userService.getCurrentUser();

		Program prog = programDAO.getProgramByCode(program);
		if (prog == null || programInstanceDAO.getActiveProgramInstances(prog).isEmpty()) {
			return new ModelAndView(PROGRAM_DOES_NOT_EXIST);
		}		
	
		ApplicationForm applicationForm = applicationService.createAndSaveNewApplicationForm(user, prog, batchDeadline, projectTitle, researchHomePage);

		return new ModelAndView("redirect:/application", "applicationId", applicationForm.getApplicationNumber());

	}

	private String parseResearchHomePage(String programhome) {
		String researchHomePage = null;
		if(StringUtils.isBlank(programhome)){
			return null;
		}
		if( !programhome.startsWith("http://") && !programhome.startsWith("https://")){
			researchHomePage = "http://" +  programhome;
		}else{
			researchHomePage = programhome;
		}
		if(!UrlValidator.getInstance().isValid(researchHomePage)){
			throw new InvalidParameterFormatException(programhome + " is not a valid URL");
		}
		return researchHomePage;
	}

	private Date parseBatchDeadline(String programDeadline) {
		Date batchDeadline = null;
		if(StringUtils.isBlank(programDeadline)){
			return null;
		}
		try {
			batchDeadline = new SimpleDateFormat("dd-MMM-yyyy").parse(programDeadline);
		} catch (ParseException e) {
			throw new InvalidParameterFormatException(e);
		}
		return batchDeadline;
	}

	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}

	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(RegisteredUser.class, userPropertyEditor);

	}
}
