package com.zuehlke.pgadmissions.controllers;

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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.validators.DocumentSectionValidator;


@Controller
@RequestMapping("/update")
public class DocumentsController{

	private static final String STUDENTS_FORM_DOCUMENTS_VIEW = "/private/pgStudents/form/components/documents";
	private final ApplicationsService applicationsService;
	private final DocumentSectionValidator documentSectionValidator;
	private final DocumentPropertyEditor documentPropertyEditor;

	DocumentsController(){
		this(null, null, null);
	}
	
	@Autowired
	public DocumentsController(ApplicationsService applicationsService, DocumentSectionValidator documentSectionValidator,
			DocumentPropertyEditor documentPropertyEditor) {
				this.applicationsService = applicationsService;
				this.documentSectionValidator = documentSectionValidator;
				this.documentPropertyEditor = documentPropertyEditor;
	
	}

	@RequestMapping(value = "/editDocuments", method = RequestMethod.POST)
	public String editDocuments(@Valid ApplicationForm applicationForm, BindingResult result) {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		if(applicationForm.isSubmitted()){
			throw new CannotUpdateApplicationException();
		}
		if(result.hasErrors()){
			return STUDENTS_FORM_DOCUMENTS_VIEW;
		}
		applicationsService.save(applicationForm);
		return "redirect:/update/getDocuments?applicationId=" + applicationForm.getId();
			
	}

	@RequestMapping(value = "/getDocuments", method = RequestMethod.GET)
	public String getDocumentsView() {

		if (!getCurrentUser().isInRole(Authority.APPLICANT)) {
			throw new ResourceNotFoundException();
		}
		return STUDENTS_FORM_DOCUMENTS_VIEW;
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam Integer applicationId) {		
		ApplicationForm application = applicationsService.getApplicationById(applicationId);
		if(application == null || !getCurrentUser().canSee(application)){
			throw new ResourceNotFoundException();
		}
		return application;
	}

	@InitBinder(value="applicationForm")
	public void registerPropertyEditors(WebDataBinder binder) {
		binder.setValidator(documentSectionValidator);
		binder.registerCustomEditor(Document.class, documentPropertyEditor);
		
	}

	@ModelAttribute("message")
	public String getMessage(@RequestParam(required=false)String message) {		
		return message;
	}
	
	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}
}
