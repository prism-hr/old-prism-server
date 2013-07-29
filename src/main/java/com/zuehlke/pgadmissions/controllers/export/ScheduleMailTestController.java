package com.zuehlke.pgadmissions.controllers.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.mail.ScheduledMailSendingService;

@Controller
@RequestMapping("/wyslijMejle")
public class ScheduleMailTestController {
    
    @Autowired
    private ScheduledMailSendingService scheduledMailSendingService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public String sendMails() {
        scheduledMailSendingService.sendDigestsToUsers();
        return "Done!";
    }

}
