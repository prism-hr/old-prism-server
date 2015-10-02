package com.zuehlke.pgadmissions.workflow.resolvers.state.transition.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_STUDENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_STUDENT_UNVERIFIED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_PENDING_PARTNER_APPROVAL;
import static org.apache.commons.collections.CollectionUtils.containsAny;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.workflow.StateTransition;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationConfirmedOfferOutcome implements StateTransitionResolver<Application> {

    @Inject
    private ResourceService resourceService;

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Application resource, Comment comment) {
        if (resource.getOpportunityType().isRequireEndorsement()) {
            List<Integer> creatorDepartments = resourceService.getResourcesForWhichUserHasRoles(resource.getUser(), DEPARTMENT_STUDENT, DEPARTMENT_STUDENT_UNVERIFIED);
            List<Integer> recruiterDepartment = resourceService.getResourcesForWhichUserHasRoles(comment.getUser(), DEPARTMENT_ADMINISTRATOR, DEPARTMENT_APPROVER);
            if (containsAny(creatorDepartments, recruiterDepartment)) {
                return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_APPROVED_COMPLETED);
            }
            return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_APPROVED_PENDING_PARTNER_APPROVAL);
        }
        return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_APPROVED_COMPLETED);
    }

}