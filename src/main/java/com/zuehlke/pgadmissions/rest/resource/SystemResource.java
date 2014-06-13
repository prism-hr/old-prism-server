package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.rest.domain.application.ApplicationListRowRepresentation;
import com.zuehlke.pgadmissions.services.*;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @RequestMapping(value = "/{id}", params = "action=SYSTEM_VIEW_APPLICATION_LIST", method = RequestMethod.GET)
    @Transactional
    public List<ApplicationListRowRepresentation> getApplications(@PathVariable Integer id, @RequestParam Integer page, @RequestParam(value = "per_page") Integer perPage) {
        User currentUser = userService.getCurrentUser();
        List<Application> applications = applicationService.getApplications(currentUser, page, perPage);
        List<ApplicationListRowRepresentation> representations = Lists.newArrayListWithExpectedSize(applications.size());

        for (Application application : applications) {
            ApplicationListRowRepresentation representation = dozerBeanMapper.map(application, ApplicationListRowRepresentation.class);
            List<PrismAction> permittedActions = actionService.getPermittedActions(application, currentUser);
            representation.getPermittedActions().addAll(permittedActions);
            representations.add(representation);
        }
        return representations;
    }

}
