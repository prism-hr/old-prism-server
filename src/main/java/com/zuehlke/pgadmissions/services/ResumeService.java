package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.RESUME_RETIRE;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ResumeDAO;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resume;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;

@Service
@Transactional
public class ResumeService {

    @Inject
    private ResumeDAO resumeDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private ApplicationService applicationService;

    public void prepopulateResume(Resume resume) {
        User user = resume.getUser();
        Resume templateResume = resumeDAO.getPreviousCompletedResume(resume.getUser(), resume.getOpportunityCategories());
        templateResume = templateResume == null ? resumeDAO.getPreviousCompletedResume(user, null) : templateResume;

        if (templateResume != null) {
            applicationService.prepopulateApplication(resume, templateResume);
        }
    }

    public void retireResume(Resume resume) {
        Action action = actionService.getById(RESUME_RETIRE);
        Comment comment = new Comment().withUser(resume.getUser()).withResource(resume).withAction(action).withDeclinedResponse(false).withCreatedTimestamp(new DateTime());
        actionService.executeAction(resume, action, comment);
    }

}
