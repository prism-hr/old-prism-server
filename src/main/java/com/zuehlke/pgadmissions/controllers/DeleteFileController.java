package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/delete")
public class DeleteFileController {

	private final DocumentService documentService;
	private final UserService userService;
	private final EncryptionHelper encryptionHelper;
	private final ApplicationsService applicationsService;

	DeleteFileController() {
		this(null, null, null, null);
	}

	@Autowired
	public DeleteFileController(DocumentService documentService, UserService userService, ApplicationsService applicationsService, EncryptionHelper encryptionHelper) {
		this.documentService = documentService;
		this.userService = userService;
		this.applicationsService = applicationsService;
		this.encryptionHelper = encryptionHelper;

	}

	@RequestMapping(value="asyncdelete", method = RequestMethod.POST)
	public ModelAndView asyncdelete(@RequestParam("documentId") String encryptedDocumentId) {
		Document document = documentService.getDocumentById(encryptionHelper.decryptToInteger(encryptedDocumentId));	
		if(document != null && document.getUploadedBy() != null && userService.getCurrentUser().getId().equals(document.getUploadedBy().getId())) {
			documentService.delete(document);
		}
		return new ModelAndView("/private/common/simpleMessage", "message", "document.deleted");
	}
	
	@RequestMapping(value="deletePersonalStatement", method = RequestMethod.POST)
	public String deletePersonalStatement(@RequestParam("application") String applicationNumber) {
		documentService.deletePersonalStatement(applicationsService.getApplicationByApplicationNumber(applicationNumber));
		return "/private/common/ajax_OK";
	}

	@RequestMapping(value="deleteCV", method = RequestMethod.POST)
	public String deleteCV(@RequestParam("application") String applicationNumber) {
		documentService.deleteCV(applicationsService.getApplicationByApplicationNumber(applicationNumber));
		return "/private/common/ajax_OK";
	}
}
