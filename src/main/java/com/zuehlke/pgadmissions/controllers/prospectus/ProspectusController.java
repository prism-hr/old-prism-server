package com.zuehlke.pgadmissions.controllers.prospectus;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/prospectus")
public class ProspectusController {

    private static final String PROSPECTUS_PAGE = "/private/prospectus/prospectus";
    private static final String LINK_TO_APPLY = "/private/prospectus/link_to_apply";
    private static final String BUTTON_TO_APPLY = "/private/prospectus/button_to_apply";

    private final UserService userService;

    private final ProgramsService programsService;
    private final String host;

    public ProspectusController() {
        this(null, null, null);
    }

    @Autowired
    public ProspectusController(UserService userService, ProgramsService programsService, @Value("${application.host}") final String host) {
        this.userService = userService;
        this.programsService = programsService;
        this.host = host;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showProspectus() {
        return PROSPECTUS_PAGE;
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("programmes")
    public List<Program> getProgrammes() {
        if (userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
            return programsService.getAllPrograms();
        }
        return userService.getCurrentUser().getProgramsOfWhichAdministrator();
    }

    @RequestMapping(value = "/getLinkToApply", method = RequestMethod.GET)
    public String getLinkToApply(@RequestParam String programmeCode, Model model) {
        model.addAttribute("programmeCode", programmeCode);
        model.addAttribute("host", host);
        return LINK_TO_APPLY;
    }

    @RequestMapping(value = "/getButtonToApply", method = RequestMethod.GET)
    public String getButtonToApply(@RequestParam String programmeCode, Model model) {
        model.addAttribute("programmeCode", programmeCode);
        model.addAttribute("host", host);
        return BUTTON_TO_APPLY;
    }

}
