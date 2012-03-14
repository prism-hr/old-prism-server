package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
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

@Controller
@RequestMapping("/documents")
public class FileManagementController {



	final private ApplicationsService applicationService;

	FileManagementController() {
		this(null);
	}

	@Autowired
	public FileManagementController(ApplicationsService applicationService) {
		this.applicationService = applicationService;
	

	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView uploadFile(@ModelAttribute("applicationForm") ApplicationForm applicationForm, @RequestParam("documentType") DocumentType documentType,
			@RequestParam("file") MultipartFile multipartFile) throws IOException {

		Document document = new Document();
		document.setFileName(multipartFile.getOriginalFilename());
		document.setContentType(multipartFile.getContentType());
		document.setContent(multipartFile.getBytes());
		document.setType(documentType);
		applicationForm.getSupportingDocuments().add(document);
		applicationService.save(applicationForm);
		return new ModelAndView("redirect:/application", "id", applicationForm.getId());

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
