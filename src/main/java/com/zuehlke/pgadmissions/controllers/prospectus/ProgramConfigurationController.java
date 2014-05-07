package com.zuehlke.pgadmissions.controllers.prospectus;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zuehlke.pgadmissions.domain.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramTypePropertyEditor;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.OpportunitiesService;
import com.zuehlke.pgadmissions.services.ProgramInstanceService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.FieldErrorUtils;
import com.zuehlke.pgadmissions.utils.GsonExclusionStrategies;
import com.zuehlke.pgadmissions.utils.HibernateProxyTypeAdapter;
import com.zuehlke.pgadmissions.validators.AbstractValidator;
import com.zuehlke.pgadmissions.validators.AdvertClosingDateValidator;
import com.zuehlke.pgadmissions.validators.OpportunityRequestValidator;

import freemarker.template.TemplateException;

@Controller
@RequestMapping("/prospectus/programme")
public class ProgramConfigurationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProgramService programsService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DomicilePropertyEditor domicilePropertyEditor;

    @Resource(name = "programValidator")
    private OpportunityRequestValidator opportunityRequestValidator;

    @Autowired
    private AdvertClosingDateValidator closingDateValidator;

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

    @Autowired
    private ProgramTypePropertyEditor programTypePropertyEditor;

    @Autowired
    private RoleService roleService;

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
        binder.registerCustomEditor(ProgramType.class, programTypePropertyEditor);
    }

    @InitBinder("programClosingDate")
    public void registerEditorsAndValidatorsForClosingDate(WebDataBinder binder) {
        binder.registerCustomEditor(Program.class, "program", programPropertyEditor);
        binder.registerCustomEditor(Date.class, "closingDate", datePropertyEditor);
    }

    @ModelAttribute("user")
    public User getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("programmes")
    public List<Program> getProgrammes() {
        User user = userService.getCurrentUser();
        if (roleService.hasRole(user, Authority.SYSTEM_ADMINISTRATOR)) {
            return programsService.getAllEnabledPrograms();
        }
        return roleService.getProgramsByUserAndRole(user, Authority.PROGRAM_ADMINISTRATOR);
    }

    @RequestMapping(value = "/getAdvertData", method = RequestMethod.GET)
    @ResponseBody
    public String getOpportunityData(@RequestParam(required = false) String programCode) {
        Program program = programsService.getProgramByCode(programCode);

        Map<String, Object> result = Maps.newHashMap();

        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("advertId", program.getId());

        Domicile institutionCountry = domicileService.getEnabledDomicileByCode(program.getInstitution().getDomicileCode());

        result.put("programId", program.getId());
        result.put("programTitle", program.getTitle());
        result.put("programDescription", program.getDescription());
        result.put("programStudyDuration", program.getStudyDuration());
        result.put("programFunding", program.getFunding());
        result.put("programState", program.getState());
        result.put("isCustomProgram", program.getProgramImport() == null);
        result.put("atasRequired", program.getRequireProjectDefinition());
        result.put("programType", program.getProgramType().getId());
        result.put("institutionCountryCode", encryptionHelper.encrypt(institutionCountry.getId()));
        result.put("institutionCode", program.getInstitution().getCode());
        // TODO check if locked
//        result.put("programLock", program.getLocked());
        result.put("advertisingDeadline", programInstanceService.getAdvertisingDeadlineYear(program));
        result.put("studyOptions", programInstanceService.getStudyOptions(program));
        result.put("buttonToApply", templateRenderer.renderButton(dataMap));
        result.put("linkToApply", templateRenderer.renderLink(dataMap));

        return gson.toJson(result);
    }

    @RequestMapping(value = "/saveProgramAdvert", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String saveOpportunity(@Valid OpportunityRequest opportunityRequest, BindingResult result) {
        Map<String, Object> map;
        if (result.hasErrors()) {
            map = FieldErrorUtils.populateMapWithErrors(result, applicationContext);
            FieldError otherInstitutionError = result.getFieldError("otherInstitution");
            if (otherInstitutionError != null && "institution.did.you.mean".equals(otherInstitutionError.getCode())) {
                map.put("otherInstitution", ImmutableMap.of("errorCode", "institution.did.you.mean", "institutions", otherInstitutionError.getDefaultMessage()));
            }
        } else {
            map = Maps.newHashMap();
            User currentUser = getUser();
            opportunityRequest.setAuthor(currentUser);
            if (programsService.canChangeInstitution(currentUser, opportunityRequest)) {
                Program program = programsService.saveProgramOpportunity(opportunityRequest);
                map.put("success", (Object) true);
                map.put("programCode", program.getCode());
            } else {
                opportunitiesService.createOpportunityRequest(opportunityRequest, false);
                map.put("changeRequestCreated", (Object) true);
            }
        }
        return gson.toJson(map);
    }

    @RequestMapping(value = "/addClosingDate", method = RequestMethod.POST)
    @ResponseBody
    public String addClosingDate(@RequestParam String programCode, AdvertClosingDate programClosingDate, BindingResult result, HttpServletRequest request) {
        Program program = programsService.getProgramByCode(programCode);
        programClosingDate.setAdvert(program);

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
    public String updateClosingDate(@RequestParam String programCode, AdvertClosingDate programClosingDate, BindingResult result, HttpServletRequest request) {
        Program program = programsService.getProgramByCode(programCode);
        programClosingDate.setAdvert(program);

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
