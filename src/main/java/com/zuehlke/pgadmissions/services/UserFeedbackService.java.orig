package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.UserFeedbackDAO;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserFeedback;
import com.zuehlke.pgadmissions.rest.dto.user.UserFeedbackDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserFeedbackDeclineDTO;

@Service
@Transactional
public class UserFeedbackService {

	@Inject
	private UserFeedbackDAO userFeedbackDAO;

	@Inject
	private EntityService entityService;

	@Inject
	private InstitutionService institutionService;

	@Inject
	private ResourceService resourceService;

	@Inject
	private RoleService roleService;

	@Inject
	private UserService userService;

	public void createFeedback(UserFeedbackDTO userFeedbackDTO) {
		User user = userService.getById(userFeedbackDTO.getUser());
		Institution institution = getFeedbackInstitution(user,
		        resourceService.getById(userFeedbackDTO.getResourceScope().getResourceClass(), userFeedbackDTO.getResourceId()));

		if (institution == null) {
			UserFeedback userFeedback = new UserFeedback().withUser(user).withRoleCategory(userFeedbackDTO.getRoleCategory()).withInstitution(institution)
			        .withDeclinedResponse(false).withRating(userFeedbackDTO.getRating()).withContent(userFeedbackDTO.getContent())
			        .withCreatedTimestamp(new DateTime());
			entityService.save(userFeedback);
			setLastSequenceIdentifier(userFeedback);
		}
	}

	public void declineFeedback(UserFeedbackDeclineDTO userFeedbackDeclineDTO) {
		User user = userService.getById(userFeedbackDeclineDTO.getUser());
		Institution institution = getFeedbackInstitution(user,
		        resourceService.getById(userFeedbackDeclineDTO.getResourceScope().getResourceClass(), userFeedbackDeclineDTO.getResourceId()));

		if (institution == null) {
			UserFeedback userFeedback = new UserFeedback().withUser(user).withRoleCategory(userFeedbackDeclineDTO.getRoleCategory()).withInstitution(institution)
			        .withDeclinedResponse(true).withCreatedTimestamp(new DateTime());
			entityService.save(userFeedback);
			setLastSequenceIdentifier(userFeedback);
		}
	}

	public List<UserFeedback> getUserFeedback(Integer ratingThreshold, String lastSequenceIdentifier) {
		return userFeedbackDAO.getUserFeedback(ratingThreshold, lastSequenceIdentifier);
	}

	public PrismRoleCategory getRoleCategoryUserFeedbackRequiredFor(User user) {
		PrismRoleCategory required = null;
		DateTime latestFeedbackTimestamp = userFeedbackDAO.getLatestUserFeedbackTimestamp(user);
		if (latestFeedbackTimestamp.isBefore(new DateTime().minusYears(1))) {
			for (PrismRoleCategory prismRoleCategory : PrismRoleCategory.values()) {
				if (!roleService.getUserRolesByRoleCategory(user, prismRoleCategory, SYSTEM).isEmpty()) {
					return required;
				}
			}
		}
		return required;
	}

	private Institution getFeedbackInstitution(User user, Resource resource) {
		if (resource.getResourceScope() == SYSTEM) {
			return institutionService.getUserPrimaryInstitution(user);
		}
		return resource.getInstitution();
	}

	private void setLastSequenceIdentifier(UserFeedback userFeedback) {
		userFeedback.setSequenceIdentifier(Long.toString(new DateTime().getMillis()) + String.format("%010d", userFeedback.getId()));
	}

}
