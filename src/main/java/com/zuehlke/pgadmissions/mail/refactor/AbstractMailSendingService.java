package com.zuehlke.pgadmissions.mail.refactor;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.services.UserService;

public class AbstractMailSendingService {

    protected final UserService userService;
    
    protected class UpdateDigestNotificationClosure implements Closure {
        private final DigestNotificationType type;

        public UpdateDigestNotificationClosure(final DigestNotificationType type) {
            this.type = type;
        }

        @Override
        public void execute(final Object input) {
            userService.setDigestNotificationType((RegisteredUser) input, type);
        }
    }
    
    public AbstractMailSendingService(final UserService userService) {
        this.userService = userService;
    }
    
    @SuppressWarnings("unchecked")
    protected Collection<RegisteredUser> getSupervisorsFromLatestApprovalRound(final ApplicationForm form) {
        if (form.getLatestApprovalRound() != null) {
            return CollectionUtils.collect(form.getLatestApprovalRound().getSupervisors(), new Transformer() {
                @Override
                public Object transform(final Object input) {
                    return ((Supervisor) input).getUser();
                }
            });
        }
        return Collections.emptyList();
    }
    
    protected Collection<RegisteredUser> getProgramAdministrators(final ApplicationForm form) {
        return form.getProgram().getAdministrators();
    }
    
    protected String getAdminsEmailsCommaSeparatedAsString(List<RegisteredUser> administrators) {
		Set<String> administratorMails = new LinkedHashSet<String>();
		for (RegisteredUser admin : administrators) {
			administratorMails.add(admin.getEmail());
		}
		return StringUtils.join(administratorMails.toArray(new String[] {}), ";");
	}
    
    @SuppressWarnings("unchecked")
    protected Collection<RegisteredUser> getInterviewersFromLatestInterviewRound(final ApplicationForm form) {
        if (form.getLatestInterview() != null) {
            return CollectionUtils.collect(form.getLatestInterview().getInterviewers(), new Transformer() {
                @Override
                public Object transform(final Object input) {
                    return ((Interviewer) input).getUser();
                }
            });
        }
        return Collections.emptyList();
    }
    
    @SuppressWarnings("unchecked")
    protected Collection<RegisteredUser> getReviewersFromLatestReviewRound(final ApplicationForm form) {
        if (form.getLatestReviewRound() != null) {
            return CollectionUtils.collect(form.getLatestReviewRound().getReviewers(), new Transformer() {
                @Override
                public Object transform(final Object input) {
                    return ((Reviewer) input).getUser();
                }
            });
        }
        return Collections.emptyList();
    }
}
