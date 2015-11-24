package uk.co.alumeni.prism.workflow.resolvers.state.transition.application;

import static org.apache.commons.collections.CollectionUtils.containsAny;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.DEPARTMENT_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.DEPARTMENT_APPROVER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.DEPARTMENT_STUDENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.DEPARTMENT_STUDENT_UNVERIFIED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_PENDING_OFFER_ACCEPTANCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_PENDING_PARTNER_APPROVAL;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.workflow.StateTransition;
import uk.co.alumeni.prism.services.ApplicationService;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.StateService;
import uk.co.alumeni.prism.workflow.resolvers.state.transition.StateTransitionResolver;

@Component
public class ApplicationConfirmedOfferOutcome implements StateTransitionResolver<Application> {

    @Inject
    private ApplicationService applicationService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private StateService stateService;

    @Override
    public StateTransition resolve(Application resource, Comment comment) {
        if (isTrue(applicationService.getApplicationOnCourse(resource))) {
            List<Integer> creatorDepartments = resourceService.getResourcesForWhichUserHasRoles(resource.getUser(), DEPARTMENT_STUDENT, DEPARTMENT_STUDENT_UNVERIFIED);
            List<Integer> recruiterDepartment = resourceService.getResourcesForWhichUserHasRoles(comment.getUser(), DEPARTMENT_ADMINISTRATOR, DEPARTMENT_APPROVER);
            if (containsAny(creatorDepartments, recruiterDepartment)) {
                return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_APPROVED_PENDING_OFFER_ACCEPTANCE);
            }
            return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_APPROVED_PENDING_PARTNER_APPROVAL);
        }
        return stateService.getStateTransition(resource, comment.getAction(), APPLICATION_APPROVED_PENDING_OFFER_ACCEPTANCE);
    }

}
