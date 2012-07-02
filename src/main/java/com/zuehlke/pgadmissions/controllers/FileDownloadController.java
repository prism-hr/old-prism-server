package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.ReferenceService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/download")
public class FileDownloadController {

	private final DocumentService documentService;
	private final ReferenceService referenceService;
	private final UserService userService;
	private final EncryptionHelper encryptionHelper;

	FileDownloadController() {
		this(null, null, null, null);
	}

	@Autowired
	public FileDownloadController(DocumentService documentService, ReferenceService referenceService, UserService userService, EncryptionHelper encryptionHelper) {
		this.documentService = documentService;
		this.referenceService = referenceService;
		this.userService = userService;
		this.encryptionHelper = encryptionHelper;

	}

	@RequestMapping(method = RequestMethod.GET)
	public void downloadApplicationDocument(@RequestParam("documentId") String encryptedDocumentId, HttpServletResponse response) throws IOException {
		Document document = documentService.getDocumentById(encryptionHelper.decryptToInteger(encryptedDocumentId));
		if (DocumentType.REFERENCE == document.getType()) {
			throw new ResourceNotFoundException();
		}

		sendDocument(response, document);
	}

	@RequestMapping(value = "/reference", method = RequestMethod.GET)
	public void downloadReferenceDocument(@RequestParam("referenceId") String encryptedReferenceId, HttpServletResponse response) throws IOException {
		ReferenceComment reference = referenceService.getReferenceById(encryptionHelper.decryptToInteger(encryptedReferenceId));
		RegisteredUser currentUser =userService.getCurrentUser();
		if (reference == null || reference.getDocuments() == null || !currentUser.canSeeReference(reference)) {
			throw new ResourceNotFoundException();
		}
		Document document = reference.getDocuments().get(0);

		sendDocument(response, document);

	}

	private void sendDocument(HttpServletResponse response, Document document) throws IOException {
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		response.setHeader("Content-Disposition", "inline; filename=\"" + document.getFileName() + "\"");
		response.setContentType("application/pdf");
		response.setContentLength(document.getContent().length);
		OutputStream out = response.getOutputStream();
		try {
			out.write(document.getContent());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				out.flush();
			} catch (Exception e) {
				// ignore
			}
			try {
				out.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}

}
