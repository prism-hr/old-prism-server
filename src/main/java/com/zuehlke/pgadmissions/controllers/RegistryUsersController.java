package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zuehlke.pgadmissions.dao.RegistryUserDAO;
import com.zuehlke.pgadmissions.services.RegistryUserService;

@RequestMapping("/registryUsers")
@Controller

public class RegistryUsersController {
	private static final String CHANGE_STATES_DURATION_VIEW_NAME = "/private/staff/superAdmin/assign_stages_duration";
	
	RegistryUsersController() {
		
	}
	
	@Autowired
	public void RegistryUsersController(RegistryUserService registryUserService) {
		
	}
	
}
