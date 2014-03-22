package com.zuehlke.pgadmissions.controllers.prospectus;

import java.io.IOException;
import java.lang.reflect.Type;
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

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.zuehlke.pgadmissions.converters.ProjectConverter;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.ProjectDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DurationOfStudyPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.PersonPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.HibernateProxyTypeAdapter;
import com.zuehlke.pgadmissions.validators.ProjectDTOValidator;

import freemarker.template.TemplateException;

@Controller
@RequestMapping("/prospectus/projects")
public class ProjectConfigurationController {

    private final UserService userService;

    private final ProgramService programsService;

    private final ApplicationContext applicationContext;

    private final ProjectDTOValidator projectDTOValidator;

    private final DatePropertyEditor datePropertyEditor;

    private final ProgramPropertyEditor programPropertyEditor;

    private final PersonPropertyEditor personPropertyEditor;

    private final ProjectConverter projectConverter;

    private final ApplyTemplateRenderer templateRenderer;

    private final DurationOfStudyPropertyEditor durationOfStudyPropertyEditor;

    private Gson gson;

    public ProjectConfigurationController() {
        this(null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public ProjectConfigurationController(UserService userService, ProgramService programsService, ApplicationContext applicationContext,
            ProjectDTOValidator projectDTOValidator, DatePropertyEditor datePropertyEditor, ProgramPropertyEditor programPropertyEditor,
            PersonPropertyEditor personPropertyEditor, ProjectConverter projectConverter, ApplyTemplateRenderer templateRenderer,
            DurationOfStudyPropertyEditor durationOfStudyPropertyEditor) {
        this.userService = userService;
        this.programsService = programsService;
        this.applicationContext = applicationContext;
        this.projectDTOValidator = projectDTOValidator;
        this.datePropertyEditor = datePropertyEditor;
        this.programPropertyEditor = programPropertyEditor;
        this.personPropertyEditor = personPropertyEditor;
        this.projectConverter = projectConverter;
        this.templateRenderer = templateRenderer;
        this.durationOfStudyPropertyEditor = durationOfStudyPropertyEditor;
    }

    @PostConstruct
    public void customizeJsonSerializer() throws IOException {
        gson = new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY)
                .registerTypeAdapter(Program.class, new JsonSerializer<Program>() {
                    @Override
                    public JsonElement serialize(Program src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.getCode());
                    }
                }).registerTypeAdapter(RegisteredUser.class, new JsonSerializer<RegisteredUser>() {
                    @Override
                    public JsonElement serialize(RegisteredUser supervisor, Type typeOfSrc, JsonSerializationContext context) {
                        Person person = new Person();
                        person.setEmail(supervisor.getEmail());
                        person.setFirstname(supervisor.getFirstName());
                        person.setLastname(supervisor.getLastName());
                        return new Gson().toJsonTree(person);
                    }
                }).create();
    }

    @InitBinder("projectDTO")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(projectDTOValidator);
        binder.registerCustomEditor(Program.class, "program", programPropertyEditor);
        binder.registerCustomEditor(Person.class, "administrator", personPropertyEditor);
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(Person.class, "primarySupervisor", personPropertyEditor);
        binder.registerCustomEditor(Person.class, "secondarySupervisor", personPropertyEditor);
        binder.registerCustomEditor(Integer.class, "studyDuration", durationOfStudyPropertyEditor);
    }

    @ModelAttribute("program")
    public Program getProgram(@RequestParam(required = false) String programCode) {
        if (Strings.isNullOrEmpty(programCode)) {
            return null;
        }
        return programsService.getProgramByCode(programCode);
    }

    @ModelAttribute("user")
    public RegisteredUser getUser() {
        return userService.getCurrentUser();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String addProject(@ModelAttribute("projectDTO") @Valid ProjectDTO projectDTO, BindingResult result, HttpServletRequest request) {
        Map<String, Object> map = getErrorValues(result, request);

        if (map.isEmpty()) {
            RegisteredUser currentUser = getUser();
            Project project = projectConverter.toDomainObject(projectDTO);
            project.setContactUser(currentUser);
            programsService.save(project);
            map.put("success", "true");
            map.put("projectId", project.getId());
        }

        return gson.toJson(map);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> listProjects(@RequestParam String programCode) {
        Map<String, Object> json = new HashMap<String, Object>();
        Program program = getProgram(programCode);
        List<Project> projects = Collections.emptyList();
        if (program != null) {
            projects = programsService.listProjects(getUser(), program);
            json.put("projects", gson.toJson(projects));
            json.put("closingDate", programsService.getDefaultClosingDate(program));
            json.put("studyDuration", program.getStudyDuration());
        }
        return json;
    }

    @RequestMapping(value = "/defaultPrimarySupervisor", method = RequestMethod.GET)
    @ResponseBody
    public String defaultSupervisor() {
        RegisteredUser user = getUser();
        Person person = new Person();
        person.setFirstname(user.getFirstName());
        person.setLastname(user.getLastName());
        person.setEmail(user.getEmail());
        return gson.toJson(person);
    }

    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    @ResponseBody
    public String getProject(@PathVariable("projectId") int projectId) throws TemplateException, IOException {
        Map<String, Object> map = Maps.newHashMap();
        Project project = (Project) programsService.getById(projectId);
        if (project == null || !project.isEnabled()) {
            throw new ResourceNotFoundException();
        }
        map.put("project", project);
        map.putAll(createApplyTemplates(project));
        return gson.toJson(map);
    }

    private Map<String, Object> createApplyTemplates(Project project) throws TemplateException, IOException {
        Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(3);
        dataMap.put("advertId", project.getId());
        Map<String, Object> templateMap = Maps.newHashMapWithExpectedSize(2);
        templateMap.put("buttonToApply", templateRenderer.renderButton(dataMap));
        templateMap.put("linkToApply", templateRenderer.renderLink(dataMap));
        return templateMap;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody
    public String saveProject(@Valid ProjectDTO projectDTO, BindingResult result, HttpServletRequest request) {
        Map<String, Object> map = getErrorValues(result, request);
        if (!result.hasErrors()) {
            Project project = projectConverter.toDomainObject(projectDTO);
            if (project == null) {
                throw new ResourceNotFoundException();
            }
            programsService.save(project);
            map.put("success", "true");
            map.put("projectId", project.getId());
        }
        return gson.toJson(map);
    }

    private Map<String, Object> getErrorValues(BindingResult result, HttpServletRequest request) {
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(result.getErrorCount());
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                map.put(error.getField(), applicationContext.getMessage(error, request.getLocale()));
            }
        }
        return map;
    }

    @RequestMapping(value = "/{projectId}", method = RequestMethod.DELETE)
    @ResponseBody
    public String removeProject(@PathVariable("projectId") int projectId) {
        programsService.removeAdvert(projectId);
        return "ok";
    }

}
