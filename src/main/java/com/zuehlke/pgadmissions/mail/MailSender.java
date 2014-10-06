package com.zuehlke.pgadmissions.mail;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.dto.MailMessageDTO;
import com.zuehlke.pgadmissions.services.builders.pdf.mail.AttachmentInputSource;

import freemarker.template.Template;

@Service
public class MailSender {

    private final Logger logger = LoggerFactory.getLogger(MailSender.class);

    @Value("${context.environment}")
    private String contextEnvironment;

    @Value("${email.address.from}")
    private String emailAddressFrom;

    @Value("${email.address.to}")
    private String emailAddressTo;

    @Value("${email.broken.link.message}")
    private String emailBrokenLinkMessage;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private FreeMarkerConfig freemarkerConfig;

    @Autowired
    private MailToPlainTextConverter mailToPlainTextConverter;

    public void sendEmail(final MailMessageDTO message) {

        final NotificationTemplateVersion templateVersion = message.getTemplate();

        if (contextEnvironment.equals("prod") || contextEnvironment.equals("uat")) {
            logger.info(String.format("Sending Production Email: %s", message.toString()));
            try {
                javaMailSender.send(new MimeMessagePreparator() {
                    @Override
                    public void prepare(final MimeMessage mimeMessage) throws Exception {
                        final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

                        String templateReference = templateVersion.getNotificationConfiguration().getNotificationTemplate().getId().name();

                        String templateName = templateReference + "_subject_" + templateVersion.getId();
                        Template subjectTemplate = new Template(templateName, new StringReader(templateVersion.getSubject()), freemarkerConfig
                                .getConfiguration());
                        String subject = FreeMarkerTemplateUtils.processTemplateIntoString(subjectTemplate, message.getModel());

                        String contentName = templateReference + "_content_" + templateVersion.getId();
                        Template contentTemplate = new Template(contentName, new StringReader(templateVersion.getContent()), freemarkerConfig
                                .getConfiguration());
                        String htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(contentTemplate, message.getModel());
                        String plainText = mailToPlainTextConverter.getPlainText(htmlText);
                        plainText = plainText + "\n\n" + emailBrokenLinkMessage;

                        messageHelper.setTo(convertToInternetAddresses(message.getTo()));
                        messageHelper.setSubject(subject);
                        messageHelper.setText(plainText, htmlText);
                        messageHelper.setFrom(emailAddressFrom);
                        for (AttachmentInputSource attachment : message.getAttachments()) {
                            messageHelper.addAttachment(attachment.getAttachmentFilename(), attachment, "application/pdf");
                        }
                    }
                });
            } catch (Exception e) {
                if (templateVersion.getNotificationConfiguration().getNotificationTemplate().getNotificationType() == PrismNotificationType.INDIVIDUAL) {
                    throw new Error(e);
                } else {
                    logger.error(String.format("Failed to send email %s", message.toString()), e);
                }
            }
        } else {
            logger.info(String.format("Sending Development Email: %s", message.toString()));
        }
    }

    private InternetAddress convertToInternetAddresses(User user) {
        try {
            StringBuilder stringBuilder = new StringBuilder(user.getFirstName());
            if (!StringUtils.isEmpty(user.getFirstName2())) {
                stringBuilder.append(" ").append(user.getFirstName2());
            }
            if (!StringUtils.isEmpty(user.getFirstName3())) {
                stringBuilder.append(" ").append(user.getFirstName3());
            }
            stringBuilder.append(" ").append(user.getLastName());
            return new InternetAddress(user.getEmail(), stringBuilder.toString());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
