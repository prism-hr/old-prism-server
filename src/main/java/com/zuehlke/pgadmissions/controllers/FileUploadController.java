package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.DocumentValidator;

@Controller
@RequestMapping("/documents")
public class FileUploadController {

	private final ApplicationsService applicationService;
	
	private final DocumentValidator documentValidator;
	
	private final DocumentService documentService;
	
	private final UserService userService;

	FileUploadController() {
		this(null, null, null, null);
	}

	@Autowired		
	public FileUploadController(ApplicationsService applicationService, DocumentValidator documentValidator, 
	        DocumentService documentService, UserService userService) {
		this.applicationService = applicationService;
		this.documentValidator = documentValidator;
		this.documentService = documentService;
		this.userService = userService;
	}
	
	BindingResult newErrors(Document document) {
		return new DirectFieldBindingResult(document, document.getFileName());
	}

	Document newDocument() {
		return new Document();
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam(required=false)String id) {
		if(id  == null){
			return null;
		}
		ApplicationForm applicationForm = applicationService.getApplicationByApplicationNumber(id);
		if (applicationForm == null || (applicationForm.getApplicant() != null && !userService.getCurrentUser().getId().equals(applicationForm.getApplicant().getId()))) {
			throw new ResourceNotFoundException();
		}
		if (applicationForm.isSubmitted()) {
			throw new CannotUpdateApplicationException(applicationForm.getApplicationNumber());
		}
		return applicationForm;
	}

	@ModelAttribute
	public Document getDocument(@RequestParam(value="file", required=false) MultipartFile multipartFile, 
	        @RequestParam(value="type", required=false) DocumentType documentType) throws IOException {		 
		
	    if (multipartFile == null) {
			return null;
		}
		
		Document document = new Document();
		document.setFileName(multipartFile.getOriginalFilename());
		document.setContentType(multipartFile.getContentType());
		document.setContent(multipartFile.getBytes());
		document.setType(documentType);
		document.setUploadedBy(userService.getCurrentUser());
		document.setFileData(multipartFile);
		
		return document;
	}
	
	@InitBinder(value="document")
	public void initBinder(WebDataBinder binder) {
		binder.setValidator(documentValidator);
	}
	
	@RequestMapping(value = "/async", method = RequestMethod.POST)
	public String uploadFileAsynchronously(@Valid Document document, BindingResult errors) {
		if(!errors.hasErrors()){
			documentService.save(document);
		}
		return "/private/common/parts/supportingDocument";
	}
}
