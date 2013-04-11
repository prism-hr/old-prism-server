package com.zuehlke.pgadmissions.mail.refactor;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.UserService;

public abstract class AbstractScheduledMailSendingService {

    private final AbstractMailSender mailSender;
    
    private final UserService userService;
    
    public AbstractScheduledMailSendingService(final AbstractMailSender mailSender, final UserService userService) {
        this.mailSender = mailSender;
        this.userService = userService;
    }
    
    public void sendEmail(final PrismEmailMessage emailMessage) {
        mailSender.sendEmail(Arrays.asList(emailMessage));
    }
    
    public void sendEmail(final Collection<PrismEmailMessage> emailMessages) {
        CollectionUtils.forAllDo(emailMessages, new Closure() {
            @Override
            public void execute(final Object input) {
                PrismEmailMessage message = (PrismEmailMessage) input;
                for (RegisteredUser user : message.getDigestReceiver()) {
                    //userService.updateNeedsToReceiveNewDigest(user);
                }
            }
        });
        mailSender.sendEmail(emailMessages);
    }
}
