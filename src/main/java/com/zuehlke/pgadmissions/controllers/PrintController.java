package com.zuehlke.pgadmissions.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.utils.PdfDocumentBuilder;

@Controller
@RequestMapping("/print")
public class PrintController {

	private final ApplicationsService applicationSevice;
	private final PdfDocumentBuilder builder;

	public PrintController(){
		this(null, null);
	}

	@Autowired
	public PrintController(ApplicationsService applicationSevice, PdfDocumentBuilder builder){
		this.applicationSevice = applicationSevice;
		this.builder = builder;
	}

	@RequestMapping(method = RequestMethod.GET)
	public Document printPage(HttpServletRequest request, HttpServletResponse response) {
		try {
			String applicationFormNumber = ServletRequestUtils.getStringParameter(request, "applicationFormId");
			ApplicationForm application = applicationSevice.getApplicationByApplicationNumber(applicationFormNumber);
			if (application == null) {
				throw new ResourceNotFoundException();
			}


			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			PdfWriter writer = PdfWriter.getInstance(document, baos);


			document.open();

			//Un-comment this to try the html to pdf conversion

			//HTMLWorker htmlWorker = new HTMLWorker(document);
			//String htmlSource = buildHtml();
			//htmlWorker.parse(new StringReader(htmlSource));

			builder.buildDocument(application, document, writer);
			document.close();

			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setHeader("Content-Disposition", "inline; filename=\"application"+applicationFormNumber+".pdf\"");
			response.setContentType("application/pdf");
			response.setContentLength(baos.size());
			ServletOutputStream out = response.getOutputStream();
			baos.writeTo(out); 
			out.flush();
			out.close();

			return document;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public Document printAll(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException, DocumentException, IOException {
		String appListToPrint = ServletRequestUtils.getStringParameter(request, "appList");
		String[] applications = appListToPrint.split(";");

		Document document = new Document(PageSize.A4, 50, 50, 50, 50);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PdfWriter writer = PdfWriter.getInstance(document, baos);
	

		document.open();
		for (String applicationId : applications) {
			ApplicationForm application = applicationSevice.getApplicationByApplicationNumber(applicationId);
			builder.buildDocument(application, document, writer);
			document.newPage();
		}

		document.close();

		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		response.setHeader("Content-Disposition", "attachment; filename=\"applications.pdf\"");
		response.setContentType("application/pdf");
		response.setContentLength(baos.size());
		ServletOutputStream out = response.getOutputStream();
		baos.writeTo(out); 
		out.flush();
		out.close();
		
		return document;
	}

}
