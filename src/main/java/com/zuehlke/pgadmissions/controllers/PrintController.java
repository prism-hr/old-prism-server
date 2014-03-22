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

import com.google.common.io.Closer;
import com.google.common.io.Flushables;
import com.itextpdf.text.DocumentException;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.pdf.PdfModelBuilder;
import com.zuehlke.pgadmissions.services.ApplicationFormService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/print")
public class PrintController {

    private final Logger log = LoggerFactory.getLogger(PrintController.class);
    
	private final ApplicationFormService applicationSevice;
	
	private final PdfDocumentBuilder pdfDocumentBuilder;
	
	private final UserService userService;

	public PrintController() {
		this(null, null, null);
	}

	@Autowired
	public PrintController(final ApplicationFormService applicationSevice, final PdfDocumentBuilder builder, final UserService userService) {
		this.applicationSevice = applicationSevice;
		this.pdfDocumentBuilder = builder;
		this.userService = userService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public void printPage(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletRequestBindingException {
		String applicationFormNumber = ServletRequestUtils.getStringParameter(request, "applicationFormId");
		
		ApplicationForm form = applicationSevice.getApplicationByApplicationNumber(applicationFormNumber);
		
		if (form == null) {
		    throw new ResourceNotFoundException();
		}
		
		RegisteredUser currentUser = userService.getCurrentUser();
		
		PdfModelBuilder pdfModelBuilder = new PdfModelBuilder();
		if (isApplicant(currentUser, form)) {
		    pdfModelBuilder.includeCriminialConvictions(true);
		    pdfModelBuilder.includeDisability(true);
		    pdfModelBuilder.includeEthnicity(true);
		} else if (!currentUser.isRefereeOfApplicationForm(form)) {
		    pdfModelBuilder.includeReferences(true);
		}
		
		sendPDF(response, applicationFormNumber, pdfDocumentBuilder.build(pdfModelBuilder, form));
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public void printAll(final HttpServletRequest request, final HttpServletResponse response) throws ServletRequestBindingException, DocumentException, IOException {
		String appListToPrint = ServletRequestUtils.getStringParameter(request, "appList");
		String[] applicationIds = appListToPrint.split(";");
		RegisteredUser currentUser = userService.getCurrentUser();
		HashMap<PdfModelBuilder, ApplicationForm> formsToPrint = new HashMap<PdfModelBuilder, ApplicationForm>();
		
		for (String applicationId : applicationIds) {
			ApplicationForm form = applicationSevice.getApplicationByApplicationNumber(applicationId);
			
			if (form == null) {
			    continue;
			}
			
			PdfModelBuilder pdfModelBuilder = new PdfModelBuilder();
			if (isApplicant(currentUser, form)) {
			    pdfModelBuilder.includeCriminialConvictions(true);
			    pdfModelBuilder.includeDisability(true);
			    pdfModelBuilder.includeEthnicity(true);
			} else if (!currentUser.isRefereeOfApplicationForm(form)) {
	            pdfModelBuilder.includeReferences(true);
	        }
			
			formsToPrint.put(pdfModelBuilder, form);
		}
		
		sendPDF(response, getTimestamp(), pdfDocumentBuilder.build(formsToPrint));
	}

	private void sendPDF(final HttpServletResponse response, final String pdfFileNamePostFix, final byte[] pdf) throws IOException {
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		response.setHeader("Content-Disposition", "inline; filename=\"UCL_PRISM_" + pdfFileNamePostFix + ".pdf\"");
		response.setContentType("application/pdf");
		response.setContentLength(pdf.length);
		ServletOutputStream out = null;
		
		Closer closer = Closer.create();
		try {
			out = closer.register(response.getOutputStream());
			out.write(pdf);
		} catch (IOException e) {
            log.warn("Problem with sending PDF, " + e.getClass().getSimpleName() + " thrown.");		    
		} finally {
		    Flushables.flushQuietly(out);
		    closer.close();
		}
	}
	
	protected String getTimestamp(){
		return new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
	}
	
	private boolean isApplicant(final RegisteredUser user, final ApplicationForm form) {
	    return user.getId().equals(form.getApplicant().getId());
	}
}
