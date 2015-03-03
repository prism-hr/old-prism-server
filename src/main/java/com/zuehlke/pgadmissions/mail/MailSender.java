package com.zuehlke.pgadmissions.mail;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_EMAIL_LINK_MESSAGE;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.dto.MailMessageDTO;
import com.zuehlke.pgadmissions.dto.NotificationDefinitionModelDTO;
import com.zuehlke.pgadmissions.exceptions.AbortMailSendException;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

import freemarker.template.Template;
import freemarker.template.TemplateException;

@Component
@Scope(SCOPE_PROTOTYPE)
public class MailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailSender.class);

    private PropertyLoader propertyLoader;

    @Value("${context.environment}")
    private String contextEnvironment;

    @Value("${email.source}")
    private String emailSource;

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${application.api.url}")
    private String applicationApiUrl;

    @Inject
    private FreeMarkerConfig freemarkerConfig;

    @Inject
    private MailToPlainTextConverter mailToPlainTextConverter;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private SystemService systemService;

    public void sendEmail(final MailMessageDTO messageDTO) {
        NotificationDefinitionModelDTO modelDTO = messageDTO.getModelDTO();
        final NotificationConfiguration configuration = messageDTO.getConfiguration();
        try {
            Map<String, Object> model = createNotificationModel(messageDTO.getConfiguration().getNotificationDefinition(), modelDTO);
            final String subject = processHeader(configuration.getNotificationDefinition().getId(), configuration.getSubject(), model);

            Institution institution = modelDTO.getResource().getInstitution();
            Document logoDocument = institution != null ? institution.getLogoDocument() : null;
            final String html = processContent(configuration.getNotificationDefinition().getId(), configuration.getContent(), model, subject,
                    logoDocument);
            final String plainText = mailToPlainTextConverter.getPlainText(html) + "\n\n" + propertyLoader.load(SYSTEM_EMAIL_LINK_MESSAGE);

            if (contextEnvironment.equals("prod") || contextEnvironment.equals("uat") || messageDTO.getModelDTO().getUser().getEmail().contains("fibinger")) {
                LOGGER.info("Sending Production Email: " + messageDTO.toString());

                Destination destination = new Destination().withToAddresses(new String[]{convertToInternetAddresses(modelDTO.getUser()).toString()});
                Content subjectContent = new Content().withData(subject);
                Content plainTextContent = new Content().withData(plainText);
                Content htmlContent = new Content().withData(html);
                Body body = new Body().withText(plainTextContent).withHtml(htmlContent);
                Message message = new Message().withSubject(subjectContent).withBody(body);
                SendEmailRequest request = new SendEmailRequest().withSource(emailSource).withDestination(destination)
                        .withMessage(message);

                AWSCredentials credentials = systemService.getAmazonCredentials();
                AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(credentials);
                Region REGION = Region.getRegion(Regions.EU_WEST_1);
                client.setRegion(REGION);
                client.sendEmail(request);
            } else if (contextEnvironment.equals("dev")) {
                LOGGER.info("Sending Development Email: " + messageDTO.toString() + "\nSubject: " + subject + "\nContent:\n" + html);
            } else {
                LOGGER.info("Sending Development Email: " + messageDTO.toString());
            }
        } catch (Exception e) {
            LOGGER.error(String.format("Failed to send email %s", messageDTO.toString()), e);
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

    public String processContent(PrismNotificationDefinition templateId, String templateValue, Map<String, Object> model, String subject, Document logoDocument)
            throws IOException, TemplateException {
        Template template = new Template(templateId.name(), new StringReader(templateValue), freemarkerConfig.getConfiguration());
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

        String emailTemplate = Resources.toString(Resources.getResource("email/email_template.ftl"), Charsets.UTF_8);
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

    public Map<String, Object> createNotificationModel(NotificationDefinition notificationTemplate, NotificationDefinitionModelDTO modelDTO)
            throws AbortMailSendException {
        return createNotificationModel(notificationTemplate, modelDTO, false);
    }

    private Map<String, Object> createNotificationModel(NotificationDefinition notificationTemplate, NotificationDefinitionModelDTO modelDTO,
                                                        boolean validationMode) throws AbortMailSendException {
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
