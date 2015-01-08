package com.zuehlke.pgadmissions.redirect;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("api/robots")
public class RobotController {

    @RequestMapping(method = RequestMethod.GET)
    public String serve() {
        // TODO finish it
        return "<http></http>";
    }

}
