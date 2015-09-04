package com.zuehlke.pgadmissions.workflow.transition.populators;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.resource.Resume;
import com.zuehlke.pgadmissions.services.ResumeService;

@Component
public class ResumePopulator implements ResourcePopulator<Resume> {

    @Inject
    private ResumeService resumeService;

    @Override
    public void populate(Resume resource) {
        resumeService.prepopulateResume(resource);
    }

}
