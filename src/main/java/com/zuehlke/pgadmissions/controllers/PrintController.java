package com.zuehlke.pgadmissions.controllers;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;

@Controller
@RequestMapping("/print")
public class PrintController {
	
	private final ApplicationsService applicationSevice;

	public PrintController(){
		this(null);
	}
	
	@Autowired
	public PrintController(ApplicationsService applicationSevice){
		this.applicationSevice = applicationSevice;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView printPage(@ModelAttribute("applicationFormId") ApplicationForm application) throws IOException, DocumentException {
		Document document = new Document(PageSize.A4, 50, 50, 50, 50);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(""+application.getId()+".pdf"));
		document.open();
		document.add(new Paragraph("Application id: " + application.getId()));
		document.add(new Paragraph("Program name: " + application.getProject().getProgram().getTitle()));
		document.add(new Paragraph("Project name: " + application.getProject().getTitle()));

		document.close();
		return null;
	}
	
	@ModelAttribute("applicationFormId")
	public ApplicationForm getApplication(Integer applicationFormId) {
		ApplicationForm applicationForm = applicationSevice.getApplicationById(applicationFormId);
		if (applicationForm == null) {
			throw new ResourceNotFoundException();
		}
		return applicationForm;
	}

}
