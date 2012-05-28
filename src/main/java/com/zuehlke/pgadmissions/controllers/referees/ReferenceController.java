package com.zuehlke.pgadmissions.controllers.referees;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.Reference;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ReferenceValidator;

@Controller
@RequestMapping("/referee")
public class ReferenceController {
	private static final String ADD_REFERENCES_VIEW_NAME = "private/referees/upload_references";
	private static final String EXPIRED_VIEW_NAME = "private/referees/upload_references_expired";
	private final ApplicationsService applicationsService;
	private final DocumentPropertyEditor documentPropertyEditor;
	private final ReferenceValidator referenceValidator;
	private final RefereeService refereeService;
	private final UserService userService;

	ReferenceController() {
		this(null, null, null, null, null);
	}

	@Autowired
	public ReferenceController(ApplicationsService applicationsService, RefereeService refereeService, UserService userService, DocumentPropertyEditor documentPropertyEditor, ReferenceValidator referenceValidator) {
		this.applicationsService = applicationsService;
		this.refereeService = refereeService;
		this.userService = userService;
		this.documentPropertyEditor = documentPropertyEditor;
		this.referenceValidator = referenceValidator;
	}

	@ModelAttribute
	public ApplicationForm getApplicationForm(@RequestParam String application) {
		ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(application);
		if (applicationForm == null || !getCurrentUser().isRefereeOfApplicationForm(applicationForm)) {
			throw new ResourceNotFoundException();
		}
		return applicationForm;
	}

	RegisteredUser getCurrentUser() {
		RegisteredUser currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		return userService.getUser(currentUser.getId());
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {				
		return getCurrentUser();
	}

	@RequestMapping(value = "/addReferences", method = RequestMethod.GET)
	public String getUploadReferencesPage(@ModelAttribute ApplicationForm applicationForm) {
		if (applicationForm.isDecided()) {
			return EXPIRED_VIEW_NAME;
		}
		return ADD_REFERENCES_VIEW_NAME;
	}

	@ModelAttribute
	public Reference getReference(@RequestParam String application) {
		Referee referee = getCurrentUser().getRefereeForApplicationForm(getApplicationForm(application));
		if (referee.getReference() == null) {
			
			Reference reference = new Reference();
			reference.setReferee(referee);
			return reference;
		}
		return referee.getReference();
	}
	
	@InitBinder(value = "reference")
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.setValidator(referenceValidator);
		binder.registerCustomEditor(Document.class, documentPropertyEditor);

	}
	
	@RequestMapping(value = "/submitReference", method = RequestMethod.POST)
	public String handleReferenceSubmission(@Valid Reference reference, BindingResult bindingResult) {
		if(bindingResult.hasErrors()){
			return ADD_REFERENCES_VIEW_NAME;
		}
		Referee referee = reference.getReferee();
		referee.setReference(reference);
		refereeService.saveReferenceAndSendMailNotifications(referee);
		return "redirect:/addReferences/referenceuploaded";
	}

}
