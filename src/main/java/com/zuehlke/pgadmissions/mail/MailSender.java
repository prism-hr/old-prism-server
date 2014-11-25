package com.zuehlke.pgadmissions.mail;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.dto.MailMessageDTO;
import com.zuehlke.pgadmissions.dto.NotificationDefinitionModelDTO;
import com.zuehlke.pgadmissions.services.builders.pdf.mail.AttachmentInputSource;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_EMAIL_LINK_MESSAGE;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class MailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailSender.class);

    private PropertyLoader propertyLoader;

    @Value("${context.environment}")
    private String contextEnvironment;

    @Value("${email.address.from}")
    private String emailAddressFrom;

    @Value("${email.address.to}")
    private String emailAddressTo;

    @Value("${email.location}")
    private String emailTemplateLocation;

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${application.api.url}")
    private String applicationApiUrl;

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
            Map<String, Object> model = createNotificationModel(message.getConfiguration().getNotificationDefinition(), message.getModelDTO());
            final String subject = processHeader(configuration.getNotificationDefinition().getId(), configuration.getSubject(), model);

            Institution institution = message.getModelDTO().getResource().getInstitution();
            Document logoDocument = institution != null ? institution.getLogoDocument() : null;
            final String htmlContent = processContent(configuration.getNotificationDefinition().getId(), configuration.getContent(), model, subject, logoDocument);
            final String plainTextContent = mailToPlainTextConverter.getPlainText(htmlContent) + "\n\n" + propertyLoader.load(SYSTEM_EMAIL_LINK_MESSAGE);

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
            if (configuration.getNotificationDefinition().getNotificationType() == PrismNotificationType.INDIVIDUAL) {
                throw new Error(e);
            } else {
                LOGGER.error(String.format("Failed to send email %s", message.toString()), e);
            }
        }

    }

    public MailSender localize(PropertyLoader propertyLoader) {
        this.propertyLoader = propertyLoader;
        return this;
    }

    public String processHeader(PrismNotificationDefinition templateId, String templateValue, Map<String, Object> model) throws IOException, TemplateException {
        Template template = new Template(templateId.name(), new StringReader(templateValue), freemarkerConfig.getConfiguration());
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    public String processContent(PrismNotificationDefinition templateId, String templateValue, Map<String, Object> model, String subject, Document logoDocument) throws IOException,
            TemplateException {
        Template template = new Template(templateId.name(), new StringReader(templateValue), freemarkerConfig.getConfiguration());
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

        String emailTemplate = Resources.toString(Resources.getResource(emailTemplateLocation), Charsets.UTF_8);
        template = new Template("Email template", emailTemplate, freemarkerConfig.getConfiguration());

        String imagesPath = applicationUrl + "/images/email";
        String logoUrl;
        if (logoDocument != null) {
            logoUrl = applicationApiUrl + "/images/" + logoDocument.getId();
        } else {
            logoUrl = imagesPath + "/prism.png";
        }

        model = ImmutableMap.<String, Object>of("LOGO_URL", logoUrl, "IMAGES_PATH", imagesPath, "SUBJECT", subject, "CONTENT", content);
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    public Map<String, Object> createNotificationModelForValidation(NotificationDefinition notificationTemplate) {
        return createNotificationModel(notificationTemplate, new NotificationDefinitionModelDTO(), true);
    }

    public Map<String, Object> createNotificationModel(NotificationDefinition notificationTemplate, NotificationDefinitionModelDTO modelDTO) {
        return createNotificationModel(notificationTemplate, modelDTO, false);
    }

    private Map<String, Object> createNotificationModel(NotificationDefinition notificationTemplate, NotificationDefinitionModelDTO modelDTO, boolean validationMode) {
        Map<String, Object> model = Maps.newHashMap();
        List<PrismNotificationDefinitionPropertyCategory> categories = notificationTemplate.getId().getPropertyCategories();
        NotificationPropertyLoader loader = applicationContext.getBean(NotificationPropertyLoader.class).localize(modelDTO, propertyLoader);
        for (PrismNotificationDefinitionPropertyCategory propertyCategory : categories) {
            for (PrismNotificationDefinitionProperty property : propertyCategory.getProperties()) {
                String value = validationMode ? "placeholder" : loader.load(property);
                model.put(property.name(), property.isEscapeHtml() ? StringEscapeUtils.escapeHtml(value) : value);
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
