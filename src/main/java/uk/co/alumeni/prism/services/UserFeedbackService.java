package uk.co.alumeni.prism.services;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.dao.UserFeedbackDAO;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserFeedback;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.rest.dto.user.UserFeedbackContentDTO;
import uk.co.alumeni.prism.rest.dto.user.UserFeedbackDTO;

@Service
@Transactional
public class UserFeedbackService {

    @Inject
    private UserFeedbackDAO userFeedbackDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private EntityService entityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    public void createFeedback(UserFeedbackDTO userFeedbackDTO) {
        Resource resource = resourceService.getById(userFeedbackDTO.getResourceScope().getResourceClass(), userFeedbackDTO.getResourceId());
        Action action = actionService.getById(userFeedbackDTO.getAction());
        User user = userService.getCurrentUser();

        UserFeedbackContentDTO contentDTO = userFeedbackDTO.getContent();
        boolean declined = contentDTO == null;
        UserFeedback userFeedback = new UserFeedback().withResource(resource).withUser(user).withRoleCategory(userFeedbackDTO.getRoleCategory())
                .withAction(action).withDeclinedResponse(declined).withCreatedTimestamp(new DateTime());

        if (!declined) {
            userFeedback.setRating(contentDTO.getRating());
            userFeedback.setContent(contentDTO.getContent());
            userFeedback.setFeatureRequest(contentDTO.getFeatureRequest());
        }

        entityService.save(userFeedback);
        setLastSequenceIdentifier(userFeedback);
    }

    public List<UserFeedback> getUserFeedback(Integer ratingThreshold, String lastSequenceIdentifier) {
        return userFeedbackDAO.getUserFeedback(ratingThreshold, lastSequenceIdentifier);
    }

    public PrismRoleCategory getRequiredFeedbackRoleCategory(User user) {
        DateTime latestFeedbackTimestamp = userFeedbackDAO.getLatestUserFeedbackTimestamp(user);
        if (latestFeedbackTimestamp == null || latestFeedbackTimestamp.isBefore(new DateTime().minusYears(1))) {
            for (PrismRoleCategory prismRoleCategory : PrismRoleCategory.values()) {
                if (!roleService.getUserRolesByRoleCategory(user, prismRoleCategory, SYSTEM).isEmpty()) {
                    return prismRoleCategory;
                }
            }
        }
        return null;
    }

    private void setLastSequenceIdentifier(UserFeedback userFeedback) {
        userFeedback.setSequenceIdentifier(Long.toString(new DateTime().getMillis()) + String.format("%010d", userFeedback.getId()));
    }

}
