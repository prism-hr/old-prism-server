package com.zuehlke.pgadmissions.services;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service
@Transactional
public class ReviewService {

    @Autowired
    private ApplicationService applicationsService;

    @Autowired
    private MailSendingService mailService;

    @Autowired
    private StateDAO stateDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    public void moveApplicationToReview(int applicationId, Comment comment) {
        Application application = applicationsService.getById(applicationId);
        User user = userService.getCurrentUser();
        Comment latestAssignReviewersComment = applicationsService.getLatestStateChangeComment(application, PrismAction.APPLICATION_ASSIGN_REVIEWERS);

        LocalDate baseDate;
        if (application.getClosingDate() == null || latestAssignReviewersComment != null) {
            baseDate = new LocalDate();
        } else {
            baseDate = application.getClosingDate();
        }

        State state = stateDAO.getById(PrismState.APPLICATION_REVIEW);
        // TODO write query to get duration in minutes
        Integer durationInMinutes = 0; // state.getDurationInMinutes();
        
        application.setDueDate(baseDate.plus(new Period().withMinutes(durationInMinutes)));
        application.setState(state);

        comment.setUser(user);
        comment.setApplication(application);
        commentService.save(comment);

    }

}
