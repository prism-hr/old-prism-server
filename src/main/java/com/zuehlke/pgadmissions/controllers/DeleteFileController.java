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

	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}
	
	@RequestMapping(value="asyncdelete", method = RequestMethod.POST)
	public ModelAndView asyncdelete(@RequestParam("documentId") Integer documentId) {
		Document document = documentService.getDocumentById(documentId);	
		if(document != null && getCurrentUser().equals(document.getUploadedBy()) ){
			documentService.delete(document);
		}
		return new ModelAndView("/private/common/simpleMessage", "message", "ok");


	}
}
