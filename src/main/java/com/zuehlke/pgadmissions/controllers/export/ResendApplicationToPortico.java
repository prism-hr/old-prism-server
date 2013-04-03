package com.zuehlke.pgadmissions.controllers.export;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.jms.PorticoQueueService;

/**
 * This controller allows us to resend applications to PORTICO if we
 * get a maintenance call from UCL.
 */
@Controller
@RequestMapping("/resendApplicationToPortico")
public class ResendApplicationToPortico {
    
    private static final String OK = "OK";

    private static final String NOK = "NOK";

    private static final String PORTICO_RESEND_ACTIVATION_CODE = "781efa70-96fb-11e2-9e96-0800200c9a66";

    private final PorticoQueueService porticoQueueService;

    private final ApplicationFormDAO formDAO;
    
    public ResendApplicationToPortico() {
        this(null, null);
    }
    
    @Autowired
    public ResendApplicationToPortico(final PorticoQueueService porticoQueueService, final ApplicationFormDAO formDAO) {
        this.porticoQueueService = porticoQueueService;
        this.formDAO = formDAO;
    }

    @ResponseBody
    @RequestMapping(method=RequestMethod.GET)
    public String doResend(
            @RequestParam(required = true) final String applicationNumber, 
            @RequestParam(required = true) final String activationCode) {
        if (StringUtils.equals(PORTICO_RESEND_ACTIVATION_CODE, activationCode)) {
            ApplicationForm form = getApplicationForm(applicationNumber);
            if (form != null) {
                porticoQueueService.sendToPortico(form);
                return OK;
            }
        }
        return NOK;
    }
    
    @Transactional(readOnly = true)
    public ApplicationForm getApplicationForm(final String applicationNumber) {
        return formDAO.getApplicationByApplicationNumber(applicationNumber);
    }
}
