package com.zuehlke.pgadmissions.controllers.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.exceptions.XMLDataImportException;
import com.zuehlke.pgadmissions.timers.PorticoThrottleTask;

@Controller
@RequestMapping("/wyslijTransfery")
public class ThrottleTaskTestController {

    @Autowired
    private PorticoThrottleTask porticoThrottleTask;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public String sendMails() throws XMLDataImportException {
        porticoThrottleTask.porticoThrottleTask();
        return "Done!";
    }
}
