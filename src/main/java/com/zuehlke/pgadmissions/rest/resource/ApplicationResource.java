package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.rest.domain.application.ApplicationListRowRepresentation;
import com.zuehlke.pgadmissions.rest.domain.application.ApplicationRepresentation;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.UserService;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = {"api/applications"})
public class ApplicationResource {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private DozerBeanMapper dozerBeanMapper;

    @RequestMapping(value = "/{id}", params = "action=APPLICATION_VIEW_AS_CREATOR", method = RequestMethod.GET)
    @Transactional
    public ApplicationRepresentation getApplication(@PathVariable Integer id) {
        User currentUser = userService.getCurrentUser();
        Application application = applicationService.getById(id);

        ApplicationRepresentation representation = dozerBeanMapper.map(application, ApplicationRepresentation.class);
        List<PrismAction> permittedActions = actionService.getPermittedActions(application, currentUser);
        representation.getPermittedActions().addAll(permittedActions);
        return representation;
    }

}
