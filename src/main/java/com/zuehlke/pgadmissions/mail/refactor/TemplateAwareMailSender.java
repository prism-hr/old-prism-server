package com.zuehlke.pgadmissions.mail.refactor;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.mail.HtmlToPlainText;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;
import com.zuehlke.pgadmissions.services.EmailTemplateService;

import freemarker.template.Template;

@Service
public class TemplateAwareMailSender extends AbstractMailSender {

    private static final String PLAIN_TEXT_NOTE = "\n\nIf the links do not work in your email client copy and paste them into your browser.";

    private final Logger log = LoggerFactory.getLogger(TemplateAwareMailSender.class);
    
    private final EmailTemplateService emailTemplateService;
    
    private final FreeMarkerConfig freemarkerConfig;
    
    public TemplateAwareMailSender() {
        this(null, null, null, null, null, null, null);
    }
    
    @Autowired
    public TemplateAwareMailSender(
            final JavaMailSender mailSender, 
            final MessageSource messageSource, 
            final EmailTemplateService emailTemplateService,
            final FreeMarkerConfig freemarkerConfig,
            @Value("${email.prod}") final String production,
            @Value("${email.address.from}") final String emailAddressFrom,  
            @Value("${email.address.to}") final String emailAddressTo) {
        super(mailSender, messageSource, production, emailAddressFrom, emailAddressTo);
        this.emailTemplateService = emailTemplateService;
        this.freemarkerConfig = freemarkerConfig;
    }
    
    @Override
    public void sendEmail(final PrismEmailMessage emailMessage) {
        sendEmail(Arrays.asList(emailMessage));
    }

    @Override
    public void sendEmail(final Collection<PrismEmailMessage> emailMessages) {
        for (PrismEmailMessage message : emailMessages) {
            if (emailProductionSwitch) {
                sendEmailAsProductionMessage(message);
            } else {
                sendEmailAsDevelopmentMessage(message);
            }
        }
    }
    
    private void sendEmailAsProductionMessage(final PrismEmailMessage message) {
        log.info(String.format("Sending PRODUCTION Email: %s", message.toString()));
        javaMailSender.send(new MimeMessagePreparator() {
            @Override
            public void prepare(final MimeMessage mimeMessage) throws Exception {
                final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                
                CollectionUtils.forAllDo(message.getToAsInternetAddresses(), new Closure() {
                    @Override
                    public void execute(final Object input) {
                        try {
                            messageHelper.addTo((InternetAddress) input);
                        } catch (MessagingException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                });
                
                CollectionUtils.forAllDo(message.getCcAsInternetAddresses(), new Closure() {
                    @Override
                    public void execute(final Object input) {
                        try {
                            messageHelper.addCc((InternetAddress) input);
                        } catch (MessagingException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                });
                
                CollectionUtils.forAllDo(message.getBccAsInternetAddresses(), new Closure() {
                    @Override
                    public void execute(final Object input) {
                        try {
                            messageHelper.addBcc((InternetAddress) input);
                        } catch (MessagingException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                });
                
                if (StringUtils.isNotBlank(message.getReplyToAddress())) {
                    messageHelper.setReplyTo(message.getReplyToAddress());
                }
                
                messageHelper.setFrom(message.getFromAddress());
                
                messageHelper.setSubject(String.format(message.getSubjectCode(), message.getSubjectArgs().toArray(new Object[]{})));
                
                for (PdfAttachmentInputSource attachment : message.getAttachments()) {
                    messageHelper.addAttachment(attachment.getAttachmentFilename(), attachment, "application/pdf");
                }

                EmailTemplate activeEmailTemplate = emailTemplateService.getActiveEmailTemplate(message.getTemplateName());
                Template template = new Template(message.getTemplateName().displayValue(), new StringReader(activeEmailTemplate.getContent()), freemarkerConfig.getConfiguration());
                
                HtmlToPlainText htmlFormatter = new HtmlToPlainText();
                String htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(template, message.getModel());
                String plainText = htmlFormatter.getPlainText(htmlText);
                plainText = plainText + PLAIN_TEXT_NOTE;
                
                messageHelper.setText(plainText, htmlText);
            }
        });
    }
    
    private void sendEmailAsDevelopmentMessage(final PrismEmailMessage message) {
        log.info(String.format("Sending DEVELOPMENT Email: %s", message.toString()));
        javaMailSender.send(new MimeMessagePreparator() {
            @Override
            public void prepare(final MimeMessage mimeMessage) throws Exception {
                final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                
                messageHelper.setTo(emailAddressTo);
                messageHelper.setFrom(emailAddressFrom);
                
                if (StringUtils.isNotBlank(message.getReplyToAddress())) {
                    messageHelper.setReplyTo(message.getReplyToAddress());
                }
                
                StringBuilder subjectBuilder = new StringBuilder();
                subjectBuilder.append(String.format(message.getSubjectCode(), message.getSubjectArgs().toArray(new Object[]{})));
                subjectBuilder.append("<NON-PROD-Message: TO: ").append(message.getToAsInternetAddresses().toString());
                subjectBuilder.append(" CC: ").append(message.getCcAsInternetAddresses().toString());
                subjectBuilder.append(" BCC: ").append(message.getBccAsInternetAddresses().toString());
                
                messageHelper.setSubject(subjectBuilder.toString());
                
                for (PdfAttachmentInputSource attachment : message.getAttachments()) {
                    messageHelper.addAttachment(attachment.getAttachmentFilename(), attachment, "application/pdf");
                }

                EmailTemplate activeEmailTemplate = emailTemplateService.getActiveEmailTemplate(message.getTemplateName());
                Template template = new Template(message.getTemplateName().displayValue(), new StringReader(activeEmailTemplate.getContent()), freemarkerConfig.getConfiguration());
                
                HtmlToPlainText htmlFormatter = new HtmlToPlainText();
                String htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(template, message.getModel());
                String plainText = htmlFormatter.getPlainText(htmlText);
                plainText = plainText + PLAIN_TEXT_NOTE;
                
                messageHelper.setText(plainText, htmlText);
            }
        });
    }
}
