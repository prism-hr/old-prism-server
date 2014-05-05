package com.zuehlke.pgadmissions.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.services.OpportunitiesService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/requests")
public class RequestsController {

	protected static final String REQUESTS_PAGE_VIEW_NAME = "/private/staff/superAdmin/requests";
	
	@Autowired
	private OpportunitiesService opportunitiesService;
	
	@Autowired
	private UserService userService;

	@RequestMapping(method = RequestMethod.GET)
	public String getRequestsPage() {
		return REQUESTS_PAGE_VIEW_NAME;
	}
	
	@ModelAttribute("opportunityRequests")
	public List<OpportunityRequest> getOpportunityRequests(){
		return opportunitiesService.listOpportunityRequests(userService.getCurrentUser());
	}
	
	@ModelAttribute("user")
    public User getUser() {
        return userService.getCurrentUser();
    }

}