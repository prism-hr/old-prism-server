package com.zuehlke.pgadmissions.controllers.prospectus;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/prospectus")
public class ProspectusController {

    private static final String PROSPECTUS_PAGE = "/private/prospectus/prospectus";

    private final UserService userService;

    private final ProgramsService programsService;

    public ProspectusController() {
        this(null, null);
    }

    @Autowired
    public ProspectusController(UserService userService, ProgramsService programsService) {
        this.userService = userService;
        this.programsService = programsService;
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
    
    @ModelAttribute("closingDates")
    public Map<String, String> getDefaultClosingDateForProgramme() {
        return programsService.getDefaultClosingDates();
    }

    @ModelAttribute("projectProgrammes")
    public List<Program> getProjectProgrammes() {
        return programsService.getProgramsForWhichCanManageProjects(getUser());
    }

}
