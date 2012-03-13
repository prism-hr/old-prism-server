package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.DocumentService;

@Controller
@RequestMapping("/documents")
public class FileManagementController {

	private final DocumentService documentService;
	private final ApplicationsService apService;

	FileManagementController() {
		this(null, null);
	}

	@Autowired
	public FileManagementController(DocumentService documentService, ApplicationsService apService) {
		this.documentService = documentService;
		this.apService = apService;

	}
	
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView uploadFile(@RequestParam("appId") Integer id,   @RequestParam("file") MultipartFile multipartFile) throws IOException {
		
		Document document = new Document();
		document.setFileName(multipartFile.getOriginalFilename());
		document.setContentType(multipartFile.getContentType());
		document.setContent(multipartFile.getBytes());
		documentService.save(document);
		return new ModelAndView("redirect:/application", "id",id );
	}

}
