package com.zuehlke.pgadmissions.controllers;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.valueOf;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.dto.RegistryUserDTO;
import com.zuehlke.pgadmissions.dto.StageDurationDTO;
import com.zuehlke.pgadmissions.exceptions.EmailTemplateException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.PersonPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.StageDurationPropertyEditor;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/configuration")
public class ConfigurationController {

	private static final String CONFIGURATION_VIEW_NAME = "/private/staff/superAdmin/configuration";
	private static final String CONFIGURATION_SECTION_NAME = "/private/staff/superAdmin/configuration_section";
	private final StageDurationPropertyEditor stageDurationPropertyEditor;

	private final PersonPropertyEditor registryPropertyEditor;
	private final UserService userService;
	private final ConfigurationService configurationService;
	private final EmailTemplateService templateService;

	ConfigurationController() {
		this(null, null, null, null, null);
	}

	@Autowired
	public ConfigurationController(StageDurationPropertyEditor stageDurationPropertyEditor, PersonPropertyEditor registryPropertyEditor,
			UserService userService, ConfigurationService configurationService, EmailTemplateService templateService) {

		this.stageDurationPropertyEditor = stageDurationPropertyEditor;

		this.registryPropertyEditor = registryPropertyEditor;
		this.userService = userService;
		this.configurationService = configurationService;
		this.templateService = templateService;
	}

	@InitBinder(value = "stageDurationDTO")
	public void registerValidatorsAndPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(StageDuration.class, stageDurationPropertyEditor);
	}

	@InitBinder(value = "registryUserDTO")
	public void registerValidatorsAndPropertyEditorsForRegistryUsers(WebDataBinder binder) {
		binder.registerCustomEditor(Person.class, registryPropertyEditor);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getConfigurationPage() {
		if (!getUser().isInRole(Authority.SUPERADMINISTRATOR) && !getUser().isInRole(Authority.ADMINISTRATOR) ) {
			throw new ResourceNotFoundException();
		}

		return CONFIGURATION_VIEW_NAME;
	}
	
	@RequestMapping(method = RequestMethod.GET, value="config_section")
	public String getConfigurationSection() {		
		if (!getUser().isInRole(Authority.SUPERADMINISTRATOR)  ) {
			return "/private/common/simpleMessage";
		}

		return CONFIGURATION_SECTION_NAME;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String submit(@ModelAttribute StageDurationDTO stageDurationDto, @ModelAttribute RegistryUserDTO registryUserDTO, @ModelAttribute ReminderInterval reminderInterval) {
		if (!getUser().isInRole(Authority.SUPERADMINISTRATOR)  ) {
			throw new ResourceNotFoundException();
		}
		configurationService.saveConfigurations(stageDurationDto.getStagesDuration(), registryUserDTO.getRegistryUsers(), reminderInterval);
		return "redirect:/configuration/config_section";
		
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/editEmailTemplate/{id:\\d+}")
	@ResponseBody
	public Map<String, Object> getTemplateVersion(@PathVariable Long id) {
		
		EmailTemplate template = templateService.getEmailTemplate(id);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("content", template.getContent());
		result.put("version", template.getVersion());
		return result;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/editEmailTemplate/{templateName:[a-zA-Z_]+}")
	@ResponseBody
	public Map<Object, Object> getVersionsForTemplate(@PathVariable String templateName) {
		
		EmailTemplate template = templateService.getActiveEmailTemplate(valueOf(templateName));
		Map<Long, String> versions = templateService.getEmailTemplateVersions(template.getName());
		Map<Object, Object> result = new HashMap<Object, Object>();
		result.put("content", template.getContent());
		result.put("versions", versions);
		result.put("activeVersion", template.getId());
		result.putAll(versions);
		return result;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"saveEmailTemplate/{templateName:[a-zA-Z_]+}"})
	@ResponseBody
	public Map<String, Object> saveTemplate(@PathVariable EmailTemplateName templateName,
			@RequestParam String content) {
		EmailTemplate template = templateService.saveNewEmailTemplate(templateName, content);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("id", template.getId());
		result.put("version", new SimpleDateFormat("yyyy/M/d - HH:mm:ss").format(template.getVersion()));
		return result;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"activateEmailTemplate/{templateName:[a-zA-Z_]+}/{id:\\d+}"})
	@ResponseBody
	public Map<String, String> activateTemplate(@PathVariable String templateName, @PathVariable Long id) {
		try {
			templateService.activateEmailTemplate(valueOf(templateName), id);
			return Collections.emptyMap();
		} catch (EmailTemplateException ete) {
			return Collections.singletonMap("error", ete.getMessage());
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"deleteEmailTemplate/{id:\\d+}"})
	@ResponseBody
	public Map<String, Object> deleteTemplate(@PathVariable Long id) {
		Map<String, Object> result = new HashMap<String, Object>();
		EmailTemplate toDeleteTemplate = templateService.getEmailTemplate(id);
		EmailTemplateName templateName = toDeleteTemplate.getName();
		EmailTemplate activeTemplate = templateService.getActiveEmailTemplate(templateName);
		result.put("activeTemplateId", activeTemplate.getId());
		result.put("activeTemplateContent", activeTemplate.getContent());
		try {
			templateService.deleteTemplateVersion(toDeleteTemplate);
		}
		catch (EmailTemplateException ete) {
			result.put("error", ete.getMessage());
		}
		return result;
	}
	
	
	@ModelAttribute("templateTypes")
	public EmailTemplateName[] getTemplateTypes() {
		return EmailTemplateName.values();
	}
	
	@ModelAttribute("stages")
	public ApplicationFormStatus[] getConfigurableStages() {
		return ApplicationFormStatus.getConfigurableStages();
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

	@ModelAttribute("stageDurations")
	public Map<String, StageDuration> getStageDurations() {
		Map<ApplicationFormStatus, StageDuration> stageDurations = configurationService.getStageDurations();
		Map<String, StageDuration> durations = new HashMap<String, StageDuration>();
		for (ApplicationFormStatus status : stageDurations.keySet()) {
			durations.put(status.toString(), stageDurations.get(status));
		}
		return durations;
	}

	@ModelAttribute("reminderInterval")
	public ReminderInterval getReminderInterval() {
		return configurationService.getReminderInterval();
	}

	@ModelAttribute("allRegistryUsers")
	public List<Person> getAllRegistryContacts() {
		return configurationService.getAllRegistryUsers();
	}
	
	@ModelAttribute("units")
	public DurationUnitEnum[] getUnits() {
		return DurationUnitEnum.values();
	}

	

}
