package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.itextpdf.text.DocumentException;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.pdf.PdfModelBuilder;
import com.zuehlke.pgadmissions.security.ContentAccessProvider;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/print")
public class PrintController {

    private final Logger log = LoggerFactory.getLogger(PrintController.class);
    
	private final ApplicationsService applicationService;
	
	private final PdfDocumentBuilder pdfDocumentBuilder;
	
	private final UserService userService;
	
	private final ContentAccessProvider contentAccessProvider;

	public PrintController() {
		this(null, null, null, null);
	}

	@Autowired
	public PrintController(final ApplicationsService applicationSevice, final PdfDocumentBuilder builder, final UserService userService,
			final ContentAccessProvider contentAccessProvider) {
		this.applicationService = applicationSevice;
		this.pdfDocumentBuilder = builder;
		this.userService = userService;
		this.contentAccessProvider = contentAccessProvider;
	}

	@RequestMapping(method = RequestMethod.GET)
	public void printPage(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletRequestBindingException {
		String applicationFormNumber = ServletRequestUtils.getStringParameter(request, "applicationFormId");
		ApplicationForm application = applicationService.getApplicationByApplicationNumber(applicationFormNumber);
		sendPDF(response, applicationFormNumber, pdfDocumentBuilder.build(buildPDFModel(application), application));
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public void printAll(final HttpServletRequest request, final HttpServletResponse response) throws ServletRequestBindingException, DocumentException, IOException {
		String appListToPrint = ServletRequestUtils.getStringParameter(request, "appList");
		String[] applicationIds = appListToPrint.split(";");
		HashMap<PdfModelBuilder, ApplicationForm> formsToPrint = new HashMap<PdfModelBuilder, ApplicationForm>();
		
		for (String applicationId : applicationIds) {
			ApplicationForm application = applicationService.getApplicationByApplicationNumber(applicationId);
			try {
				formsToPrint.put(buildPDFModel(application), application);
			} catch (ResourceNotFoundException e) {
				continue;
			}	
		}
		
		sendPDF(response, getTimestamp(), pdfDocumentBuilder.build(formsToPrint));
	}
	
	private PdfModelBuilder buildPDFModel(ApplicationForm application) throws ResourceNotFoundException {
		RegisteredUser user = userService.getCurrentUser();
		contentAccessProvider.validateCanDownloadApplication(application, user);	
		return new PdfModelBuilder(application, user);
	}

	private void sendPDF(final HttpServletResponse response, final String pdfFileNamePostFix, final byte[] pdf) throws IOException {
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
		} catch (Exception e) {
            log.warn(e.getMessage(), e);		    
		} finally {
			try {
				out.flush();
			} catch (Exception e) {
			    log.warn(e.getMessage(), e);
			}
			
			try {
			    out.close();
			} catch (Exception e) {
			    log.warn(e.getMessage(), e);
			}
		}
	}
	
	protected String getTimestamp(){
		return new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
	}
	
}