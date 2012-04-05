package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.validators.DocumentValidator;

@Controller
@RequestMapping("/documents")
public class FileUploadController {

	private final ApplicationsService applicationService;
	private final DocumentValidator documentValidator;
	private final DocumentService documentService;

	FileUploadController() {
		this(null, null, null);

	}

	@Autowired
	public FileUploadController(ApplicationsService applicationService, DocumentValidator documentValidator, DocumentService documentService) {
		this.applicationService = applicationService;
		this.documentValidator = documentValidator;
		this.documentService = documentService;

	}

	@RequestMapping(method = RequestMethod.POST)
	@Deprecated
	public ModelAndView uploadFile(@ModelAttribute("applicationForm") ApplicationForm applicationForm, @RequestParam("resume") MultipartFile resume,
			@RequestParam("personalStatement") MultipartFile personalStatement) throws IOException {

		ModelMap modelMap = new ModelMap();
		Document document = newDocument();
		int errorsCount = 0;
		if (resume != null) {
			document.setFileName(resume.getOriginalFilename());
			document.setContentType(resume.getContentType());
			document.setContent(resume.getBytes());
			document.setType(DocumentType.CV);
			BindingResult errors = newErrors(document);
			documentValidator.validate(document, errors);
			if (errors.hasFieldErrors("fileName")) {
				modelMap.put("uploadErrorCode", errors.getFieldError("fileName").getCode());
				errorsCount++;
			} else {
				applicationForm.getSupportingDocuments().add(document);
			}
		}

		if (personalStatement != null) {
			document = newDocument();
			document.setFileName(personalStatement.getOriginalFilename());
			document.setContentType(personalStatement.getContentType());
			document.setContent(personalStatement.getBytes());
			document.setType(DocumentType.PERSONAL_STATEMENT);
			BindingResult errors = newErrors(document);
			documentValidator.validate(document, errors);
			if (errors.hasFieldErrors("fileName")) {
				modelMap.put("uploadTwoErrorCode", errors.getFieldError("fileName").getCode());
			} else {
				applicationForm.getSupportingDocuments().add(document);
			}
		}
		if (errorsCount == 0) {
			applicationService.save(applicationForm);
		}
		modelMap.put("id", applicationForm.getId());
		return new ModelAndView("redirect:/application", modelMap);
	}

	BindingResult newErrors(Document document) {
		return new DirectFieldBindingResult(document, document.getFileName());
	}

	Document newDocument() {
		return new Document();
	}

	@ModelAttribute("applicationForm")
	public ApplicationForm getApplicationForm(@RequestParam(required=false)Integer id) {
		if(id  == null){
			return null;
		}
		ApplicationForm applicationform = applicationService.getApplicationById(id);
		if (applicationform == null || !SecurityContextHolder.getContext().getAuthentication().getDetails().equals(applicationform.getApplicant())) {
			throw new ResourceNotFoundException();
		}
		if (applicationform.isSubmitted()) {
			throw new CannotUpdateApplicationException();
		}
		return applicationform;
	}
	

	@ModelAttribute
	public Document getDocument(@RequestParam( value="file", required=false) MultipartFile multipartFile) throws IOException {		 
		if(multipartFile == null){
			return null;
		}
		Document document = new Document();
		document.setFileName(multipartFile.getOriginalFilename());
		document.setContentType(multipartFile.getContentType());
		document.setContent(multipartFile.getBytes());
		document.setType(DocumentType.PROOF_OF_AWARD);
		document.setUploadedBy((RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails());
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
