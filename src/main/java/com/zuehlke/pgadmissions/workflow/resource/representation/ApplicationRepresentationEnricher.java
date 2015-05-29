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
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyLocation;
import com.zuehlke.pgadmissions.dto.UserSelectionDTO;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.AbstractResourceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.RefereeRepresentation;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class ApplicationRepresentationEnricher implements ResourceRepresentationEnricher {

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
    public void enrich(PrismScope resourceScope, Integer resourceId, AbstractResourceRepresentation representation) throws Exception {
        Application application = applicationService.getById(resourceId);
        ApplicationExtendedRepresentation applicationRepresentation = (ApplicationExtendedRepresentation) representation;

        HashMap<Integer, RefereeRepresentation> refereeRepresentations = Maps.newHashMap();
        for (RefereeRepresentation refereeRepresentation : applicationRepresentation.getReferees()) {
            refereeRepresentations.put(refereeRepresentation.getId(), refereeRepresentation);
        }

        for (ApplicationReferee referee : application.getReferees()) {
            Comment reference = referee.getComment();
            refereeRepresentations.get(referee.getId()).setCommentId(reference == null ? null : reference.getId());
        }

        List<UserSelectionDTO> interested = userService.getUsersInterestedInApplication(application);
        List<UserSelectionDTO> potentiallyInterested = userService.getUsersPotentiallyInterestedInApplication(application, interested);
        List<UserRepresentation> interestedRepresentations = Lists.newArrayListWithCapacity(interested.size());
        List<UserRepresentation> potentiallyInterestedRepresentations = Lists.newArrayListWithCapacity(potentiallyInterested.size());

        for (UserSelectionDTO user : interested) {
            interestedRepresentations.add(mapper.map(user, UserRepresentation.class));
        }

        for (UserSelectionDTO user : potentiallyInterested) {
            potentiallyInterestedRepresentations.add(mapper.map(user, UserRepresentation.class));
        }

        applicationRepresentation.setUsersInterestedInApplication(interestedRepresentations);
        applicationRepresentation.setUsersPotentiallyInterestedInApplication(potentiallyInterestedRepresentations);

        applicationRepresentation.setInterview(commentService.getInterview(application));

        applicationRepresentation.setOfferRecommendation(commentService.getOfferRecommendation(application));
        applicationRepresentation.setAssignedSupervisors(commentService.getApplicationSupervisors(application));

        applicationRepresentation.setPossibleThemes(advertService.getAdvertThemes(application));

        ResourceParent parent = (ResourceParent) application.getParentResource();

        List<ResourceStudyLocation> studyLocations = resourceService.getStudyLocations(parent);
        List<String> availableStudyLocations = Lists.newArrayListWithCapacity(studyLocations.size());
        for (ResourceStudyLocation studyLocation : studyLocations) {
            availableStudyLocations.add(studyLocation.getStudyLocation());
        }
        applicationRepresentation.setPossibleLocations(availableStudyLocations);

        if (!parent.getResourceScope().equals(INSTITUTION)) {
            List<PrismStudyOption> studyOptions = resourceService.getStudyOptions((ResourceOpportunity) parent);
            List<PrismStudyOption> availableStudyOptions = Lists.newArrayListWithCapacity(studyOptions.size());
            for (PrismStudyOption studyOption : studyOptions) {
                availableStudyOptions.add(studyOption);
            }
            applicationRepresentation.setAvailableStudyOptions(availableStudyOptions);
        }

        if (!actionService.hasRedactions(application, userService.getCurrentUser())) {
            applicationRepresentation.setApplicationRatingAverage(application.getApplicationRatingAverage());
        }

        applicationRepresentation.setResourceSummary(applicationService.getApplicationSummary(application.getId()));
    }

}
