package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.dto.ProgrammeDetails;
import com.zuehlke.pgadmissions.exceptions.AccessDeniedException;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ProgrammeDetailsValidator;

public class ProgrammeDetailsController {

	private final ProgrammeDetailDAO programmeDetailDAO;
	private final ApplicationsService applicationService;
	private final UserService userService;
	
	ProgrammeDetailsController() {
		this(null, null, null);
	}
	
	@Autowired
	public ProgrammeDetailsController(ProgrammeDetailDAO programmeDetailDAO, ApplicationsService applicationService, UserService userService) {
		this.programmeDetailDAO = programmeDetailDAO;
		this.applicationService = applicationService;
		this.userService = userService;
	}
	
	@RequestMapping(value = "/editProgramme", method = RequestMethod.POST)
	public ModelAndView editPersonalDetails(@ModelAttribute ProgrammeDetails programme, @RequestParam Integer id1, @RequestParam Integer appId1,
			BindingResult result, ModelMap modelMap) {

		ApplicationForm application = applicationService.getApplicationById(appId1);

		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}

		ProgrammeDetailsValidator personalDetailsValidator = new ProgrammeDetailsValidator();
		personalDetailsValidator.validate(programme, result);

		RegisteredUser user = userService.getUser(id1);
		if (!user.equals(SecurityContextHolder.getContext().getAuthentication().getDetails())) {
			throw new AccessDeniedException();
		}

		if (!result.hasErrors()) {
			@SuppressWarnings("deprecation")
			ProgrammeDetail pd = programmeDetailDAO.getProgrammeDetailWithApplication(application);
			if (pd == null) {
				pd = new ProgrammeDetail();
			}

			pd.setProgrammeName(programme.getProgrammeDetailsProgrammeName());
			pd.setProjectName(programme.getProgrammeDetailsProjectName());
			pd.setStartDate(programme.getProgrammeDetailsStartDate());
			pd.setReferrer(Referrer.fromString(programme.getProgrammeDetailsReferrer()));
			pd.setStudyOption(StudyOption.fromString(programme.getProgrammeDetailsStudyOption()));
			pd.setApplication(application);

			programmeDetailDAO.save(pd);

		}

		ApplicationPageModel model = new ApplicationPageModel();
		model.setUser(user);
		model.setApplicationForm(application);
		model.setProgrammeDetails(programme);
		model.setStudyOptions(StudyOption.values());
		model.setReferrers(Referrer.values());
		model.setResult(result);
		modelMap.put("model", model);

		return new ModelAndView("private/pgStudents/form/components/programme_details", modelMap);
	}

}
