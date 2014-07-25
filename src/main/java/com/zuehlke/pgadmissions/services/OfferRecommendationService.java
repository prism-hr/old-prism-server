package com.zuehlke.pgadmissions.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;

@Service
@Transactional
public class OfferRecommendationService {
    // TODO simplified, fix tests

    private final Logger log = LoggerFactory.getLogger(OfferRecommendationService.class);

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ApplicationService applicationsService;

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService mailSendingService;
    
    public boolean moveToApproved(Application form, Comment offerRecommendedComment) {
//        if (ApplicationFormStatus.APPROVAL != form.getStatus().getId()) {
//            throw new IllegalStateException();
//        }
//
//        if (!programInstanceService.isPrefferedStartDateWithinBounds(form)) {
//            Date earliestPossibleStartDate = programInstanceService.getEarliestPossibleStartDate(form);
//            if (earliestPossibleStartDate == null) {
//                return false;
//            }
//            form.getProgramDetails().setStartDate(earliestPossibleStartDate);
//            programmeDetailDAO.save(form.getProgramDetails());
//        }
//
//        form.setStatus(ApplicationFormStatus.APPROVED);
//        sendNotificationToApplicant(form);
//
//        applicationsService.save(form);
//
//        offerRecommendedComment.setApplication(form);
//        offerRecommendedComment.setContent("");
//        offerRecommendedComment.setUser(userService.getCurrentUser());
//        commentDAO.save(offerRecommendedComment);
//        applicationFormUserRoleService.deleteApplicationActions(form);
        return true;
    }

    public void sendToPortico(Application form) {
        if (form.getProgram().isImported()) {
//            approvedSenderService.sendToPortico(form);
        }
    }

}
