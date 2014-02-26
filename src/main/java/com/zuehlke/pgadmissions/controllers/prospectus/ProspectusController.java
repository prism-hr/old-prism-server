package com.zuehlke.pgadmissions.controllers.prospectus;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StudyOption;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.ProgramTypeId;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/prospectus")
public class ProspectusController {

    private static final String PROSPECTUS_PAGE = "/private/prospectus/prospectus";

    @Autowired
    private UserService userService;

    @Autowired
    private ProgramsService programsService;

    @Autowired
    private ProgramInstanceService programInstanceService;
    
    @Autowired
    private DomicileService domicileService;

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
            return programsService.getAllEnabledPrograms();
        }
        return userService.getCurrentUser().getProgramsOfWhichAdministrator();
    }

    @ModelAttribute("projectProgrammes")
    public List<Program> getProjectProgrammes() {
        return programsService.getProgramsForWhichCanManageProjects(getUser());
    }

    @ModelAttribute("studyOptions")
    public List<StudyOption> getDistinctStudyOptions() {
        return programInstanceService.getDistinctStudyOptions();
    }
    
    @ModelAttribute("countries")
    public List<Domicile> getAllEnabledDomiciles() {
        return domicileService.getAllEnabledDomicilesExceptAlternateValues();
    }
    
    @ModelAttribute("advertisingDeadlines")
    public List<Integer> getAdvertisingDeadlines() {
        return programInstanceService.getPossibleAdvertisingDeadlineYears();
    }
    
    @ModelAttribute("programTypes")
    public List<ProgramType> getProgramTypes() {
        return Lists.newArrayList(new ProgramType(ProgramTypeId.INTERNSHIP, 5), new ProgramType(ProgramTypeId.RESEARCH_DEGREE, 8), new ProgramType(
                ProgramTypeId.VISITING_RESEARCH, 666));
    }

}
