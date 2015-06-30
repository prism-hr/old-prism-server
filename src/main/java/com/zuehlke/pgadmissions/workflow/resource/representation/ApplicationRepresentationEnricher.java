package com.zuehlke.pgadmissions.workflow.resource.representation;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.dozer.Mapper;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyLocation;
import com.zuehlke.pgadmissions.dto.UserSelectionDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationClientRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRefereeRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class ApplicationRepresentationEnricher implements ResourceRepresentationEnricher<Application, ApplicationClientRepresentation> {

    @Inject
    private ActionService actionService;

    @Inject
    private AdvertService advertService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private CommentService commentService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private UserService userService;

    @Inject
    private Mapper mapper;

    @Override
    public void enrich(Application resource, ApplicationClientRepresentation representation) throws Exception {
        HashMap<Integer, ApplicationRefereeRepresentation> refereeRepresentations = Maps.newHashMap();
        for (ApplicationRefereeRepresentation refereeRepresentation : representation.getReferees()) {
            refereeRepresentations.put(refereeRepresentation.getId(), refereeRepresentation);
        }

        for (ApplicationReferee referee : resource.getReferees()) {
            Comment reference = referee.getComment();
            refereeRepresentations.get(referee.getId()).setCommentId(reference == null ? null : reference.getId());
        }

        List<UserSelectionDTO> interested = userService.getUsersInterestedInApplication(resource);
        List<UserSelectionDTO> potentiallyInterested = userService.getUsersPotentiallyInterestedInApplication(resource, interested);
        List<UserRepresentationSimple> interestedRepresentations = Lists.newArrayListWithCapacity(interested.size());
        List<UserRepresentationSimple> potentiallyInterestedRepresentations = Lists.newArrayListWithCapacity(potentiallyInterested.size());

        for (UserSelectionDTO user : interested) {
            interestedRepresentations.add(mapper.map(user, UserRepresentationSimple.class));
        }

        for (UserSelectionDTO user : potentiallyInterested) {
            potentiallyInterestedRepresentations.add(mapper.map(user, UserRepresentationSimple.class));
        }

        representation.setUsersInterestedInApplication(interestedRepresentations);
        representation.setUsersPotentiallyInterestedInApplication(potentiallyInterestedRepresentations);

        representation.setInterview(commentService.getInterview(resource));

        representation.setOfferRecommendation(commentService.getOfferRecommendation(resource));
        representation.setAssignedSupervisors(commentService.getApplicationSupervisors(resource));

        representation.setPossibleThemes(advertService.getAdvertThemes(resource.getAdvert()));

        ResourceParent parent = (ResourceParent) resource.getParentResource();

        List<ResourceStudyLocation> studyLocations = resourceService.getStudyLocations(parent);
        List<String> availableStudyLocations = Lists.newArrayListWithCapacity(studyLocations.size());
        for (ResourceStudyLocation studyLocation : studyLocations) {
            availableStudyLocations.add(studyLocation.getStudyLocation());
        }
        representation.setPossibleLocations(availableStudyLocations);

        if (!parent.getResourceScope().equals(INSTITUTION)) {
            List<PrismStudyOption> studyOptions = resourceService.getStudyOptions((ResourceOpportunity) parent);
            List<PrismStudyOption> availableStudyOptions = Lists.newArrayListWithCapacity(studyOptions.size());
            for (PrismStudyOption studyOption : studyOptions) {
                availableStudyOptions.add(studyOption);
            }
            representation.setAvailableStudyOptions(availableStudyOptions);
        }

        if (!actionService.hasRedactions(resource, userService.getCurrentUser())) {
            representation.setApplicationRatingAverage(resource.getApplicationRatingAverage());
        }

        representation.setResourceSummary(applicationService.getApplicationSummary(resource.getId()));
    }

}
