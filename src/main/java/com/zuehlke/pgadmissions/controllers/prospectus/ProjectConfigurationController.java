package com.zuehlke.pgadmissions.controllers.prospectus;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.google.common.collect.Maps;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ProjectDTO;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DurationOfStudyPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.HibernateProxyTypeAdapter;
import com.zuehlke.pgadmissions.validators.ProgramClosingDateValidator;
import com.zuehlke.pgadmissions.validators.ProjectAdvertDTOValidator;

import freemarker.template.Template;
import freemarker.template.TemplateException;

@Controller
@RequestMapping("/prospectus/projects")
public class ProjectConfigurationController {

    public static final String LINK_TO_APPLY = "/private/prospectus/link_to_apply.ftl";
    public static final String BUTTON_TO_APPLY = "/private/prospectus/button_to_apply.ftl";

    private final UserService userService;

    private final ProgramsService programsService;
    private final String host;

    private final ApplicationContext applicationContext;

    private final DurationOfStudyPropertyEditor durationOfStudyPropertyEditor;

    private final ProjectAdvertDTOValidator projectAdvertDTOValidator;

    private final ProgramClosingDateValidator closingDateValidator;
    private final DatePropertyEditor datePropertyEditor;
    private final ProgramPropertyEditor programPropertyEditor;

    private final FreeMarkerConfigurer freeMarkerConfigurer;
    private Template buttonToApplyTemplate;
    private Template linkToApplyTemplate;
    private Gson gson;

    public ProjectConfigurationController() {
        this(null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public ProjectConfigurationController(UserService userService, ProgramsService programsService, @Value("${application.host}") final String host,
            ApplicationContext applicationContext, ProjectAdvertDTOValidator projectAdvertDTOValidator,
            DurationOfStudyPropertyEditor durationOfStudyPropertyEditor, FreeMarkerConfigurer freeMarkerConfigurer,
            ProgramClosingDateValidator closingDateValidator, DatePropertyEditor datePropertyEditor, ProgramPropertyEditor programPropertyEditor) {
        this.userService = userService;
        this.programsService = programsService;
        this.host = host;
        this.applicationContext = applicationContext;
        this.projectAdvertDTOValidator = projectAdvertDTOValidator;
        this.durationOfStudyPropertyEditor = durationOfStudyPropertyEditor;
        this.freeMarkerConfigurer = freeMarkerConfigurer;
        this.closingDateValidator = closingDateValidator;
        this.datePropertyEditor = datePropertyEditor;
        this.programPropertyEditor = programPropertyEditor;
    }

    @PostConstruct
    public void loadFreeMarkerTemplates() throws IOException {
        linkToApplyTemplate = freeMarkerConfigurer.getConfiguration().getTemplate(LINK_TO_APPLY);
        buttonToApplyTemplate = freeMarkerConfigurer.getConfiguration().getTemplate(BUTTON_TO_APPLY);
        gson = new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY)
                .registerTypeAdapter(Program.class, new JsonSerializer<Program>() {
                    @Override
                    public JsonElement serialize(Program src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.getCode());
                    }
                }).setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return false;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return RegisteredUser.class == clazz;
                    }
                }).create();
    }

    @InitBinder("projectAdvertDTO")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(projectAdvertDTOValidator);
        binder.registerCustomEditor(Program.class, "program", programPropertyEditor);
        binder.registerCustomEditor(Integer.class, "studyDuration", durationOfStudyPropertyEditor);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @ModelAttribute("program")
    public Program getProgram(@RequestParam(required = false) String programCode) {
        if (programCode == null) {
            return null;
        }
        return programsService.getProgramByCode(programCode);
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
        // Advert advert = getProgrameAdvert(programCode);
        //
        // Map<String, Object> result = Maps.newHashMap();
        // result.put("advert", HibernateUtils.unproxy(advert));
        //
        // HashMap<String, String> dataMap = new HashMap<String, String>();
        // dataMap.put("programCode", programCode);
        // dataMap.put("host", host);
        //
        // result.put("buttonToApply", processTemplate(buttonToApplyTemplate, dataMap));
        // result.put("linkToApply", processTemplate(linkToApplyTemplate, dataMap));
        //
        // return new Gson().toJson(result);
        return null;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String addProjectAdvert(@ModelAttribute("projectAdvertDTO") @Valid ProjectDTO projectAdvertDTO, BindingResult result, HttpServletRequest request) {
        Map<String, Object> map = Maps.newHashMap();

        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                map.put(error.getField(), applicationContext.getMessage(error, request.getLocale()));
                return gson.toJson(map);
            }
        }

        programsService.addProject(projectAdvertDTO, getUser());
        map.put("success", "true");

        return gson.toJson(map);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String listProjects() {
        List<Project> projects = programsService.listProjects(getUser());
        return gson.toJson(projects);
    }

    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    @ResponseBody
    public String getProject(@PathVariable("projectId") int projectId) {
        Project project = programsService.getProject(projectId);
        return gson.toJson(project);
    }

    @RequestMapping(value = "/{projectId}", method = RequestMethod.DELETE)
    @ResponseBody
    public String removeProject(@PathVariable("projectId") int projectId) {
        programsService.removeProject(projectId);
        return "ok";
    }

    protected String processTemplate(Template template, Map<String, String> dataMap) throws TemplateException, IOException {
        StringWriter writer = new StringWriter();
        template.process(dataMap, writer);
        String result = writer.toString();
        writer.close();
        return result;
    }
}
