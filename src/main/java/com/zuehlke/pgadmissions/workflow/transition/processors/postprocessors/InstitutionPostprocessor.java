package com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

@Component
public class InstitutionPostprocessor implements ResourceProcessor<Institution> {

    @Inject
    private AdvertService advertService;

    @Override
    public void process(Institution resource, Comment comment) {
        if (comment.isViewEditComment()) {
            comment.getAssignedUsers().stream().filter(assignee -> assignee.getRoleTransitionType().equals(CREATE)).forEach(assignee -> {
                
            });
        }

        if (comment.isResourceEndorsementComment()) {
            advertService.provideAdvertRating(resource.getAdvert(), comment.getUser(), comment.getRating());
        }
    }

}
