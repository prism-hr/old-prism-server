package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.DocumentService;

@Controller
@RequestMapping("/delete")
public class DeleteFileController {

	private final DocumentService documentService;

	DeleteFileController() {
		this(null);
	}

	@Autowired
	public DeleteFileController(DocumentService documentService) {
		this.documentService = documentService;

	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView delete(@RequestParam("documentId") Integer documentId) {
		Document document = documentService.getDocumentById(documentId);

		if (document == null || !((RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails()).equals(document.getApplicationForm().getApplicant())) {
			throw new ResourceNotFoundException();
		}
		if(document.getApplicationForm().isSubmitted()){
			throw new CannotUpdateApplicationException();
		}
		documentService.delete(document);
		return new ModelAndView("redirect:/application", "id", document.getApplicationForm().getId());

	}
}
