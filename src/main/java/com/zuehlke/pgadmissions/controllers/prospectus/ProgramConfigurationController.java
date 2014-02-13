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
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramClosingDate;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.OpportunitiesService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.FieldErrorUtils;
import com.zuehlke.pgadmissions.utils.GsonExclusionStrategies;
import com.zuehlke.pgadmissions.utils.HibernateProxyTypeAdapter;
import com.zuehlke.pgadmissions.validators.AbstractValidator;
import com.zuehlke.pgadmissions.validators.OpportunityRequestValidator;
import com.zuehlke.pgadmissions.validators.ProgramClosingDateValidator;

import freemarker.template.TemplateException;

@Controller
@RequestMapping("/prospectus/programme")
@SessionAttributes("opportunityRequest")
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
    private DomicilePropertyEditor domicilePropertyEditor;

    @Autowired
    private OpportunityRequestValidator opportunityRequestValidator;

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

    @Autowired
    private EncryptionHelper encryptionHelper;

    @Autowired
    private DomicileService domicileService;

    @Autowired
    private OpportunitiesService opportunitiesService;

    private Gson gson;

    @PostConstruct
    public void customizeGsonBuilder() throws IOException {
        gson = new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY)
                .setExclusionStrategies(GsonExclusionStrategies.excludeClass(Program.class)).create();
    }

    @InitBinder("opportunityRequest")
    public void registerPropertyEditorsForOpportunityRequest(WebDataBinder binder) {
        binder.setValidator(opportunityRequestValidator);
        binder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binder.registerCustomEditor(Program.class, programPropertyEditor);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @InitBinder("programClosingDate")
    public void registerEditorsAndValidatorsForClosingDate(WebDataBinder binder) {
        binder.registerCustomEditor(Program.class, "program", programPropertyEditor);
        binder.registerCustomEditor(Date.class, "closingDate", datePropertyEditor);
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
    public String getOpportunityData(@RequestParam String programCode) {
        Program program = programsService.getProgramByCode(programCode);
        Advert advert = getProgrameAdvert(program);

        Map<String, Object> result = Maps.newHashMap();
        result.put("advert", advert);

        HashMap<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("programCode", programCode);
        if (advert != null) {
            dataMap.put("advertId", advert.getId());
        }

        Domicile institutionCountry = domicileService.getEnabledDomicileByCode(program.getInstitution().getDomicileCode());

        result.put("isCustomProgram", program.getProgramFeed() == null);
        result.put("atasRequired", program.getAtasRequired());
        result.put("institutionCountryCode", encryptionHelper.encrypt(institutionCountry.getId()));
        result.put("institutionCode", program.getInstitution().getCode());
        result.put("advertisingDeadline", programInstanceService.getAdvertisingDeadlineYear(program));
        result.put("studyOptions", programInstanceService.getStudyOptions(program));
        result.put("buttonToApply", templateRenderer.renderButton(dataMap));
        result.put("linkToApply", templateRenderer.renderLink(dataMap));

        return gson.toJson(result);
    }

    @RequestMapping(value = "/saveProgramAdvert", method = RequestMethod.POST)
    @ResponseBody
    public String saveOpportunity(@Valid OpportunityRequest opportunityRequest, BindingResult result) {
        Preconditions.checkNotNull(opportunityRequest.getSourceProgram());
        Map<String, Object> map;
        if (result.hasErrors()) {
            map = FieldErrorUtils.populateMapWithErrors(result, applicationContext);
        } else {
            if (programsService.canChangeInstitution(getUser(), opportunityRequest)) {
                Program program = programsService.saveProgramOpportunity(opportunityRequest);
                map = Maps.newHashMap();
                map.put("success", (Object) true);
                map.put("programCode", program.getCode());
            } else {
                map = Collections.singletonMap("changeRequestRequired", (Object) true);
            }
        }
        return gson.toJson(map);
    }

    @RequestMapping(value = "/confirmOpportunityChangeRequest", method = RequestMethod.POST)
    @ResponseBody
    public String confirmOpportunityChangeRequest(ModelMap modelMap, SessionStatus sessionStatus) {
        OpportunityRequest opportunityRequest = (OpportunityRequest) modelMap.get("opportunityRequest");
        opportunityRequest.setAuthor(getUser());
        opportunitiesService.createOpportunityChangeRequest(opportunityRequest);

        sessionStatus.setComplete();
        return gson.toJson(Collections.singletonMap("success", true));
    }

    @RequestMapping(value = "/addClosingDate", method = RequestMethod.POST)
    @ResponseBody
    public String addClosingDate(@RequestParam String programCode, ProgramClosingDate programClosingDate, BindingResult result, HttpServletRequest request) {
        Program program = programsService.getProgramByCode(programCode);
        programClosingDate.setProgram(program);

        ValidationUtils.invokeValidator(closingDateValidator, programClosingDate, result);

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
    public String updateClosingDate(@RequestParam String programCode, ProgramClosingDate programClosingDate, BindingResult result, HttpServletRequest request) {
        Program program = programsService.getProgramByCode(programCode);
        programClosingDate.setProgram(program);

        ValidationUtils.invokeValidator(closingDateValidator, programClosingDate, result);

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
    public String removeClosingDate(@RequestParam String programCode, @RequestParam Integer closingDateId, HttpServletRequest request)
            throws TemplateException, IOException {
        Program program = programsService.getProgramByCode(programCode);
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
