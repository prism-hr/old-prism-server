package com.zuehlke.pgadmissions.mail;

import java.io.StringReader;

import javax.mail.MessagingException;
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
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;

import freemarker.template.Template;

@Service
public class MailSender {

    private final Logger logger = LoggerFactory.getLogger(MailSender.class);

    @Value("${email.prod}")
    private boolean productionContext;

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

    public void sendEmail(final MailMessageDTO message) {        
        logger.info(String.format("Sending Email: %s", message.toString()));
        try {
            javaMailSender.send(new MimeMessagePreparator() {
                @Override
                public void prepare(final MimeMessage mimeMessage) throws Exception {
                    final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

                    if (!productionContext) {
                        flagAsDevelopmentEmail(message, messageHelper);
                    }
                    
                    messageHelper.setFrom(emailAddressFrom);

                    for (InternetAddress addresses : message.getToAsInternetAddresses()) {
                        messageHelper.addTo(addresses);
                    }

                    for (InternetAddress addresses : message.getCcAsInternetAddresses()) {
                        messageHelper.addCc(addresses);
                    }

                    for (InternetAddress addresses : message.getBccAsInternetAddresses()) {
                        messageHelper.addBcc(addresses);
                    }

                    if (StringUtils.isNotBlank(message.getReplyToAddress())) {
                        messageHelper.setReplyTo(message.getReplyToAddress());
                    }

                    for (PdfAttachmentInputSource attachment : message.getAttachments()) {
                        messageHelper.addAttachment(attachment.getAttachmentFilename(), attachment, "application/pdf");
                    }

                    NotificationTemplateVersion notificationTemplate = message.getTemplate();

                    Template subjectTemplate = new Template(null, new StringReader(notificationTemplate.getSubject()), freemarkerConfig.getConfiguration());
                    String subject = FreeMarkerTemplateUtils.processTemplateIntoString(subjectTemplate, message.getModel());

                    messageHelper.setSubject(subject);

                    Template contentTemplate = new Template(null, new StringReader(notificationTemplate.getContent()), freemarkerConfig.getConfiguration());
                    MailToPlainTextConverter htmlFormatter = new MailToPlainTextConverter();
                    String htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(contentTemplate, message.getModel());
                    String plainText = htmlFormatter.getPlainText(htmlText);
                    plainText = plainText + "\n\n" + emailBrokenLinkMessage;

                    messageHelper.setText(plainText, htmlText);
                }
            });
        } catch (Exception e) {
            logger.error(String.format("Failed to send email %s", message.toString()), e);
            throw new MailException(message);
        }
    }
    
    private void flagAsDevelopmentEmail(final MailMessageDTO message, final MimeMessageHelper messageHelper) throws MessagingException {
        messageHelper.setTo(emailAddressTo);
        StringBuilder subjectBuilder = new StringBuilder();
        subjectBuilder.append("<NON-PROD-Message: TO: ").append(message.getToAsInternetAddresses().toString());
        subjectBuilder.append(" CC: ").append(message.getCcAsInternetAddresses().toString());
        subjectBuilder.append(" BCC: ").append(message.getBccAsInternetAddresses().toString());
        messageHelper.setSubject(subjectBuilder.toString());
    }
    
}
