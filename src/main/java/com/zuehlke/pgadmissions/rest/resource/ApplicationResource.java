package com.zuehlke.pgadmissions.rest.resource;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.rest.dto.CommentDTO;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationProgramDetailsDTO;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.UserService;

@RestController
@RequestMapping(value = {"api/applications"})
public class ApplicationResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{applicationId}/programDetails", method = RequestMethod.PUT)
    public void saveProgramDetails(@PathVariable Integer applicationId, @RequestBody ApplicationProgramDetailsDTO programDetailsDTO) {
        applicationService.saveProgramDetails(applicationId, programDetailsDTO);
    }

    @RequestMapping(value = "/{applicationId}/comments", method = RequestMethod.POST)
    public void performAction(@PathVariable Integer applicationId, @RequestParam PrismAction actionId, @RequestBody CommentDTO commentDTO) {
        Application application = entityService.getById(Application.class, applicationId);
        Action action = actionService.getById(actionId);
        Comment comment = new Comment().withContent(commentDTO.getContent()).withUser(userService.getCurrentUser())
                .withAction(entityService.getById(Action.class, action)).withCreatedTimestamp(new DateTime())
                .withDeclinedResponse(commentDTO.getDeclinedResponse());
        actionService.executeUserAction(application, action, comment);
    }

}
