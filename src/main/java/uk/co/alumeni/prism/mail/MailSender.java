package uk.co.alumeni.prism.mail;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_EMAIL_LINK_MESSAGE;

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

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.google.common.collect.Maps;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionProperty;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.NotificationConfiguration;
import uk.co.alumeni.prism.domain.workflow.NotificationDefinition;
import uk.co.alumeni.prism.dto.MailMessageDTO;
import uk.co.alumeni.prism.dto.NotificationDefinitionDTO;
import uk.co.alumeni.prism.services.SystemService;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;
import uk.co.alumeni.prism.utils.PrismConversionUtils;
import uk.co.alumeni.prism.utils.PrismTemplateUtils;

@Component
@Scope(SCOPE_PROTOTYPE)
public class MailSender {

    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);

    private PropertyLoader propertyLoader;

    @Value("${context.environment}")
    private String contextEnvironment;

    @Value("${email.source}")
    private String emailSource;

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${application.api.url}")
    private String applicationApiUrl;

    @Value("${email.strategy}")
    private String emailStrategy;

    @Inject
    private PrismTemplateUtils prismTemplateUtils;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private SystemService systemService;

    public void sendEmail(final MailMessageDTO messageDTO) {
        NotificationDefinitionDTO notificationDefinitionDTO = messageDTO.getNotificationDefinitionDTO();
        final NotificationConfiguration configuration = messageDTO.getNotificationConfiguration();
        try {
            Map<String, Object> model = createNotificationModel(messageDTO.getNotificationConfiguration().getDefinition(), notificationDefinitionDTO);
            String definitionReference = configuration.getDefinition().getId().name();
            String subject = prismTemplateUtils.getContent(definitionReference + "_subject", configuration.getSubject(), model);
            String content = prismTemplateUtils.getContent(definitionReference + "_content", configuration.getContent(), model);

            String html = getMessage(messageDTO.getNotificationDefinitionDTO().getResource(), subject, content, model);
            String plainText = PrismConversionUtils.htmlToPlainText(html) + "\n\n" + propertyLoader.loadLazy(SYSTEM_EMAIL_LINK_MESSAGE);

            if (emailStrategy.equals("send")) {
                logger.info("Sending Production Email: " + messageDTO.toString());

                Destination destination = new Destination().withToAddresses(new String[] { convertToInternetAddresses(notificationDefinitionDTO.getRecipient()).toString() });
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
            } else if (emailStrategy.equals("log")) {
                logger.info("Sending Development Email: " + messageDTO.toString() + "\n" + subject + "\nContent:\n" + html);
            } else {
                logger.info("Sending Development Email: " + messageDTO.toString());
            }
        } catch (Exception e) {
            logger.error(String.format("Failed to send email %s", messageDTO.toString()), e);
        }

    }

    public MailSender localize(PropertyLoader propertyLoader) {
        this.propertyLoader = propertyLoader;
        return this;
    }

    public String getMessage(Resource resource, String subject, String content, Map<String, Object> model) {
        model.put("IMAGES_PATH", applicationUrl + "/images/email");

        Institution institution = resource.getInstitution();
        if (institution != null) {
            Document institutionLogoImage = institution.getLogoImageEmail();
            if (institutionLogoImage != null) {
                model.put("LOGO_URL", applicationApiUrl + "/images/" + institutionLogoImage.getId());
            }
        }

        model.put("SUBJECT", subject);
        model.put("CONTENT", content);
        return prismTemplateUtils.getContentFromLocation("email_template", "email/email_template.ftl", model);
    }

    private Map<String, Object> createNotificationModel(NotificationDefinition notificationDefinition, NotificationDefinitionDTO notificationDefinitionDTO) {
        Map<String, Object> model = Maps.newHashMap();
        List<PrismNotificationDefinitionPropertyCategory> categories = notificationDefinition.getId().getPropertyCategories();
        NotificationPropertyLoader loader = applicationContext.getBean(NotificationPropertyLoader.class).localize(notificationDefinitionDTO, propertyLoader);
        for (PrismNotificationDefinitionPropertyCategory propertyCategory : categories) {
            for (PrismNotificationDefinitionProperty property : propertyCategory.getProperties()) {
                String propertyValue = loader.load(property);
                model.put(property.name(), property.isEscapeHtml() ? StringEscapeUtils.escapeHtml(propertyValue) : propertyValue);
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
