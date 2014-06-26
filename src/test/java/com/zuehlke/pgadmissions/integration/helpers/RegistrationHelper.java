package com.zuehlke.pgadmissions.integration.helpers;

import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.PrismScope;
import com.zuehlke.pgadmissions.mail.MailSenderMock;
import com.zuehlke.pgadmissions.services.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST;

@Service
@Transactional
public class RegistrationHelper {

    @Autowired
    private MailSenderMock mailSenderMock;

    @Autowired
    private RegistrationService registrationService;

    public void assertActivationEmailRegisterAndActivateUser(User user, Resource resource, PrismNotificationTemplate activationTemplate) {
        if (user.getUserAccount() != null) {
            throw new IllegalStateException("User already registered");
        }
        mailSenderMock.assertEmailSent(user, activationTemplate);

        registrationService.submitRegistration(user.withAccount(new UserAccount().withPassword("password")), resource);

        mailSenderMock.assertEmailSent(user, SYSTEM_COMPLETE_REGISTRATION_REQUEST);

        registrationService.activateAccount(user.getActivationCode());
    }

}
