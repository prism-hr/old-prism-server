package com.zuehlke.pgadmissions.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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

	public PrintController(){
		this(null);
	}

	@Autowired
	public PrintController(ApplicationsService applicationSevice){
		this.applicationSevice = applicationSevice;
	}

	@RequestMapping(method = RequestMethod.GET)
	public void printPage(HttpServletRequest request, HttpServletResponse response) {
		try {
			Integer applicationFormId = ServletRequestUtils.getIntParameter(request, "applicationFormId");
			ApplicationForm application = applicationSevice.getApplicationById(applicationFormId);
			if (application == null) {
				throw new ResourceNotFoundException();
			}


			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			PdfWriter writer = PdfWriter.getInstance(document, baos);
			PdfDocumentBuilder builder = new PdfDocumentBuilder(writer);

			document.open();

			//Un-comment this to try the html to pdf conversion

			//HTMLWorker htmlWorker = new HTMLWorker(document);
			//String htmlSource = buildHtml();
			//htmlWorker.parse(new StringReader(htmlSource));

			builder.buildDocument(application, document);
			document.close();

			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setHeader("Content-Disposition", "inline; filename=\"application"+applicationFormId+".pdf\"");
			response.setContentType("application/pdf");
			response.setContentLength(baos.size());
			ServletOutputStream out = response.getOutputStream();
			baos.writeTo(out); 
			out.flush();
			out.close();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public void printAll(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException, DocumentException, IOException {
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		response.setHeader("Content-Disposition", "attachment; filename=\"applications.zip\"");
		response.setContentType("application/zip");
		ServletOutputStream out = response.getOutputStream();
		 ZipOutputStream zip = new ZipOutputStream(out);
		
		String appListToPrint = ServletRequestUtils.getStringParameter(request, "appList");
		String[] applications = appListToPrint.split(";");


		if (StringUtils.isNotBlank(appListToPrint)) {
			for (String applicationId : applications) {
				ApplicationForm application = applicationSevice.getApplicationById(new Integer(applicationId));
				
				Document document = new Document(PageSize.A4, 50, 50, 50, 50);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				PdfWriter writer = PdfWriter.getInstance(document, baos);
				PdfDocumentBuilder builder = new PdfDocumentBuilder(writer);
				document.open();
				builder.buildDocument(application, document);
				document.close();
				
				zip.putNextEntry(new ZipEntry("application"+applicationId+".pdf"));
				zip.write(baos.toByteArray());
				zip.closeEntry();
			}
		} 

		

		zip.flush();        
		zip.close();
		
		out.flush();
		out.close();

	}
}
