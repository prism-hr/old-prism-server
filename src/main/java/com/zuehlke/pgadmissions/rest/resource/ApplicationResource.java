package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.rest.domain.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.domain.application.ApplicationListRowRepresentation;
import com.zuehlke.pgadmissions.rest.domain.application.ApplicationRepresentation;
import com.zuehlke.pgadmissions.services.*;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = {"api/applications"})
public class ApplicationResource {

    @Autowired
    private EntityService entityService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private DozerBeanMapper dozerBeanMapper;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Transactional
    public ApplicationRepresentation getApplication(@PathVariable Integer id) {
        User currentUser = userService.getCurrentUser();
        Application application = entityService.getById(Application.class, id);
        if (application == null) {
            return null;
        }

        ApplicationRepresentation representation = dozerBeanMapper.map(application, ApplicationRepresentation.class);

        List<Comment> comments = commentService.getVisibleComments(application, currentUser);
        representation.setComments(Lists.<CommentRepresentation>newArrayListWithExpectedSize(comments.size()));
        for(Comment comment : comments) {
            representation.getComments().add(dozerBeanMapper.map(comment, CommentRepresentation.class));
        }

        List<PrismAction> permittedActions = actionService.getPermittedActions(application, currentUser);
        representation.getActions().addAll(permittedActions);
        return representation;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ApplicationListRowRepresentation> getApplications(@RequestParam Integer page, @RequestParam(value = "per_page") Integer perPage) {
        List<ResourceConsoleListRowDTO> consoleListBlock = resourceService.getConsoleListBlock(Application.class, page, perPage);
        List<ApplicationListRowRepresentation> representations = Lists.newArrayList();
        for (ResourceConsoleListRowDTO appDTO : consoleListBlock) {
            representations.add(dozerBeanMapper.map(appDTO, ApplicationListRowRepresentation.class));
        }
        return representations;
    }

}
