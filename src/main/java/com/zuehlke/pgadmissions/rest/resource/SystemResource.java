package com.zuehlke.pgadmissions.rest.resource;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.services.*;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"api/systems"})
public class SystemResource {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private DozerBeanMapper dozerBeanMapper;

}
