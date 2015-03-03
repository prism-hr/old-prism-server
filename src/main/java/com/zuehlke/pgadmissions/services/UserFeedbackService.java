package com.zuehlke.pgadmissions.services;

import com.zuehlke.pgadmissions.dao.UserFeedbackDAO;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserFeedback;
import com.zuehlke.pgadmissions.rest.dto.user.UserFeedbackContentDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserFeedbackDTO;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

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
        UserFeedbackContentDTO contentDTO = userFeedbackDTO.getContent();
        User user = userService.getById(userFeedbackDTO.getUser());
        Institution institution = institutionService.getById(5243); // FIXME get right institution

        UserFeedback userFeedback = new UserFeedback().withUser(user).withRoleCategory(userFeedbackDTO.getRoleCategory()).withInstitution(institution)
                .withDeclinedResponse(contentDTO == null).withCreatedTimestamp(new DateTime());
        if (contentDTO != null) {
            userFeedback.setRating(contentDTO.getRating());
            userFeedback.setContent(contentDTO.getContent());
            userFeedback.setFeatureRequests(contentDTO.getFeatureRequests());
        }
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
