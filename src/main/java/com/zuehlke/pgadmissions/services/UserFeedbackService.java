package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.UserFeedbackDAO;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserFeedback;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.rest.dto.user.UserFeedbackContentDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserFeedbackDTO;

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

	public PrismRoleCategory getRoleCategoryUserFeedbackRequiredFor(User user) {
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
