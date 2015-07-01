package com.zuehlke.pgadmissions.workflow.transition.processors;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.AdvertService;

@Component
public class ProgramProcessor implements ResourceProcessor {

    @Inject
    private AdvertService advertService;

    @Override
    public void process(Resource resource, Comment comment) throws Exception {
        Program program = (Program) resource;

        if (comment.isSponsorshipComment()) {
            advertService.synchronizeSponsorship(program, comment);
        }
    }

}
