package com.zuehlke.pgadmissions.controllers.prospectus;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/prospectus")
public class ProspectusController {

    private static final String PROSPECTUS_PAGE = "/private/prospectus/prospectus";

    @Autowired
    private UserService userService;

    @Autowired
    private ProgramService programsService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private DomicileService domicileService;

    @Autowired
    private RoleService roleService;

    @RequestMapping(method = RequestMethod.GET)
    public String showProspectus() {
        return PROSPECTUS_PAGE;
    }

    @ModelAttribute("user")
    public User getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("programmes")
    public List<Program> getProgrammes() {
        User currentUser = userService.getCurrentUser();
        if (roleService.hasRole(currentUser, Authority.SUPERADMINISTRATOR)) {
            return programsService.getAllEnabledPrograms();
        }
        return roleService.getProgramsByUserAndRole(currentUser, Authority.ADMINISTRATOR);
    }

    @ModelAttribute("projectProgrammes")
    public List<Program> getProjectProgrammes() {
        return programsService.getProgramsForWhichCanManageProjects(getUser());
    }

    @ModelAttribute("studyOptions")
    public List<StudyOption> getDistinctStudyOptions() {
        return programInstanceService.getAvailableStudyOptions();
    }

    @ModelAttribute("countries")
    public List<Domicile> getAllEnabledDomiciles() {
        return domicileService.getAllEnabledDomiciles();
    }

    @ModelAttribute("advertisingDeadlines")
    public List<Integer> getAdvertisingDeadlines() {
        return programInstanceService.getPossibleAdvertisingDeadlineYears();
    }

    @ModelAttribute("programTypes")
    public List<ProgramType> getProgramTypes() {
        return programsService.getProgramTypes();
    }

}
