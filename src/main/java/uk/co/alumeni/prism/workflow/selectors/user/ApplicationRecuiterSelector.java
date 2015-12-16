package uk.co.alumeni.prism.workflow.selectors.user;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.CREATE;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.comment.CommentAssignedUser;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.rest.dto.StateActionPendingDTO;
import uk.co.alumeni.prism.rest.dto.user.UserDTO;
import uk.co.alumeni.prism.services.UserService;

@Component
public class ApplicationRecuiterSelector implements PrismReplicableActionUserAssignmentSelector {
    
    @Inject
    private UserService userService;
    
    @Override
    public void setUserAssignments(Comment comment, StateActionPendingDTO stateActionPendingDTO) {
        Set<CommentAssignedUser> commentAssignedUsers = comment.getAssignedUsers();
        if (isNotEmpty(commentAssignedUsers)) {
            PrismRole assignUserRole = null;
            List<UserDTO> assignUserList = Lists.newLinkedList();
            for (CommentAssignedUser commentAssignedUser : commentAssignedUsers) {
                if (commentAssignedUser.getRoleTransitionType().equals(CREATE)) {
                    if (assignUserRole == null) {
                        assignUserRole = commentAssignedUser.getRole().getId();
                    }

                    assignUserList.add(userService.getUserDTO(commentAssignedUser.getUser()));
                }
            }

            if (assignUserRole != null) {
                stateActionPendingDTO.setAssignUserRole(assignUserRole);
                stateActionPendingDTO.setAssignUserList(assignUserList);
            }
        }
    }

}
