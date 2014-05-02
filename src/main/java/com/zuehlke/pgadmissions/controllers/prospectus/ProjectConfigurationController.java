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
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.AdvertState;
import com.zuehlke.pgadmissions.dto.ProjectDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DurationOfStudyPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.UserPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.HibernateProxyTypeAdapter;
import com.zuehlke.pgadmissions.validators.ProjectDTOValidator;

import freemarker.template.TemplateException;

@Controller
@RequestMapping("/prospectus/projects")
public class ProjectConfigurationController {

    private UserService userService;

    private ProgramService programsService;

    private ApplicationContext applicationContext;

    private ProjectDTOValidator projectDTOValidator;

    private DatePropertyEditor datePropertyEditor;

    private ProgramPropertyEditor programPropertyEditor;

    private UserPropertyEditor personPropertyEditor;

    private ApplyTemplateRenderer templateRenderer;

    private DurationOfStudyPropertyEditor durationOfStudyPropertyEditor;

    private Gson gson;

    @PostConstruct
    protected void customizeJsonSerializer() throws IOException {
        gson = new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY)
                .registerTypeAdapter(Program.class, new JsonSerializer<Program>() {
                    @Override
                    public JsonElement serialize(Program src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.getCode());
                    }
                }).registerTypeAdapter(User.class, new JsonSerializer<User>() {
                    @Override
                    public JsonElement serialize(User user, Type typeOfSrc, JsonSerializationContext context) {
                        JsonObject element = new JsonObject();
                        element.add("email", new JsonPrimitive(user.getEmail()));
                        element.add("firstName", new JsonPrimitive(user.getFirstName()));
                        element.add("lastName", new JsonPrimitive(user.getLastName()));
                        return element;
                    }
                }).create();
    }

    @InitBinder("projectDTO")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(projectDTOValidator);
        binder.registerCustomEditor(Program.class, "program", programPropertyEditor);
        binder.registerCustomEditor(User.class, "administrator", personPropertyEditor);
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(User.class, "primarySupervisor", personPropertyEditor);
        binder.registerCustomEditor(User.class, "secondarySupervisor", personPropertyEditor);
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
    public User getUser() {
        return userService.getCurrentUser();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String addProject(@ModelAttribute("projectDTO") @Valid ProjectDTO projectDTO, BindingResult result, HttpServletRequest request) {
        Map<String, Object> map = getErrorValues(result, request);

        if (map.isEmpty()) {
            Project project = programsService.addProject(projectDTO);
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
        return gson.toJson(getUser());
    }

    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    @ResponseBody
    public String getProject(@PathVariable("projectId") int projectId) throws TemplateException, IOException {
        Map<String, Object> map = Maps.newHashMap();
        Project project = (Project) programsService.getById(projectId);
        if (project == null || project.getState() != AdvertState.PROGRAM_APPROVED) {
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
    public String saveProject(@RequestParam Integer id, @Valid ProjectDTO projectDTO, BindingResult result, HttpServletRequest request) {
        Map<String, Object> map = getErrorValues(result, request);
        if (!result.hasErrors()) {
            programsService.updateProject(id, projectDTO);
            map.put("success", "true");
            map.put("projectId", id);
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
        programsService.removeProject(projectId);
        return "ok";
    }

}
