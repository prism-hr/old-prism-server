package com.zuehlke.pgadmissions.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.dao.ReminderIntervalDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.dto.RegistryUserDTO;
import com.zuehlke.pgadmissions.dto.StageDurationDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.PersonPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.StageDurationPropertyEditor;
import com.zuehlke.pgadmissions.services.PersonService;

@Controller
@RequestMapping("/configuration")
public class ConfigurationController {

	private final StageDurationDAO stateDurationDao;
	private static final String CONFIGURATION_VIEW_NAME = "/private/staff/superAdmin/configuration";
	private final StageDurationPropertyEditor stageDurationPropertyEditor;
	private final ReminderIntervalDAO reminderIntervalDAO;
	private final PersonService registryUserService;
	private final PersonPropertyEditor registryPropertyEditor;
	
	ConfigurationController(){
		this(null, null, null, null, null);
	}
	
	@Autowired
	public ConfigurationController(StageDurationDAO stateDurationDao, StageDurationPropertyEditor stageDurationPropertyEditor, ReminderIntervalDAO reminderIntervalDAO, PersonService registryUserService,
			PersonPropertyEditor registryPropertyEditor) {
		this.stateDurationDao = stateDurationDao;
		this.stageDurationPropertyEditor = stageDurationPropertyEditor;
		this.reminderIntervalDAO = reminderIntervalDAO;
		this.registryUserService = registryUserService;
		this.registryPropertyEditor = registryPropertyEditor;
	}
	
	@InitBinder
	public void registerValidatorsAndPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(StageDuration.class, stageDurationPropertyEditor);
	}
	
	
	@InitBinder(value="registryDTO")
	public void registerValidatorsAndPropertyEditorsForRegistryUsers(WebDataBinder binder) {
		binder.registerCustomEditor(Person.class, registryPropertyEditor);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String getAssignStagesDurationStage(ModelMap modelMap) {
		if (!getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			throw new ResourceNotFoundException();
		}
		populateModelMap(modelMap);
		return CONFIGURATION_VIEW_NAME;
	}
	
	@RequestMapping(value="/submit", method = RequestMethod.POST)
	public String submitStagesDurations(StageDurationDTO stageDurationDTO, ModelMap modelMap) {
		if (!getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			throw new ResourceNotFoundException();
		}
		List<StageDuration> stagesDuration = stageDurationDTO.getStagesDuration();
		for (StageDuration stageDuration : stagesDuration) {
			stateDurationDao.save(stageDuration);
		}
		populateModelMap(modelMap);
		return CONFIGURATION_VIEW_NAME;
	}
	
	@RequestMapping(value="/submitReminderInterval", method = RequestMethod.POST)
	public String submitReminderInterval(ReminderInterval reminderInterval, ModelMap modelMap) {
		if (!getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			throw new ResourceNotFoundException();
		}
		
		reminderIntervalDAO.save(reminderInterval);
		
		populateModelMap(modelMap);
		return CONFIGURATION_VIEW_NAME;
	}
	
	@RequestMapping(value="/submitRegistryUsers", method = RequestMethod.POST)
	public String submitregistryUsers(@ModelAttribute("registryDTO") RegistryUserDTO registryUserDTO, ModelMap modelMap) {
		if (!getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			throw new ResourceNotFoundException();
		}
		List<Person> registryUsers = registryUserDTO.getRegistryUsers();
		for (Person registryUser : registryUsers) {
			registryUserService.save(registryUser);
		}
		populateModelMap(modelMap);
		return CONFIGURATION_VIEW_NAME;
	}
	
	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}
	
	@ModelAttribute("stages")
	public ApplicationFormStatus[] getConfigurableStages() {
		return ApplicationFormStatus.getConfigurableStages();
	}
	
	private void populateModelMap(ModelMap modelMap) {
		modelMap.put("stages", getConfigurableStages());
		modelMap.put("units", DurationUnitEnum.values());
		modelMap.put("durationDAO", stateDurationDao);
		modelMap.put("intervalDAO", reminderIntervalDAO);
		modelMap.put("allRegistryUsers", registryUserService.getAllRegistryUsers());
	}
	
}
