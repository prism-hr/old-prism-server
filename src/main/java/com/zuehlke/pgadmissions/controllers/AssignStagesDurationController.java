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

import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.dto.StageDurationDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.StageDurationPropertyEditor;

@Controller
@RequestMapping("/assignStagesDuration")
public class AssignStagesDurationController {

	private final StageDurationDAO stateDurationDao;
	private static final String CHANGE_STATES_DURATION_VIEW_NAME = "/private/staff/superAdmin/assign_stages_duration";
	private final StageDurationPropertyEditor stageDurationPropertyEditor;
	
	AssignStagesDurationController(){
		this(null, null);
	}
	
	@InitBinder
	public void registerValidatorsAndPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(StageDuration.class, stageDurationPropertyEditor);
	}
	
	@Autowired
	public AssignStagesDurationController(StageDurationDAO stateDurationDao, StageDurationPropertyEditor stageDurationPropertyEditor) {
		this.stateDurationDao = stateDurationDao;
		this.stageDurationPropertyEditor = stageDurationPropertyEditor;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String getAssignStagesDurationStage(ModelMap modelMap) {
		if (!getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			throw new ResourceNotFoundException();
		}
		modelMap.put("stages", getConfigurableStages());
		modelMap.put("units", DurationUnitEnum.values());
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
