package com.zuehlke.pgadmissions.workflow.transition.processors.postprocessors;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resume;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

@Component
public class ResumePostprocessor implements ResourceProcessor<Resume> {

    @Inject
    private ApplicationService applicationService;

    @Override
    public void process(Resume resource, Comment comment) {
        if (comment.isApplicationRatingComment()) {
            applicationService.syncronizeApplicationRating(resource);
        }
    }

}
