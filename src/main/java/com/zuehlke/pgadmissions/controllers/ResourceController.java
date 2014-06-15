package com.zuehlke.pgadmissions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.PrismResourceDynamic;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = "application/txt")
    public <T extends PrismResourceDynamic> String getConsoleListBlockTest() {
        User user = userService.getById(1024);
        return resourceService.getResourceListBlockSelect(user, Application.class, 0, 50);
    }

}
