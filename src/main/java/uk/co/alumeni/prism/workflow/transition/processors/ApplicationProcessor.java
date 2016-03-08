package uk.co.alumeni.prism.workflow.transition.processors;

import static uk.co.alumeni.prism.domain.definitions.PrismRejectionReason.POSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_HIRING_MANAGER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType.CREATE;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.application.ApplicationHiringManager;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.comment.CommentOfferDetail;
import uk.co.alumeni.prism.domain.comment.CommentPositionDetail;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Role;
import uk.co.alumeni.prism.services.ApplicationService;
import uk.co.alumeni.prism.services.EntityService;
import uk.co.alumeni.prism.services.RoleService;

@Component
public class ApplicationProcessor implements ResourceProcessor<Application> {

    @Inject
    private ApplicationService applicationService;

    @Inject
    private EntityService entityService;

    @Inject
    private RoleService roleService;

    @Override
    public void process(Application resource, Comment comment) {
        if (comment.isApplicationAssignRefereesComment()) {
            appendApplicationReferees(resource, comment);
        }

        if (comment.isApplicationUpdateRefereesComment()) {
            appendApplicationReferees(resource, comment);
        }

        if (comment.isApplicationOfferRecommendationComment()) {
            synchronizeOfferRecommendation(resource, comment);
        }

        if (comment.isApplicationAutomatedRejectionComment()) {
            comment.setRejectionReason(POSITION);
        }
    }

    private void appendApplicationReferees(Application application, Comment comment) {
        Role role = roleService.getById(APPLICATION_REFEREE);
        for (User user : applicationService.getApplicationRefereesNotResponded(application)) {
            comment.addAssignedUser(user, role, CREATE);
        }
    }

    private void synchronizeOfferRecommendation(Application application, Comment comment) {
        CommentPositionDetail positionDetail = comment.getPositionDetail();
        if (positionDetail != null) {
            application.setOfferedPositionName(positionDetail.getPositionName());
            application.setOfferedPositionDescription(positionDetail.getPositionDescription());
        }

        CommentOfferDetail offerDetail = comment.getOfferDetail();
        if (offerDetail != null) {
            application.setOfferedStartDate(offerDetail.getPositionProvisionalStartDate());
            application.setOfferedAppointmentConditions(offerDetail.getAppointmentConditions());
        }

        application.getHiringManagers().clear();
        comment.getAssignedUsers().stream().forEach(assignedUser -> { //
                    if (assignedUser.getRole().getId().equals(APPLICATION_HIRING_MANAGER) && assignedUser.getRoleTransitionType().equals(CREATE)) {
                        ApplicationHiringManager hiringManager = entityService.getOrCreate(new ApplicationHiringManager().withApplication(application)
                                .withUser(assignedUser.getUser()));
                        application.addHiringManager(hiringManager);
                    }
                });
    }
}
