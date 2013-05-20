package com.zuehlke.pgadmissions.controllers.prospectus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/prospectus")
public class ProspectusController {

    private static final String PROSPECTUS_PAGE = "/private/prospectus/prospectus";
    
    private final UserService userService;
    
    public ProspectusController() {
        this(null);
    }

    @Autowired
    public ProspectusController(UserService userService) {
        this.userService = userService;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String showProspectus() {
        return PROSPECTUS_PAGE;
    }
    
    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }
}
