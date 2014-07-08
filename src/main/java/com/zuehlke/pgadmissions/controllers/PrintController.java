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
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pdf.PdfDocumentBuilder;
import com.zuehlke.pgadmissions.pdf.PdfModelBuilder;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/print")
public class PrintController {

    private final Logger log = LoggerFactory.getLogger(PrintController.class);
    
    @Autowired
	private ApplicationService applicationSevice;
	
    @Autowired
	private PdfDocumentBuilder pdfDocumentBuilder;
	
    @Autowired
	private UserService userService;
	
	@RequestMapping(method = RequestMethod.GET)
	public void printPage(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletRequestBindingException {
		String applicationFormNumber = ServletRequestUtils.getStringParameter(request, "applicationFormId");
		
		Application form = applicationSevice.getByApplicationNumber(applicationFormNumber);
		
		if (form == null) {
		    throw new ResourceNotFoundException();
		}
		
		User user = userService.getCurrentUser();
		
		PdfModelBuilder pdfModelBuilder = new PdfModelBuilder();
		if (isApplicant(user, form)) {
		    pdfModelBuilder.includeCriminialConvictions(true);
		    pdfModelBuilder.includeDisability(true);
		    pdfModelBuilder.includeEthnicity(true);
		    //  FIXME check VIEW_AS_*_ACTION
		} else if (true) {
		    pdfModelBuilder.includeReferences(true);
		}
		
		sendPDF(response, applicationFormNumber, pdfDocumentBuilder.build(pdfModelBuilder, form));
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public void printAll(final HttpServletRequest request, final HttpServletResponse response) throws ServletRequestBindingException, DocumentException, IOException {
		String appListToPrint = ServletRequestUtils.getStringParameter(request, "appList");
		String[] applicationIds = appListToPrint.split(";");
		User user = userService.getCurrentUser();
		HashMap<PdfModelBuilder, Application> formsToPrint = new HashMap<PdfModelBuilder, Application>();
		
		for (String applicationId : applicationIds) {
			Application form = applicationSevice.getByApplicationNumber(applicationId);
			
			if (form == null) {
			    continue;
			}
			
			PdfModelBuilder pdfModelBuilder = new PdfModelBuilder();
			if (isApplicant(user, form)) {
			    pdfModelBuilder.includeCriminialConvictions(true);
			    pdfModelBuilder.includeDisability(true);
			    pdfModelBuilder.includeEthnicity(true);
			} else if (true) {
		         //  FIXME check VIEW_AS_*_ACTION
			    // TODO specify visible IDs of references
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
	
	private boolean isApplicant(final User user, final Application form) {
	    return user.getId().equals(form.getUser().getId());
	}
}
