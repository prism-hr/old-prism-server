package com.zuehlke.pgadmissions.mail;

import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.dto.MailMessageDTO;
import com.zuehlke.pgadmissions.services.builders.pdf.mail.AttachmentInputSource;
import freemarker.template.Template;
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

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

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

        final NotificationTemplateVersion notificationTemplate = message.getTemplate();

        try {
            String templateName = notificationTemplate.getNotificationTemplate().getId().name() + "_subject_" + notificationTemplate.getId();
            Template subjectTemplate = new Template(templateName, new StringReader(notificationTemplate.getSubject()), freemarkerConfig.getConfiguration());
            final String subject = FreeMarkerTemplateUtils.processTemplateIntoString(subjectTemplate, message.getModel());

            templateName = notificationTemplate.getNotificationTemplate().getId().name() + "_content_" + notificationTemplate.getId();
            Template contentTemplate = new Template(templateName, new StringReader(notificationTemplate.getContent()), freemarkerConfig.getConfiguration());
            final String htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(contentTemplate, message.getModel());
            final String plainText = mailToPlainTextConverter.getPlainText(htmlText) + "\n\n" + emailBrokenLinkMessage;


            if (contextEnvironment.equals("prod") || contextEnvironment.equals("uat")) {
                logger.info(String.format("Sending Production Email: %s", message.toString()));
                javaMailSender.send(new MimeMessagePreparator() {
                    @Override
                    public void prepare(final MimeMessage mimeMessage) throws Exception {
                        final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

                        messageHelper.setTo(convertToInternetAddresses(message.getTo()));
                        messageHelper.setSubject(subject);
                        messageHelper.setText(plainText, htmlText);
                        messageHelper.setFrom(emailAddressFrom);
                        for (AttachmentInputSource attachment : message.getAttachments()) {
                            messageHelper.addAttachment(attachment.getAttachmentFilename(), attachment, "application/pdf");
                        }
                    }
                });
            } else {
                logger.info(String.format("Sending Development Email: %s", message.toString()));
            }

        } catch (Exception e) {
            if (notificationTemplate.getNotificationTemplate().getNotificationType() == PrismNotificationType.INDIVIDUAL) {
                throw new Error(e);
            } else {
                logger.error(String.format("Failed to send email %s", message.toString()), e);
            }
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
