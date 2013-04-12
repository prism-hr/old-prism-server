package com.zuehlke.pgadmissions.mail.refactor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;

@Service
public class EmailDigestService {

    @Autowired
    public EmailDigestService() {
    }
    
    public PrismEmailMessage createDigest(final List<ApplicationForm> forms) {
        PrismEmailMessageBuilder builder = new PrismEmailMessageBuilder();
        
        builder.emailTemplate(EmailTemplateName.DIGEST)
        builder.subjectCode(subjectCode)
    }
}
