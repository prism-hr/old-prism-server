package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.security.ContentAccessProvider;
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
	private final ContentAccessProvider contentAccessProvider;

	DeleteFileController() {
		this(null, null, null, null, null);
	}

	@Autowired
	public DeleteFileController(DocumentService documentService, UserService userService, ApplicationsService applicationsService, 
			EncryptionHelper encryptionHelper, ContentAccessProvider contentAccessProvider) {
		this.documentService = documentService;
		this.userService = userService;
		this.applicationsService = applicationsService;
		this.encryptionHelper = encryptionHelper;
		this.contentAccessProvider = contentAccessProvider;
	}

	@RequestMapping(value="asyncdelete", method = RequestMethod.POST)
	public ModelAndView asyncdelete(@RequestParam("documentId") String encryptedDocumentId) {
		Document document = documentService.getDocumentById(encryptionHelper.decryptToInteger(encryptedDocumentId));
		contentAccessProvider.validateCanDeleteDocument(document, userService.getCurrentUser());
		documentService.delete(document);
		return new ModelAndView("/private/common/simpleMessage", "message", "document.deleted");
	}
	
	@RequestMapping(value="deletePersonalStatement", method = RequestMethod.POST)
	public String deletePersonalStatement(@RequestParam("application") String applicationNumber) {
		ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationNumber);
		contentAccessProvider.validateCanDeleteApplicationDocument(application, userService.getCurrentUser());
		documentService.deletePersonalStatement(application);
		return "/private/common/ajax_OK";
	}

	@RequestMapping(value="deleteCV", method = RequestMethod.POST)
	public String deleteCV(@RequestParam("application") String applicationNumber) {
		ApplicationForm application = applicationsService.getApplicationByApplicationNumber(applicationNumber);
		contentAccessProvider.validateCanDeleteApplicationDocument(application, userService.getCurrentUser());
		documentService.deleteCV(application);
		return "/private/common/ajax_OK";
	}
	
}