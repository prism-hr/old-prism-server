package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.validators.DocumentValidator;

@Controller
@RequestMapping("/documents")
public class FileUploadController {



	final private ApplicationsService applicationService;
	private final DocumentValidator documentValidator;

	FileUploadController() {
		this(null, null);
	}

	
	@Autowired
	public FileUploadController(ApplicationsService applicationService, DocumentValidator documentValidator) {
		this.applicationService = applicationService;
		this.documentValidator = documentValidator;
	

	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView uploadFile(@ModelAttribute("applicationForm") ApplicationForm applicationForm, @RequestParam("documentType") DocumentType documentType,
			@RequestParam("file") MultipartFile multipartFile) throws IOException {

		Document document = newDocument();
		document.setFileName(multipartFile.getOriginalFilename());
		document.setContentType(multipartFile.getContentType());
		document.setContent(multipartFile.getBytes());
		document.setType(documentType);
		BindingResult errors = newErrors(document);
		documentValidator.validate(document, errors);
		ModelMap modelMap = new ModelMap();		
		if(errors.hasFieldErrors("fileName")){
			modelMap.put("uploadErrorCode", errors.getFieldError("fileName").getCode());
		}else{
			applicationForm.getSupportingDocuments().add(document);
			applicationService.save(applicationForm);
		}
		modelMap.put("id", applicationForm.getId());
		return new ModelAndView("redirect:/application", modelMap);

	}


	BindingResult newErrors(Document document) {
		return new DirectFieldBindingResult(document, "document");
	}

	Document newDocument() {
		return new Document();
	}

	@ModelAttribute("applicationForm") 
	public ApplicationForm getApplicationForm(Integer id) {
		ApplicationForm applicationform = applicationService.getApplicationById(id);
		if (applicationform == null || !SecurityContextHolder.getContext().getAuthentication().getDetails().equals(applicationform.getApplicant())) {
			throw new ResourceNotFoundException();
		}
		if (applicationform.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}
		return applicationform;
	}

	

}
