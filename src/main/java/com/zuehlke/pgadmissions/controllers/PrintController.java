package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.itextpdf.text.DocumentException;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/print")
public class PrintController {

	private final ApplicationsService applicationSevice;
	private final PdfDocumentBuilder builder;
	private final UserService userService;

	public PrintController() {
		this(null, null, null);
	}

	@Autowired
	public PrintController(ApplicationsService applicationSevice, PdfDocumentBuilder builder, UserService userService) {
		this.applicationSevice = applicationSevice;
		this.builder = builder;
		this.userService = userService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public void printPage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletRequestBindingException {
		String applicationFormNumber = ServletRequestUtils.getStringParameter(request, "applicationFormId");
		ApplicationForm application = applicationSevice.getApplicationByApplicationNumber(applicationFormNumber);
		if (application == null || !userService.getCurrentUser().canSee(application)) {
			throw new ResourceNotFoundException();
		}
		sendPDF(response, applicationFormNumber, builder.buildPdfWithAttachments(application));
	}


	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public void printAll(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException, DocumentException, IOException {
		String appListToPrint = ServletRequestUtils.getStringParameter(request, "appList");
		String[] applicationIds = appListToPrint.split(";");
		List<ApplicationForm> applicationList = new ArrayList<ApplicationForm>();

		for (String applicationId : applicationIds) {
			ApplicationForm applicationForm = applicationSevice.getApplicationByApplicationNumber(applicationId);
			if (applicationForm != null && userService.getCurrentUser().canSee(applicationForm)) {
				applicationList.add(applicationForm);
			}

		}
		sendPDF(response,getTimestamp(), builder.buildPdfWithAttachments(applicationList.toArray(new ApplicationForm[] {})));
	}

	private void sendPDF(HttpServletResponse response, String pdfFileNamePostFix, byte[] pdf) throws IOException {
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		response.setHeader("Content-Disposition", "inline; filename=\"UCL_PRISM_" + pdfFileNamePostFix + ".pdf\"");
		response.setContentType("application/pdf");
		response.setContentLength(pdf.length);
		ServletOutputStream out = null;
		try {
			out = response.getOutputStream();
			out.write(pdf);

		} finally {
			try {
				out.flush();
			} catch (Exception e) {
			    // do nothing
			}
			
			try {
			    out.close();
			} catch (Exception e) {
			    // do nothing
			}
		}
	}
	
	String getTimestamp(){
		return new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
	}
}
