package com.zuehlke.pgadmissions.controllers.prospectus;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
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
import com.google.gson.GsonBuilder;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ProgramOpportunityDTO;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DurationOfStudyPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.FieldErrorUtils;
import com.zuehlke.pgadmissions.utils.GsonExclusionStrategies;
import com.zuehlke.pgadmissions.utils.HibernateProxyTypeAdapter;
import com.zuehlke.pgadmissions.validators.AbstractValidator;
import com.zuehlke.pgadmissions.validators.ProgramOpportunityDTOValidator;
import com.zuehlke.pgadmissions.validators.ProgramClosingDateValidator;

import freemarker.template.TemplateException;

@Controller
@RequestMapping("/prospectus/programme")
public class ProgramConfigurationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProgramsService programsService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DurationOfStudyPropertyEditor durationOfStudyPropertyEditor;

    @Autowired
    private ProgramOpportunityDTOValidator programOpportunityDTOValidator;

    @Autowired
    private ProgramClosingDateValidator closingDateValidator;

    @Autowired
    private DatePropertyEditor datePropertyEditor;

    @Autowired
    private ProgramPropertyEditor programPropertyEditor;

    @Autowired
    private ApplyTemplateRenderer templateRenderer;

    @Autowired
    private AdvertService advertsService;

    private Gson gson;

    @PostConstruct
    public void customizeGsonBuilder() throws IOException {
        gson = new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY)
                .setExclusionStrategies(GsonExclusionStrategies.excludeClass(Program.class)).create();
    }

    @InitBinder("programOpportunityDTO")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(programOpportunityDTOValidator);
        binder.registerCustomEditor(Integer.class, "studyDuration", durationOfStudyPropertyEditor);
    }

    @InitBinder("programClosingDate")
    public void registerEditorsAndValidatorsForClosingDate(WebDataBinder binder) {
        binder.setValidator(closingDateValidator);
        binder.registerCustomEditor(Program.class, "program", programPropertyEditor);
        binder.registerCustomEditor(Date.class, "closingDate", datePropertyEditor);
    }

    @ModelAttribute("program")
    public Program getProgram(@RequestParam(required = false) String programCode) {
        if (programCode == null) {
            return null;
        }
        return programsService.getProgramByCode(programCode);
    }

    private Advert getProgrameAdvert(Program program) {
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
    public String getAdvertData(@RequestParam String programCode) throws TemplateException, IOException {
        Program program = getProgram(programCode);
        Advert advert = getProgrameAdvert(program);

        Map<String, Object> result = Maps.newHashMap();
        result.put("advert", advert);

        HashMap<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("programCode", programCode);
        if (advert != null) {
            dataMap.put("advertId", advert.getId());
        }
        result.put("isCustomProgram", program.getProgramFeed() == null);
        result.put("possibleAdvertisingDeadlines", programInstanceService.getPossibleAdvertisingDeadlineYears());
        result.put("advertisingDeadline", programInstanceService.getAdvertisingDeadlineYear(program));
        result.put("studyOptions", programInstanceService.getStudyOptions(program));
        result.put("buttonToApply", templateRenderer.renderButton(dataMap));
        result.put("linkToApply", templateRenderer.renderLink(dataMap));

        return gson.toJson(result);
    }

    @RequestMapping(value = "/saveProgramAdvert", method = RequestMethod.POST)
    @ResponseBody
    public String saveProgramAdvert(@Valid ProgramOpportunityDTO programOpportunityDTO, BindingResult result, HttpServletRequest request) {
        Map<String, Object> map;
        if (result.hasErrors()) {
            map = FieldErrorUtils.populateMapWithErrors(result, applicationContext);
        } else {
            programsService.saveProgramOpportunity(programOpportunityDTO);
            map = Collections.singletonMap("success", (Object) true);
        }
        return gson.toJson(map);
    }

    @RequestMapping(value = "/addClosingDate", method = RequestMethod.POST)
    @ResponseBody
    public String addClosingDate(@Valid ProgramClosingDate programClosingDate, BindingResult result, @ModelAttribute Program program, HttpServletRequest request) {
        Map<String, Object> map;
        if (result.hasErrors()) {
            map = FieldErrorUtils.populateMapWithErrors(result, applicationContext);
        } else {
            programsService.addClosingDateToProgram(program, programClosingDate);
            map = Collections.singletonMap("programClosingDate", (Object) programClosingDate);
        }

        return gson.toJson(map);
    }

    @RequestMapping(value = "/updateClosingDate", method = RequestMethod.POST)
    @ResponseBody
    public String updateClosingDate(@Valid ProgramClosingDate programClosingDate, BindingResult result, HttpServletRequest request) {
        Map<String, Object> map;
        if (result.hasErrors()) {
            map = FieldErrorUtils.populateMapWithErrors(result, applicationContext);
        } else {
            programsService.updateClosingDate(programClosingDate);
            map = Collections.singletonMap("programClosingDate", (Object) programClosingDate);
        }

        return gson.toJson(map);
    }

    @RequestMapping(value = "/getClosingDates", method = RequestMethod.GET)
    @ResponseBody
    public String getClosingDates(@RequestParam String programCode, HttpServletRequest request) throws TemplateException, IOException {
        Map<String, Object> map = Maps.newHashMap();
        Program program = programsService.getProgramByCode(programCode);

        if (program == null) {
            map.put("program", applicationContext.getMessage(AbstractValidator.EMPTY_DROPDOWN_ERROR_MESSAGE, null, request.getLocale()));
        }

        if (map.isEmpty()) {
            map.put("programCode", programCode);

            map.put("closingDates", program.getClosingDates());
        }
        return gson.toJson(map);
    }

    @RequestMapping(value = "/removeClosingDate", method = RequestMethod.POST)
    @ResponseBody
    public String removeClosingDate(@ModelAttribute Program program, @RequestParam Integer closingDateId, HttpServletRequest request) throws TemplateException,
            IOException {
        Map<String, Object> map = Maps.newHashMap();

        if (program == null) {
            map.put("program", applicationContext.getMessage(AbstractValidator.EMPTY_DROPDOWN_ERROR_MESSAGE, null, request.getLocale()));
        }

        if (map.isEmpty()) {
            programsService.deleteClosingDateById(closingDateId);
            map.put("removedDate", closingDateId);
        }
        return gson.toJson(map);
    }

}
