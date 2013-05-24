package com.zuehlke.pgadmissions.controllers.prospectus;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zuehlke.pgadmissions.dao.ProgramAdvertDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramAdvert;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.propertyeditors.DurationOfStudyPropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ProgramAdvertValidator;

@Controller
@RequestMapping("/prospectus")
public class ProspectusController {

    private static final String PROSPECTUS_PAGE = "/private/prospectus/prospectus";
    private static final String LINK_TO_APPLY = "/private/prospectus/link_to_apply";
    private static final String BUTTON_TO_APPLY = "/private/prospectus/button_to_apply";

    private final UserService userService;

    private final ProgramsService programsService;
    private final String host;
    private final ProgramAdvertDAO programAdvertDAO;
    private final DurationOfStudyPropertyEditor durationOfStudyPropertyEditor;

    private final ProgramAdvertValidator programAdvertValidator;

    public ProspectusController() {
        this(null, null, null, null, null, null);
    }

    @Autowired
    public ProspectusController(UserService userService, ProgramsService programsService, @Value("${application.host}") final String host,
                    ProgramAdvertDAO programAdvertDAO, ProgramAdvertValidator programAdvertValidator,
                    DurationOfStudyPropertyEditor durationOfStudyPropertyEditor) {
        this.userService = userService;
        this.programsService = programsService;
        this.host = host;
        this.programAdvertDAO = programAdvertDAO;
        this.programAdvertValidator = programAdvertValidator;
        this.durationOfStudyPropertyEditor = durationOfStudyPropertyEditor;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showProspectus() {
        return PROSPECTUS_PAGE;
    }

    @InitBinder("programAdvert")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(programAdvertValidator);
        binder.registerCustomEditor(Integer.class, "durationOfStudyInMonth", durationOfStudyPropertyEditor);
    }

    @ModelAttribute("program")
    public Program getProgram(@RequestParam(required = false) String programCode) {
        if (programCode == null) {
            return null;
        }
        return programsService.getProgramByCode(programCode);
    }

    @ModelAttribute("programAdvert")
    public ProgramAdvert getOrCreateProgrameAdvert(@RequestParam(required = false) String programCode) {
        Program program = getProgram(programCode);
        if (program == null) {
            return null;
        }
        return program.getAdvert();
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

    @RequestMapping(value = "/getAdvertData", method = RequestMethod.GET)
    @ResponseBody
    public String getLinkToApply(@RequestParam String programCode, Model model) {
        // model.addAttribute("programmeCode", programCode);
        // model.addAttribute("host", host);

        ProgramAdvert advert = new ProgramAdvert();
        advert.setDescription("aaa");
        advert.setDurationOfStudyInMonth(4);
        
                        //programsService.getProgramByCode(programCode).getAdvert();

        Map<String, Object> result = Maps.newHashMap();
        String templateString = "dummy template";
        result.put("linkToApply", host + "/apply/new?program=" + programCode);
        result.put("buttonToApply", templateString);
        result.put("advert", advert);

        return new Gson().toJson(result);
    }

    @RequestMapping(value = "/saveProgramAdvert", method = RequestMethod.POST)
    public String saveProgramAdvert(@ModelAttribute Program program, BindingResult programResult,
                    @ModelAttribute(value = "programAdvert") @Valid ProgramAdvert programAdvert, BindingResult result) {
        if (program == null) {
            programResult.reject("dropdown.radio.select.none");
        }

        if (!programResult.hasErrors() && !result.hasErrors()) {
            program.setAdvert(programAdvert);
            programsService.save(program);
        }

        return PROSPECTUS_PAGE;
    }

}
