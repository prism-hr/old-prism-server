package com.zuehlke.pgadmissions.controllers.export;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.services.ReportPorticoDocumentUploadFailureService;

/**
 * This controller exposes a simple GET request which is used by 
 * Ivo Antao from UCL to report any errors that occurred during 
 * unzipping and processing the uploaded applicant's document to PORTICO.
 * <p>
 * We only allow access to this page for requests that provide the 
 * correct activationCode.
 */
@Controller
@RequestMapping("/reportDocumentUploadFailure")
public class ReportPorticoDocumentUploadFailureController {

    private Logger log = Logger.getLogger(ReportPorticoDocumentUploadFailureController.class);
    
    private final ReportPorticoDocumentUploadFailureService service;
    
    private static final String PORTICO_UPLOAD_ACTIVATION_CODE = "6a219fb0-6acb-11e2-bcfd-0800200c9a66";
    
    public ReportPorticoDocumentUploadFailureController() {
        this(null);
    }
    
    @Autowired
    public ReportPorticoDocumentUploadFailureController(final ReportPorticoDocumentUploadFailureService service) {
        this.service = service;
    }
    
    @ResponseBody
    @RequestMapping(method=RequestMethod.GET)
    public String reportError(
            @RequestParam(required = true) String bookingReference,
            @RequestParam(required = true) String errorCode, 
            @RequestParam(required = true) String message,
            @RequestParam(required = true) String activationCode) {
        if (StringUtils.equals(PORTICO_UPLOAD_ACTIVATION_CODE, activationCode)) {
            StringBuilder mailMessageBuilder = new StringBuilder();
            mailMessageBuilder.append("Portico reported that there was an error uploading the documents [bookingReference=").append(StringUtils.trimToEmpty(bookingReference)).append("].");
            mailMessageBuilder.append("\n");
            mailMessageBuilder.append("The error message received was: \n");
            mailMessageBuilder.append(StringUtils.trimToEmpty(message));
            // TODO: Enabled when ready for production
            //service.sendErrorMessage(mailMessageBuilder.toString());
            log.warn(mailMessageBuilder.toString());
            return "OK";
        }
        return "NOK";
    }
}
