package com.zuehlke.pgadmissions.domain.user;

import com.zuehlke.pgadmissions.workflow.user.PrismUserReassignmentProcessor;

public interface UserAssignment<T extends PrismUserReassignmentProcessor> {

    Class<T> getUserReassignmentProcessor();

    boolean isResourceUserAssignmentProperty();

}
