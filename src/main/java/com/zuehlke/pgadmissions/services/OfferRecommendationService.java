package com.zuehlke.pgadmissions.services;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.CommentDAO;
import com.zuehlke.pgadmissions.dao.ProgrammeDetailDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.OfferRecommendedComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.mail.MailSendingService;

@Service
@Transactional
public class OfferRecommendationService {
    // TODO simplified, fix tests

    private final Logger log = LoggerFactory.getLogger(OfferRecommendationService.class);

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ApplicationFormService applicationsService;

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private MailSendingService mailSendingService;
    
    @Autowired
    private ExportQueueService approvedSenderService;
    
    @Autowired
    private WorkflowService applicationFormUserRoleService;

    public boolean moveToApproved(ApplicationForm form, OfferRecommendedComment offerRecommendedComment) {
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

    private void sendNotificationToApplicant(ApplicationForm form) {
        try {
            mailSendingService.sendApprovedNotification(form);
        } catch (Exception e) {
            log.warn("{}", e);
        }
    }
    
    public void sendToPortico(ApplicationForm form) {
        if (form.getProgram().getProgramFeed() != null) {
            approvedSenderService.sendToPortico(form);
        }
    }

}
