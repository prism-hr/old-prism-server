package com.zuehlke.pgadmissions.mail;

import java.io.PrintStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;
import com.zuehlke.pgadmissions.services.NotificationTemplateService;

import freemarker.template.Template;

@Service
public class MailSender {

    private static final String PLAIN_TEXT_NOTE = "\n\nIf the links do not work in your email client copy and paste them into your browser.";

    private final Logger log = LoggerFactory.getLogger(MailSender.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${email.prod}")
    private boolean productionEmail;

    @Value("${email.address.from}")
    private String emailAddressFrom;

    @Value("${email.address.to}")
    private String emailAddressTo;

    @Autowired
    private NotificationTemplateService emailTemplateService;

    @Autowired
    private FreeMarkerConfig freemarkerConfig;

    public MailSender() {
        enableLoggingOfSMTPCommands();
    }

    private void enableLoggingOfSMTPCommands() {
        if (javaMailSender instanceof JavaMailSenderImpl) {
            JavaMailSenderImpl impl = (JavaMailSenderImpl) this.javaMailSender;
            if (impl.getSession().getDebug()) {
                impl.getSession().setDebugOut(new PrintStream(new LogOutputStream() {
                    @Override
                    protected void processLine(String line, int level) {
                        log.trace(line);
                        if (StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(line), "QUIT")) {
                            log.trace("**********************************************************************");
                        }
                    }
                }));
            }
        }
    }

    protected String resolveSubject(final PrismNotificationTemplate templateName, final Object... args) {
        String subjectFormat = emailTemplateService.getById(templateName).getVersion().getSubject();
        return args == null ? subjectFormat : String.format(subjectFormat, args);
    }

    public void sendEmail(final PrismEmailMessage... emailMessage) {
        sendEmail(Arrays.asList(emailMessage));
    }

    public void sendEmail(final Collection<PrismEmailMessage> emailMessages) {
        for (PrismEmailMessage message : emailMessages) {
            if (productionEmail) {
                sendEmailAsProductionMessage(message);
            } else {
                sendEmailAsDevelopmentMessage(message);
            }
        }
    }

    protected void sendEmailAsProductionMessage(final PrismEmailMessage message) {
        log.info(String.format("Sending PRODUCTION Email: %s", message.toString()));
        try {
            javaMailSender.send(new MimeMessagePreparator() {
                @Override
                public void prepare(final MimeMessage mimeMessage) throws Exception {
                    final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

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

                    messageHelper.setSubject(message.getSubjectCode());

                    for (PdfAttachmentInputSource attachment : message.getAttachments()) {
                        messageHelper.addAttachment(attachment.getAttachmentFilename(), attachment, "application/pdf");
                    }

                    NotificationTemplate notificationTemplate = emailTemplateService.getById(message.getTemplateName());
                    Template template = new Template(message.getTemplateName().displayValue(),
                            new StringReader(notificationTemplate.getVersion().getContent()), freemarkerConfig.getConfiguration());

                    HtmlToPlainText htmlFormatter = new HtmlToPlainText();
                    String htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(template, message.getModel());
                    String plainText = htmlFormatter.getPlainText(htmlText);
                    plainText = plainText + PLAIN_TEXT_NOTE;

                    messageHelper.setText(plainText, htmlText);
                }
            });
        } catch (Exception e) {
            log.error(String.format("Failed to send email message %s", message.toString()), e);
            throw new PrismMailMessageException(message);
        }
    }

    protected void sendEmailAsDevelopmentMessage(final PrismEmailMessage message) {
        log.info(String.format("Sending DEVELOPMENT Email: %s", message.toString()));
        try {
            javaMailSender.send(new MimeMessagePreparator() {
                @Override
                public void prepare(final MimeMessage mimeMessage) throws Exception {
                    final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

                    messageHelper.setFrom(emailAddressFrom);

                    messageHelper.setTo(emailAddressTo);

                    if (StringUtils.isNotBlank(message.getReplyToAddress())) {
                        messageHelper.setReplyTo(message.getReplyToAddress());
                    }

                    StringBuilder subjectBuilder = new StringBuilder();
                    // the subject should be built anywhere else even with the new editable subject!!
                    subjectBuilder.append(message.getSubjectCode());
                    subjectBuilder.append("<NON-PROD-Message: TO: ").append(message.getToAsInternetAddresses().toString());
                    subjectBuilder.append(" CC: ").append(message.getCcAsInternetAddresses().toString());
                    subjectBuilder.append(" BCC: ").append(message.getBccAsInternetAddresses().toString());

                    messageHelper.setSubject(subjectBuilder.toString());

                    for (PdfAttachmentInputSource attachment : message.getAttachments()) {
                        messageHelper.addAttachment(attachment.getAttachmentFilename(), attachment, "application/pdf");
                    }

                    NotificationTemplate notificationTemplate = emailTemplateService.getById(message.getTemplateName());
                    Template template = new Template(message.getTemplateName().displayValue(),
                            new StringReader(notificationTemplate.getVersion().getContent()), freemarkerConfig.getConfiguration());

                    HtmlToPlainText htmlFormatter = new HtmlToPlainText();
                    String htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(template, message.getModel());
                    String plainText = htmlFormatter.getPlainText(htmlText);
                    plainText = plainText + PLAIN_TEXT_NOTE;

                    messageHelper.setText(plainText, htmlText);
                }
            });
        } catch (Exception e) {
            log.error(String.format("Failed to send email message %s", message.toString()), e);
            throw new PrismMailMessageException(message);
        }
    }

    public NotificationTemplateService getEmailTemplateService() {
        return emailTemplateService;
    }

    public FreeMarkerConfig getFreemarkerConfig() {
        return freemarkerConfig;
    }

}