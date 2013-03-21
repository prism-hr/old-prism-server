package com.zuehlke.pgadmissions.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.Throttle;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.dto.RegistryUserDTO;
import com.zuehlke.pgadmissions.dto.StageDurationDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.PersonPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.StageDurationPropertyEditor;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.services.ThrottleService;
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
	private final ThrottleService throttleService;

	ConfigurationController() {
		this(null, null, null, null, null);
	}

	@Autowired
	public ConfigurationController(StageDurationPropertyEditor stageDurationPropertyEditor, PersonPropertyEditor registryPropertyEditor,
			UserService userService, ConfigurationService configurationService, ThrottleService throttleService) {

		this.stageDurationPropertyEditor = stageDurationPropertyEditor;

		this.registryPropertyEditor = registryPropertyEditor;
		this.userService = userService;
		this.configurationService = configurationService;
		this.throttleService = throttleService;
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
	
	@RequestMapping(method = RequestMethod.GET, value = "/getThrottle")
	@ResponseBody
	public Map<String, Object> getThrottle() {
		Throttle throttle = throttleService.getThrottle();
		if (throttle==null) {
			return Collections.emptyMap();
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("throttleId", throttle.getId());
		result.put("enabled", throttle.getEnabled());
		result.put("batchSize", throttle.getBatchSize());
		return result;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/updateThrottle")
	@ResponseBody
	public Map<String, String> updateThrottle(@RequestParam Integer id, @RequestParam Boolean enabled, @RequestParam String batchSize) {
		Throttle throttle = new Throttle();
		throttle.setEnabled(enabled);
		try {
			throttle.setBatchSize(Integer.parseInt(batchSize));
		}
		catch (NumberFormatException nfe) {
			return Collections.singletonMap("error", "The throttling batch size must be a number");
		}
		throttle.setId(id);
		throttleService.updateThrottle(throttle);
		return Collections.emptyMap();
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
