package uk.co.alumeni.prism.domain.user;

import uk.co.alumeni.prism.workflow.user.PrismUserReassignmentProcessor;

public interface UserAssignment<T extends PrismUserReassignmentProcessor> {

    Class<T> getUserReassignmentProcessor();

    boolean isResourceUserAssignmentProperty();

}
