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

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.zuehlke.pgadmissions.converters.ProjectConverter;
import com.zuehlke.pgadmissions.dao.ProjectDAO;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ProjectDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.PersonPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.security.ContentAccessProvider;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.utils.HibernateProxyTypeAdapter;
import com.zuehlke.pgadmissions.validators.ProjectDTOValidator;

import freemarker.template.TemplateException;

@Controller
@RequestMapping("/prospectus/projects")
public class ProjectConfigurationController {

	private final ApplicationsService applicationsService;
	
    private final ApplicationFormUserRoleService applicationFormUserRoleService;

    private final ProgramsService programsService;

    private final ApplicationContext applicationContext;

    private final ProjectDTOValidator projectDTOValidator;

    private final DatePropertyEditor datePropertyEditor;
    
    private final ProgramPropertyEditor programPropertyEditor;
    
    private final PersonPropertyEditor personPropertyEditor;
    
    private final ProjectConverter projectConverter;
    
    private final ApplyTemplateRenderer templateRenderer;
    
    private final String host;
    
    private final ProjectDAO projectDAO;

    private Gson gson;

	private ContentAccessProvider contentAccessProvider;
    
    public ProjectConfigurationController() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public ProjectConfigurationController(ApplicationsService applicationsService, ApplicationFormUserRoleService applicationFormUserRoleService, ProgramsService programsService, 
    		ApplicationContext applicationContext, ProjectDTOValidator projectDTOValidator, DatePropertyEditor datePropertyEditor, ProgramPropertyEditor programPropertyEditor,
            PersonPropertyEditor personPropertyEditor, ProjectConverter projectConverter, ApplyTemplateRenderer templateRenderer, @Value("${application.host}") final String host,
            ProjectDAO projectDAO, ContentAccessProvider contentAccessProvider) {
    	this.applicationsService = applicationsService;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
        this.programsService = programsService;
        this.applicationContext = applicationContext;
        this.projectDTOValidator = projectDTOValidator;
        this.datePropertyEditor = datePropertyEditor;
        this.programPropertyEditor = programPropertyEditor;
        this.personPropertyEditor = personPropertyEditor;
        this.projectConverter = projectConverter;
		this.templateRenderer = templateRenderer;
		this.host = host;
		this.projectDAO = projectDAO;
		this.contentAccessProvider = contentAccessProvider;
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
    }

    @ModelAttribute("program")
    public Program getProgram(@RequestParam(required = false) String programCode) {
        if (programCode == null) {
            return null;
        }
        Program program = programsService.getProgramByCode(programCode);
        contentAccessProvider.validateCanManageProgramProjectAdverts(program, getCurrentUser());
        return program;
    }
	
    @ModelAttribute("user")
    public RegisteredUser getCurrentUser() {
        return applicationFormUserRoleService.getCurrentUser();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String addProject(@ModelAttribute("projectDTO") @Valid ProjectDTO projectDTO, BindingResult result, 
    		HttpServletRequest request, @ModelAttribute Program program) {
    	Map<String, Object> map = getErrorValues(result, request);
        if (map.isEmpty()) {
            RegisteredUser currentUser = getCurrentUser();
            Project project = projectConverter.toDomainObject(projectDTO);
            project.setAuthor(currentUser);
            applicationFormUserRoleService.grantUserProjectRoles(currentUser, project, Authority.PROJECTAUTHOR);
            applicationFormUserRoleService.grantUserProjectRoles(project.getPrimarySupervisor(), project, Authority.PROJECTPRIMARYSUPERVISOR);
            RegisteredUser administrator = project.getAdministrator();
            if (administrator != null) {
            	applicationFormUserRoleService.grantUserProjectRoles(administrator, project, Authority.PROJECTADMINISTRATOR);
            }
            RegisteredUser secondarySupervisor = project.getSecondarySupervisor();
            if (secondarySupervisor != null) {
            	applicationFormUserRoleService.grantUserProjectRoles(secondarySupervisor, project, Authority.SECONDARYSUPERVISOR);
            }
            programsService.saveProject(project);
            map.put("success", "true");
        } 
        return gson.toJson(map);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> listProjects(@ModelAttribute Program program) {
        Map<String, Object> json = new HashMap<String, Object>();
        List<Project> projects = Collections.emptyList();
        if (program != null) {
            projects = projectDAO.getProgramProjectsOfWhichProjectEditor(program, getCurrentUser());
        }
        json.put("projects", gson.toJson(projects));
        json.put("closingDate", programsService.getDefaultClosingDate(program));
        return json;
    }

    @RequestMapping(value = "/defaultPrimarySupervisor", method = RequestMethod.GET)
    @ResponseBody
    public String defaultSupervisor() {
        RegisteredUser user = getCurrentUser();
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
        Project project = programsService.getProject(projectId);  
        if(project == null) {
        	throw new ResourceNotFoundException();
        }
        contentAccessProvider.validateCanManageProjectAdvert(project, getCurrentUser());
        map.put("project", project);
        map.putAll(createApplyTemplates(project));
        return gson.toJson(map);
    }

	private Map<String,Object> createApplyTemplates(Project project) throws TemplateException, IOException{
		Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(3);
        dataMap.put("programCode", project.getProgram().getCode());
        dataMap.put("projectId", project.getId());
        dataMap.put("advertId", project.getAdvert().getId());
        dataMap.put("host", host);
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
        	Project toBeUpdated = projectDAO.getProjectById(projectDTO.getId());
        	contentAccessProvider.validateCanManageProjectAdvert(toBeUpdated, getCurrentUser());
        	RegisteredUser oldAdministrator = toBeUpdated.getAdministrator();
        	RegisteredUser oldPrimarySupervisor = toBeUpdated.getPrimarySupervisor();
        	RegisteredUser oldSecondarySupervisor = toBeUpdated.getSecondarySupervisor();
            Project project = projectConverter.toDomainObject(projectDTO);
        	RegisteredUser newAdministrator = project.getAdministrator();
        	RegisteredUser newPrimarySupervisor = project.getPrimarySupervisor();
        	RegisteredUser newSecondarySupervisor = project.getSecondarySupervisor();
        	if (!applicationsService.getApplicationsForProject(project).isEmpty()) {
	        	applicationFormUserRoleService.switchUserProjectRoles(oldAdministrator, newAdministrator, project, Authority.PROJECTADMINISTRATOR);
	        	applicationFormUserRoleService.switchUserProjectRoles(oldPrimarySupervisor, newPrimarySupervisor, project, Authority.PROJECTADMINISTRATOR, Authority.SUGGESTEDSUPERVISOR);
	        	applicationFormUserRoleService.switchUserProjectRoles(oldSecondarySupervisor, newSecondarySupervisor, project, Authority.SUGGESTEDSUPERVISOR);
        	}
        	programsService.saveProject(project);
            map.put("success", "true");
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
    	contentAccessProvider.validateCanManageProjectAdvert(programsService.getProject(projectId), getCurrentUser());
        programsService.removeProject(projectId);
        return "ok";
    }

}