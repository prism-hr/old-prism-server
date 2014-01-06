package com.zuehlke.pgadmissions.controllers.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.security.ContentAccessProvider;
import com.zuehlke.pgadmissions.services.PorticoQueueService;
import com.zuehlke.pgadmissions.services.UserService;

/**
 * This controller allows us to resubmit applications to PORTICO if we
 * get a maintenance call from UCL.
 */
@Controller
@RequestMapping("/resendApplicationToPortico")
public class ResendApplicationToPortico {
    
    private static final String OK = "OK";

    private final PorticoQueueService porticoQueueService;

    private final ApplicationFormDAO formDAO;
    
    private final UserService userService;
    
    private final ContentAccessProvider contentAccessProvider;
    
    public ResendApplicationToPortico() {
        this(null, null, null, null);
    }
    
    @Autowired
    public ResendApplicationToPortico(final PorticoQueueService porticoQueueService, final ApplicationFormDAO formDAO,
    		UserService userService, ContentAccessProvider contentAccessProvider) {
        this.porticoQueueService = porticoQueueService;
        this.formDAO = formDAO;
        this.userService = userService;
        this.contentAccessProvider = contentAccessProvider;
    }

    @ResponseBody
    @RequestMapping(method=RequestMethod.GET)
    public String doResend(
            @RequestParam(required = true) final String applicationNumber, 
            @RequestParam(required = true) final String activationCode) {
    	ApplicationForm application = getApplicationForm(applicationNumber);
    	contentAccessProvider.validateCanConfigureInterfaces(userService.getCurrentUser());
    	porticoQueueService.sendToPortico(application);
    	return OK;
    }
    
    @Transactional(readOnly = true)
    public ApplicationForm getApplicationForm(final String applicationNumber) {
        return formDAO.getApplicationByApplicationNumber(applicationNumber);
    }
    
}