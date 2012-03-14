package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.services.DocumentService;

@Controller
@RequestMapping("/upload")
public class FileUploadController {

	private final DocumentService documentService;

	FileUploadController() {
		this(null);
	}


	@Autowired
	public FileUploadController(DocumentService documentService) {
		this.documentService = documentService;

	}

	@RequestMapping(method = RequestMethod.GET)
	public String getView() {
		return "public/upload";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String save(@ModelAttribute("document") Document document, @RequestParam("file") MultipartFile file) {

		System.out.println("Name:" + document.getFileName());
		System.out.println("File:" + file.getOriginalFilename());
		System.out.println("ContentType:" + file.getContentType());
		document.setFileName(file.getOriginalFilename());
		try {
			document.setContent(file.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		documentService.save(document);
		return "redirect:/upload?id="+ document.getId();
	}

	
	/*@RequestMapping("/download/{documentId}")
	public String download(@PathVariable("documentId") Integer documentId, HttpServletResponse response) {

		Document doc = documentService.getDocumentById(documentId);
		try {
			response.setHeader("Content-Disposition", "inline;filname=\"" + doc.getFileName() + "\"");
			OutputStream out = response.getOutputStream();
			out.write(doc.getContent());
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}*/
}
