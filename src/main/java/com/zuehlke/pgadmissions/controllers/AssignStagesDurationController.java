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
import com.zuehlke.pgadmissions.domain.RegistryUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.dto.RegistryUserDTO;
import com.zuehlke.pgadmissions.dto.StageDurationDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.RegistryUserPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.StageDurationPropertyEditor;
import com.zuehlke.pgadmissions.services.RegistryUserService;

@Controller
@RequestMapping("/assignStagesDuration")
public class AssignStagesDurationController {

	private final StageDurationDAO stateDurationDao;
	private static final String CHANGE_STATES_DURATION_VIEW_NAME = "/private/staff/superAdmin/assign_stages_duration";
	private final StageDurationPropertyEditor stageDurationPropertyEditor;
	private final ReminderIntervalDAO reminderIntervalDAO;
	private final RegistryUserService registryUserService;
	private final RegistryUserPropertyEditor registryPropertyEditor;
	
	AssignStagesDurationController(){
		this(null, null, null, null, null);
	}
	
	@Autowired
	public AssignStagesDurationController(StageDurationDAO stateDurationDao, StageDurationPropertyEditor stageDurationPropertyEditor, ReminderIntervalDAO reminderIntervalDAO, RegistryUserService registryUserService,
			RegistryUserPropertyEditor registryPropertyEditor) {
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
		binder.registerCustomEditor(RegistryUser.class, registryPropertyEditor);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String getAssignStagesDurationStage(ModelMap modelMap) {
		if (!getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			throw new ResourceNotFoundException();
		}
		modelMap.put("stages", getConfigurableStages());
		modelMap.put("units", DurationUnitEnum.values());
		modelMap.put("durationDAO", stateDurationDao);
		modelMap.put("intervalDAO", reminderIntervalDAO);
		return CHANGE_STATES_DURATION_VIEW_NAME;
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
		modelMap.put("stages", getConfigurableStages());
		modelMap.put("units", DurationUnitEnum.values());
		modelMap.put("durationDAO", stateDurationDao);
		modelMap.put("intervalDAO", reminderIntervalDAO);
		return CHANGE_STATES_DURATION_VIEW_NAME;
	}
	
	@RequestMapping(value="/submitReminderInterval", method = RequestMethod.POST)
	public String submitReminderInterval(ReminderInterval reminderInterval, ModelMap modelMap) {
		if (!getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			throw new ResourceNotFoundException();
		}
		
		reminderIntervalDAO.save(reminderInterval);
		
		modelMap.put("stages", getConfigurableStages());
		modelMap.put("units", DurationUnitEnum.values());
		modelMap.put("durationDAO", stateDurationDao);
		modelMap.put("intervalDAO", reminderIntervalDAO);
		return CHANGE_STATES_DURATION_VIEW_NAME;
	}
	
	@RequestMapping(value="/submitRegistryUsers", method = RequestMethod.POST)
	public String submitregistryUsers(@ModelAttribute("registryDTO") RegistryUserDTO registryUserDTO, ModelMap modelMap) {
		if (!getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			throw new ResourceNotFoundException();
		}
		List<RegistryUser> registryUsers = registryUserDTO.getRegistryUsers();
		for (RegistryUser registryUser : registryUsers) {
			registryUserService.save(registryUser);
		}
		modelMap.put("stages", getConfigurableStages());
		modelMap.put("units", DurationUnitEnum.values());
		modelMap.put("durationDAO", stateDurationDao);
		modelMap.put("intervalDAO", reminderIntervalDAO);
		modelMap.put("registryDTO", registryUserDTO);
		return CHANGE_STATES_DURATION_VIEW_NAME;
	}
	
	private RegisteredUser getCurrentUser() {
		return (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
	}
	
	@ModelAttribute("stages")
	public ApplicationFormStatus[] getConfigurableStages() {
		return ApplicationFormStatus.getConfigurableStages();
	}
	
}
