package com.zuehlke.pgadmissions.workflow.executors.action;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionPartnerDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunityDTO;
import com.zuehlke.pgadmissions.rest.dto.comment.CommentDTO;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.PROGRAM_COMMENT_UPDATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_VIEW_EDIT;

@Component
public class ProgramExecutor implements ActionExecutor {
    
    @Inject
    private ActionService actionService;
    
    @Inject
    private CommentService commentService;
    
    @Inject
    private ProgramService programService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private ApplicationContext applicationContext;
    
    @Override
    public ActionOutcomeDTO execute(Integer resourceId, CommentDTO commentDTO) throws Exception {
        User user = userService.getById(commentDTO.getUser());
        Program program = programService.getById(resourceId);
        
        PrismAction actionId = commentDTO.getAction();
        Action action = actionService.getById(actionId);
        
        OpportunityDTO programDTO = commentDTO.getResource().getProgram();
        Comment comment = prepareProcessResourceComment(program, user, action, programDTO, commentDTO);
        programService.update(resourceId, programDTO, comment);
        return actionService.executeUserAction(program, action, comment);
    }
    
    public Comment prepareProcessResourceComment(Program program, User user, Action action, OpportunityDTO programDTO, CommentDTO commentDTO) throws Exception {
        String commentContent = action.getId().equals(PROGRAM_VIEW_EDIT) ? applicationContext.getBean(PropertyLoader.class).localize(program)
                .load(PROGRAM_COMMENT_UPDATED) : commentDTO.getContent();
        
        InstitutionPartnerDTO partnerDTO = programDTO.getPartner();
        Comment comment = new Comment().withUser(user).withResource(program).withContent(commentContent).withAction(action)
                .withRemovedPartner(partnerDTO != null && partnerDTO.isEmpty()).withCreatedTimestamp(new DateTime())
                .withDeclinedResponse(false);
        commentService.appendCommentProperties(comment, commentDTO);
        
        return comment;
    }
    
}
