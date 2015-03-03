package com.zuehlke.pgadmissions.services;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.UserFeedbackDAO;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory;
import com.zuehlke.pgadmissions.domain.institution.Institution;
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
	private RoleService roleService;
	
	@Inject
	private UserService userService;

	public void createFeedback(UserFeedbackDTO userFeedbackDTO) {
		User user = userService.getById(userFeedbackDTO.getUser());
		Institution institution = institutionService.getById(userFeedbackDTO.getInstitution());

		UserFeedback userFeedback = new UserFeedback().withUser(user).withRoleCategory(userFeedbackDTO.getRoleCategory()).withInstitution(institution)
		        .withDeclinedResponse(false).withRating(userFeedbackDTO.getRating()).withContent(userFeedbackDTO.getContent())
		        .withRecommended(BooleanUtils.toBoolean(userFeedbackDTO.getRecommended())).withCreatedTimestamp(new DateTime());
		entityService.save(userFeedback);

		setLastSequenceIdentifier(userFeedback);
	}

	public void declineFeedback(UserFeedbackDeclineDTO userFeedbackDeclineDTO) {
		User user = userService.getById(userFeedbackDeclineDTO.getUser());
		Institution institution = institutionService.getById(userFeedbackDeclineDTO.getInstitution());

		UserFeedback userFeedback = new UserFeedback().withUser(user).withRoleCategory(userFeedbackDeclineDTO.getRoleCategory()).withInstitution(institution)
		        .withDeclinedResponse(true).withCreatedTimestamp(new DateTime());
		entityService.save(userFeedback);

		setLastSequenceIdentifier(userFeedback);
	}

	public List<UserFeedback> getUserFeedback(Integer ratingThreshold, String lastSequenceIdentifier) {
		return userFeedbackDAO.getUserFeedback(ratingThreshold, lastSequenceIdentifier);
	}

	public PrismRoleCategory getRoleCategoryUserFeedbackRequiredFor(User user) {
		PrismRoleCategory required = null;
		DateTime latestFeedbackTimestamp = userFeedbackDAO.getLatestUserFeedbackTimestamp(user);
		if (latestFeedbackTimestamp.isBefore(new DateTime().minusYears(1))) {
			for (PrismRoleCategory prismRoleCategory : PrismRoleCategory.values()) {
				if (!roleService.getUserRolesByRoleCategory(user, prismRoleCategory).isEmpty()) {
					return required;
				}
			}
		}
		return required;
	}

	private void setLastSequenceIdentifier(UserFeedback userFeedback) {
		userFeedback.setSequenceIdentifier(Long.toString(new DateTime().getMillis()) + String.format("%010d", userFeedback.getId()));
	}

}
