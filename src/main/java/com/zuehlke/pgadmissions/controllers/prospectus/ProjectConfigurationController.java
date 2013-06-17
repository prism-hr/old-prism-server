package com.zuehlke.pgadmissions.controllers.prospectus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
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
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ProjectDTO;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DurationOfStudyPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.PersonPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.HibernateProxyTypeAdapter;
import com.zuehlke.pgadmissions.validators.ProjectDTOValidator;

@Controller
@RequestMapping("/prospectus/projects")
public class ProjectConfigurationController {


    private final UserService userService;

    private final ProgramsService programsService;

    private final ApplicationContext applicationContext;

    private final DurationOfStudyPropertyEditor durationOfStudyPropertyEditor;

    private final ProjectDTOValidator projectDTOValidator;

    private final DatePropertyEditor datePropertyEditor;
    private final ProgramPropertyEditor programPropertyEditor;
    private final PersonPropertyEditor personPropertyEditor;
    private final ProjectConverter projectConverter;

    private Gson gson;

    public ProjectConfigurationController() {
        this(null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public ProjectConfigurationController(UserService userService, ProgramsService programsService, ApplicationContext applicationContext,
            ProjectDTOValidator projectDTOValidator, DurationOfStudyPropertyEditor durationOfStudyPropertyEditor,
            DatePropertyEditor datePropertyEditor, ProgramPropertyEditor programPropertyEditor,
            PersonPropertyEditor personPropertyEditor, ProjectConverter projectConverter) {
        this.userService = userService;
        this.programsService = programsService;
        this.applicationContext = applicationContext;
        this.projectDTOValidator = projectDTOValidator;
        this.durationOfStudyPropertyEditor = durationOfStudyPropertyEditor;
        this.datePropertyEditor = datePropertyEditor;
        this.programPropertyEditor = programPropertyEditor;
		this.personPropertyEditor = personPropertyEditor;
		this.projectConverter = projectConverter;
    }

    @PostConstruct
    public void customizeJsonSerializer() throws IOException {
        gson = new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY)
                .registerTypeAdapter(Program.class, new JsonSerializer<Program>() {
                    @Override
                    public JsonElement serialize(Program src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.getCode());
                    }
                })
                .registerTypeAdapter(RegisteredUser.class, new JsonSerializer<RegisteredUser>() {
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
        binder.registerCustomEditor(Integer.class, "studyDuration", durationOfStudyPropertyEditor);
        binder.registerCustomEditor(Date.class, datePropertyEditor);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(Person.class, "primarySupervisor", personPropertyEditor);
        binder.registerCustomEditor(Person.class, "secondarySupervisor", personPropertyEditor);
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

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String addProject(@ModelAttribute("projectDTO") @Valid ProjectDTO projectDTO, BindingResult result, HttpServletRequest request) {
        Map<String, Object> map = getErrorValues(result, request);

        if(map.isEmpty()){
        	RegisteredUser currentUser = getUser();
        	Project project = projectConverter.toDomainObject(projectDTO);
        	project.setAuthor(currentUser);
			programsService.saveProject(project);
	        map.put("success", "true");
        }

        return gson.toJson(map);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String listProjects() {
        List<Project> projects = programsService.listProjects(getUser());
        return gson.toJson(projects);
    }

    @RequestMapping(value="/defaultPrimarySupervisor", method = RequestMethod.GET)
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
    public String getProject(@PathVariable("projectId") int projectId) {
        Project project = programsService.getProject(projectId);
        return gson.toJson(project);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody
    public String saveProject(@Valid ProjectDTO projectDTO, BindingResult result, HttpServletRequest request) {
    	Map<String, Object> map = getErrorValues(result, request);
    	if(!result.hasErrors()){
          Project project = projectConverter.toDomainObject(projectDTO);
          programsService.saveProject(project);
          map.put("success", "true");
    	}
    	return gson.toJson(map);
    }

	private Map<String,Object> getErrorValues(BindingResult result,	HttpServletRequest request) {
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
