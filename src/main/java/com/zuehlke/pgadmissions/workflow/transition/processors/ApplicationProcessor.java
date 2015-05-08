package com.zuehlke.pgadmissions.workflow.transition.processors;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_REJECTION_SYSTEM;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Component
public class ApplicationProcessor implements ResourceProcessor {

    @Inject
    private ApplicationContext applicationContext;

    @Override
    public void process(Resource resource, Comment comment) throws Exception {
        if (comment.isApplicationAutomatedRejectionComment()) {
            setRejectionReasonSystem(resource, comment);
        }

    }

    private void setRejectionReasonSystem(Resource resource, Comment comment) throws Exception {
        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localize((Application) resource);
        comment.setRejectionReasonSystem(propertyLoader.load(APPLICATION_COMMENT_REJECTION_SYSTEM));
    }

}
