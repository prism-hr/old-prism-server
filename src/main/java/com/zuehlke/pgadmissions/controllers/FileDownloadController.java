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
import com.zuehlke.pgadmissions.domain.Reference;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.ReferenceService;

@Controller
@RequestMapping("/download")
public class FileDownloadController {

	private final DocumentService documentService;
	private final ReferenceService referenceService;

	FileDownloadController() {
		this(null, null);
	}

	@Autowired
	public FileDownloadController(DocumentService documentService, ReferenceService referenceService) {
		this.documentService = documentService;
		this.referenceService = referenceService;

	}

	@RequestMapping(method = RequestMethod.GET)
	public void downloadApplicationDocument(@RequestParam("documentId") Integer documentId, HttpServletResponse response) throws IOException {
		Document document = documentService.getDocumentById(documentId);
		if (DocumentType.REFERENCE == document.getType()) {
			throw new ResourceNotFoundException();
		}

		sendDocument(response, document);
	}

	@RequestMapping(value = "/reference", method = RequestMethod.GET)
	public void downloadReferenceDocument(@RequestParam("referenceId") Integer referenceId, HttpServletResponse response) throws IOException {
		Reference reference = referenceService.getReferenceById(referenceId);
		RegisteredUser currentUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
		if (reference == null || reference.getDocument() == null || !currentUser.canSeeReference(reference)) {
			throw new ResourceNotFoundException();
		}
		Document document = reference.getDocument();

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
