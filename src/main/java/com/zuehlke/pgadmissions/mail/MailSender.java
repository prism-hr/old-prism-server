package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_EMAIL_LINK_MESSAGE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_HELPDESK_REPORT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_NOTIFICATION_TEMPLATE_PROPERTY_ERROR;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplateProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.NotificationTemplate;
import com.zuehlke.pgadmissions.dto.MailMessageDTO;
import com.zuehlke.pgadmissions.dto.NotificationTemplateModelDTO;
import com.zuehlke.pgadmissions.services.builders.pdf.mail.AttachmentInputSource;
import com.zuehlke.pgadmissions.services.helpers.NotificationTemplatePropertyLoader;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class MailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailSender.class);

    @Value("${application.host}")
    private String host;

    @Value("${context.environment}")
    private String contextEnvironment;

    @Value("${email.address.from}")
    private String emailAddressFrom;

    @Value("${email.address.to}")
    private String emailAddressTo;
    
    @Value("${email.location}")
    private String emailTemplateLocation;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private FreeMarkerConfig freemarkerConfig;

    @Autowired
    private MailToPlainTextConverter mailToPlainTextConverter;

    @Autowired
    private ApplicationContext applicationContext;

    public void sendEmail(final MailMessageDTO message) {
        final NotificationConfiguration configuration = message.getConfiguration();
        try {
            Map<String, Object> model = createNotificationModel(message.getConfiguration().getNotificationTemplate(), message.getModelDTO());
            final String subject = processHeader(configuration.getNotificationTemplate().getId(), configuration.getSubject(), model);

            final String htmlContent = processContent(configuration.getNotificationTemplate().getId(), configuration.getContent(), model, subject);
            final String plainTextContent = mailToPlainTextConverter.getPlainText(htmlContent) + "\n\n"
                    + applicationContext.getBean(PropertyLoader.class).load(SYSTEM_EMAIL_LINK_MESSAGE);

            if (contextEnvironment.equals("prod") || contextEnvironment.equals("uat")) {
                LOGGER.info("Sending Production Email: " + message.toString());
                javaMailSender.send(new MimeMessagePreparator() {
                    @Override
                    public void prepare(final MimeMessage mimeMessage) throws Exception {
                        final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                        messageHelper.setTo(convertToInternetAddresses(message.getModelDTO().getUser()));
                        messageHelper.setSubject(subject);
                        messageHelper.setText(plainTextContent, htmlContent);
                        messageHelper.setFrom(emailAddressFrom);
                        for (AttachmentInputSource attachment : message.getAttachments()) {
                            messageHelper.addAttachment(attachment.getAttachmentFilename(), attachment, "application/pdf");
                        }
                    }
                });
            } else if (contextEnvironment.equals("dev")) {
                LOGGER.info("Sending Development Email: " + message.toString() + "\nSubject: " + subject + "\nContent:\n" + htmlContent);
            } else {
                LOGGER.info("Sending Development Email: " + message.toString());
            }
        } catch (Exception e) {
            if (configuration.getNotificationTemplate().getNotificationType() == PrismNotificationType.INDIVIDUAL) {
                throw new Error(e);
            } else {
                LOGGER.error(String.format("Failed to send email %s", message.toString()), e);
            }
        }

    }

    public String processHeader(PrismNotificationTemplate templateId, String templateValue, Map<String, Object> model) throws IOException, TemplateException {
        Template template = new Template(templateId.name(), new StringReader(templateValue), freemarkerConfig.getConfiguration());
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    public String processContent(PrismNotificationTemplate templateId, String templateValue, Map<String, Object> model, String subject) throws IOException,
            TemplateException {
        Template template = new Template(templateId.name(), new StringReader(templateValue), freemarkerConfig.getConfiguration());
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

        String emailTemplate = Resources.toString(Resources.getResource(emailTemplateLocation), Charsets.UTF_8);
        template = new Template("Email template", emailTemplate, freemarkerConfig.getConfiguration());

        model = ImmutableMap.<String, Object> of("IMAGES_PATH", host + "/images/email", "SUBJECT", subject, "CONTENT", content);
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    public Map<String, Object> createNotificationModel(NotificationTemplate notificationTemplate, NotificationTemplateModelDTO modelDTO) {
        Map<String, Object> model = Maps.newHashMap();
        List<PrismNotificationTemplatePropertyCategory> categories = notificationTemplate.getId().getPropertyCategories();
        NotificationTemplatePropertyLoader loader = applicationContext.getBean(NotificationTemplatePropertyLoader.class).withTemplateModelDTO(modelDTO);
        for (PrismNotificationTemplatePropertyCategory propertyCategory : categories) {
            for (PrismNotificationTemplateProperty property : propertyCategory.getProperties()) {
                String value = (String) ReflectionUtils.invokeMethod(loader, property.getMethodName());
                boolean valueNull = value == null;
                if (valueNull) {
                    PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).withResource(modelDTO.getResource());
                    value = "[" + propertyLoader.load(SYSTEM_NOTIFICATION_TEMPLATE_PROPERTY_ERROR) + ". " + propertyLoader.load(SYSTEM_HELPDESK_REPORT) + ": "
                            + modelDTO.getResource().getSystem().getHelpdesk() + "]";
                }
                model.put(property.name(), !property.isEscapeHtml() || valueNull ? value : escapeHtml(value));
            }
        }
        return model;
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
