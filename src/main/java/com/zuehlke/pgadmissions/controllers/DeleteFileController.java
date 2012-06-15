package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/delete")
public class DeleteFileController {

	private final DocumentService documentService;
	private final UserService userService;
	private final EncryptionHelper encryptionHelper;

	DeleteFileController() {
		this(null, null, null);
	}

	@Autowired
	public DeleteFileController(DocumentService documentService, UserService userService, EncryptionHelper encryptionHelper) {
		this.documentService = documentService;
		this.userService = userService;
		this.encryptionHelper = encryptionHelper;

	}

	
	@RequestMapping(value="asyncdelete", method = RequestMethod.POST)
	public ModelAndView asyncdelete(@RequestParam("documentId") String encryptedDocumentId) {
		Document document = documentService.getDocumentById(encryptionHelper.decryptToInteger(encryptedDocumentId));	
		if(document != null && userService.getCurrentUser().equals(document.getUploadedBy()) ){
			documentService.delete(document);
		}
		return new ModelAndView("/private/common/simpleMessage", "message", "document.deleted");


	}
}
