package com.zuehlke.pgadmissions.rest.resource;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.rest.domain.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.domain.application.ProgramRepresentation;
import com.zuehlke.pgadmissions.rest.domain.application.ResourceListRowRepresentation;
import com.zuehlke.pgadmissions.services.*;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = {"api/programs"})
public class ProgramResource {

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
    public ProgramRepresentation getProgram(@PathVariable Integer id) {
        User currentUser = userService.getCurrentUser();
        Program program = entityService.getById(Program.class, id);
        if (program == null) {
            return null;
        }

        ProgramRepresentation representation = dozerBeanMapper.map(program, ProgramRepresentation.class);

        List<Comment> comments = commentService.getVisibleComments(program, currentUser);
        representation.setComments(Lists.<CommentRepresentation>newArrayListWithExpectedSize(comments.size()));
        for(Comment comment : comments) {
            representation.getComments().add(dozerBeanMapper.map(comment, CommentRepresentation.class));
        }

        List<PrismAction> permittedActions = actionService.getPermittedActions(program, currentUser);
        representation.setActions(permittedActions);
        return representation;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ResourceListRowRepresentation> getPrograms(@RequestParam Integer page, @RequestParam(value = "per_page") Integer perPage) {
        List<ResourceConsoleListRowDTO> consoleListBlock = resourceService.getConsoleListBlock(Program.class, page, perPage);
        List<ResourceListRowRepresentation> representations = Lists.newArrayList();
        for (ResourceConsoleListRowDTO appDTO : consoleListBlock) {
            representations.add(dozerBeanMapper.map(appDTO, ResourceListRowRepresentation.class));
        }
        return representations;
    }

}
