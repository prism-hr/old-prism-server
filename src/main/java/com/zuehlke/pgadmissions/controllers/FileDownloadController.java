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
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.DocumentService;

@Controller
@RequestMapping("/download")
public class FileDownloadController {

	private final DocumentService documentService;

	FileDownloadController() {
		this(null);
	}

	@Autowired
	public FileDownloadController(DocumentService documentService) {
		this.documentService = documentService;

	}

	@RequestMapping(method = RequestMethod.GET)
	public void download(@RequestParam("documentId") Integer documentId, HttpServletResponse response) throws IOException {
		Document document = documentService.getDocumentById(documentId);
		if (!((RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails()).canSee(document.getApplicationForm())) {
			throw new ResourceNotFoundException();
		}
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
