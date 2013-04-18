package com.zuehlke.pgadmissions.controllers.applicantform;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.BooleanPropertyEditor;
import com.zuehlke.pgadmissions.services.AdditionalInfoService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.AdditionalInformationValidator;

@Controller
@RequestMapping("/update")
public class AdditionalInformationController {

	private static final String STUDENTS_FORM_ADDITIONAL_INFORMATION_VIEW = "/private/pgStudents/form/components/additional_information";
	private final AdditionalInfoService additionalService;
	private final ApplicationsService applicationService;
	private final AdditionalInformationValidator additionalInformationValidator;
	private final ApplicationFormPropertyEditor applicationFormPropertyEditor;
	private final BooleanPropertyEditor booleanPropertyEditor;
	private final UserService userService;
	
	AdditionalInformationController() {
		this(null, null, null, null, null, null);
	}

	@Autowired
	public AdditionalInformationController(ApplicationsService applicationService,
			UserService userService, ApplicationFormPropertyEditor applicationFormPropertyEditor,//
			BooleanPropertyEditor booleanEditor,//
			AdditionalInfoService addInfoServiceMock, AdditionalInformationValidator infoValidator) {
		this.applicationService = applicationService;
		this.userService = userService;
		this.applicationFormPropertyEditor = applicationFormPropertyEditor;
		this.booleanPropertyEditor = booleanEditor;
		this.additionalService = addInfoServiceMock;
		this.additionalInformationValidator = infoValidator;
	}

	@RequestMapping(value = "/editAdditionalInformation", method = RequestMethod.POST)
	public String editAdditionalInformation(@Valid AdditionalInformation info, BindingResult result) {
		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		if (info.getApplication().isDecided()) {
			throw new CannotUpdateApplicationException(info.getApplication().getApplicationNumber());
		}
		if (result.hasErrors()) {
			return STUDENTS_FORM_ADDITIONAL_INFORMATION_VIEW;
		}
		additionalService.save(info);
		info.getApplication().setLastUpdated(new Date());
		applicationService.save(info.getApplication());
		return "redirect:/update/getAdditionalInformation?applicationId=" + info.getApplication().getApplicationNumber();

	}

	@RequestMapping(value = "/getAdditionalInformation", method = RequestMethod.GET)
	public String getAdditionalInformationView() {
		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		return STUDENTS_FORM_ADDITIONAL_INFORMATION_VIEW;
	}

	@ModelAttribute("additionalInformation")
	public AdditionalInformation getAdditionalInformation(@RequestParam String applicationId) {
		ApplicationForm application = applicationService.getApplicationByApplicationNumber(applicationId);
		if (application == null || !getCurrentUser().canSee(application)) {
			throw new ResourceNotFoundException();
		}
		return application.getAdditionalInformation();
	}

	@ModelAttribute("message")
	public String getMessage(@RequestParam(required = false) String message) {
		return message;
	}

	private RegisteredUser getCurrentUser() {
		return userService.getCurrentUser();
	}

	@ModelAttribute("errorCode")
	public String getErrorCode(String errorCode) {
		return errorCode;
	}

	@InitBinder(value = "additionalInformation")
	public void registerValidatorsEditors(WebDataBinder binder) {
		binder.setValidator(additionalInformationValidator);
		binder.registerCustomEditor(String.class, newStringTrimmerEditor());
		binder.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditor);
		binder.registerCustomEditor(Boolean.class, booleanPropertyEditor);
	}
	
	public StringTrimmerEditor newStringTrimmerEditor() {
	    return new StringTrimmerEditor(false);
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
		ApplicationForm application = applicationService.getApplicationByApplicationNumber(applicationId);
		if (application == null || !getCurrentUser().canSee(application)) {
			throw new ResourceNotFoundException();
		}
		return application;
	}
}