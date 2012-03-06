package com.zuehlke.pgadmissions.controllers;

import java.util.Date;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.Funding;
import com.zuehlke.pgadmissions.dto.PersonalDetails;
import com.zuehlke.pgadmissions.dto.QualificationDTO;
import com.zuehlke.pgadmissions.exceptions.AccessDeniedException;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.pagemodels.PageModel;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AddressValidator;
import com.zuehlke.pgadmissions.validators.FundingValidator;
import com.zuehlke.pgadmissions.validators.PersonalDetailsValidator;
import com.zuehlke.pgadmissions.validators.QualificationValidator;

@Controller
@RequestMapping("/update")
public class UpdateApplicationFormController {

	private static final String APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME = "wip/qualifications";
	private static final String APPLICATION_ADDRESS_APPLICANT_VIEW_NAME = "private/pgStudents/form/components/address_details";
	private final ApplicationsService applicationService;
	private final UserService userService;
	private final UserPropertyEditor userPropertyEditor;
	private QualificationValidator qualificationValidator;
	private final DatePropertyEditor datePropertyEditor;

	UpdateApplicationFormController() {
		this(null, null, null, null, null);
	}

	@Autowired
	public UpdateApplicationFormController(UserService userService, ApplicationsService applicationService,
			UserPropertyEditor userPropertyEditor, DatePropertyEditor datePropertyEditor, QualificationValidator qualificationValidator) {
		this.applicationService = applicationService;
		this.userPropertyEditor = userPropertyEditor;
		this.userService = userService;
		this.datePropertyEditor = datePropertyEditor;
		this.qualificationValidator = qualificationValidator;

	}

	@RequestMapping(value = "/editPersonalDetails", method = RequestMethod.POST)
	@Transactional
	public ModelAndView editPersonalDetails(@ModelAttribute PersonalDetails personalDetails, @RequestParam Integer id,
			@RequestParam Integer appId, BindingResult result, ModelMap modelMap) {

		ApplicationForm application = applicationService.getApplicationById(appId);

		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}

		PersonalDetailsValidator personalDetailsValidator = new PersonalDetailsValidator();
		personalDetailsValidator.validate(personalDetails, result);

		RegisteredUser user = userService.getUser(id);
		if (!user.equals(SecurityContextHolder.getContext().getAuthentication().getDetails())) {
			throw new AccessDeniedException();
		}

		if (!result.hasErrors()) {
			user.setLastName(personalDetails.getLastName());
			user.setFirstName(personalDetails.getFirstName());
			user.setEmail(personalDetails.getEmail());
			userService.save(user);
		}

		ApplicationPageModel model = new ApplicationPageModel();
		model.setUser(user);
		model.setApplicationForm(application);
		model.setPersonalDetails(personalDetails);
		model.setResult(result);
		modelMap.put("model", model);
		modelMap.put("formDisplayState", "open");

		return new ModelAndView("private/pgStudents/form/components/personal_details", modelMap);
	}

	@RequestMapping(value = "/editQualification", method = RequestMethod.POST)
	public ModelAndView editQualification(@ModelAttribute QualificationDTO qual, @RequestParam Integer appId,
			BindingResult result) {
		qualificationValidator.validate(qual, result);
		ApplicationForm application = applicationService.getApplicationById(appId);
		if (!result.hasErrors()) {

			if (application.isSubmitted()) {
				throw new CannotUpdateApplicationException();
			}
			Qualification qualification;
			if (qual.getQualId() == null) {
				qualification = newQualification();
				qualification.setApplication(application);
				application.getQualifications().add(qualification);
			} else {
				qualification = applicationService.getQualificationById(qual.getQualId());
			}
			qualification.setAward_date(qual.getAward_date());
			qualification.setCountry(qual.getCountry());
			qualification.setGrade(qual.getGrade());
			qualification.setInstitution(qual.getInstitution());
			qualification.setLanguage_of_study(qual.getLanguage_of_study());
			qualification.setLevel(qual.getLevel());
			qualification.setName_of_programme(qual.getName_of_programme());
			qualification.setScore(qual.getScore());
			qualification.setStart_date(qual.getStart_date());
			qualification.setTermination_date(qual.getTermination_date());
			qualification.setQualification_termination_reason(qual.getTermination_reason());
			qualification.setQualification_type(qual.getType());

			applicationService.save(application);
		}

		PageModel model = new PageModel();
		model.setApplicationForm(application);
		model.setUser(((RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails()));
		model.setResult(result);
		return new ModelAndView(APPLICATION_QUALIFICATION_APPLICANT_VIEW_NAME, "model", model);
	}

	@RequestMapping(value = "/addFunding", method = RequestMethod.POST)
	@Transactional
	public ModelAndView addFunding(@ModelAttribute Funding fund, @RequestParam Integer id, @RequestParam Integer appId,
			BindingResult result, ModelMap modelMap) {
		ApplicationForm application = applicationService.getApplicationById(appId);

		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}

		FundingValidator fundingValidator = new FundingValidator();
		fundingValidator.validate(fund, result);
		if (!result.hasErrors()) {
			application.setFunding(fund.getFunding());
			applicationService.save(application);
		}

		RegisteredUser user = userService.getUser(id);

		ApplicationPageModel model = new ApplicationPageModel();
		model.setUser(user);
		ApplicationForm applicationForm = application;
		model.setApplicationForm(applicationForm);
		model.setFunding(fund);
		model.setResult(result);
		modelMap.put("model", model);

		return new ModelAndView("private/pgStudents/form/components/funding_details", modelMap);
	}

	@InitBinder
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(RegisteredUser.class, userPropertyEditor);
		binder.registerCustomEditor(Date.class, datePropertyEditor);

	}

	@RequestMapping(value = "/editAddress", method = RequestMethod.POST)
	@Transactional
	public ModelAndView editAddress(@ModelAttribute Address addr, @RequestParam Integer id,
			@RequestParam Integer appId, BindingResult result, ModelMap modelMap) {

		ApplicationForm application = applicationService.getApplicationById(appId);
		if (application.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}

		RegisteredUser user = userService.getUser(id);

		AddressValidator addressValidator = new AddressValidator();
		addressValidator.validate(addr, result);
		if (!result.hasErrors()) {
			com.zuehlke.pgadmissions.domain.Address address = new com.zuehlke.pgadmissions.domain.Address();
			address.setApplication(application);
			address.setLocation(addr.getLocation());
			address.setPostCode(addr.getPostCode());
			address.setCountry(addr.getCountry());
			address.setPurpose(addr.getPurpose());
			//TODO
			address.setStartDate(new Date());
			address.setEndDate(new Date());
			//TODO
			application.getAddresses().add(address);
			applicationService.save(application);
		}

		ApplicationPageModel model = new ApplicationPageModel();
		ApplicationForm applicationForm = application;
		model.setUser(user);
		model.setApplicationForm(applicationForm);
		model.setAddress(addr);
		model.setResult(result);
		modelMap.put("model", model);

		return new ModelAndView(APPLICATION_ADDRESS_APPLICANT_VIEW_NAME, modelMap);
	}

	ApplicationForm newApplicationForm() {
		return new ApplicationForm();
	}

	Qualification newQualification() {
		return new Qualification();
	}

}
