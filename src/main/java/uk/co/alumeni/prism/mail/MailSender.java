package uk.co.alumeni.prism.mail;

import static com.amazonaws.regions.Regions.EU_WEST_1;
import static com.google.common.collect.Maps.newHashMap;
import static javax.mail.Session.getInstance;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_EMAIL_LINK_MESSAGE;
import static uk.co.alumeni.prism.utils.PrismConversionUtils.htmlToPlainText;
import static uk.co.alumeni.prism.utils.PrismEmailUtils.getMessageData;
import static uk.co.alumeni.prism.utils.PrismEmailUtils.getMessagePart;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.advert.AdvertTarget;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionProperty;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionPropertyCategory;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.NotificationConfiguration;
import uk.co.alumeni.prism.domain.workflow.NotificationDefinition;
import uk.co.alumeni.prism.dto.MailMessageDTO;
import uk.co.alumeni.prism.dto.NotificationDefinitionDTO;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.CommentService;
import uk.co.alumeni.prism.services.DocumentService;
import uk.co.alumeni.prism.services.MessageService;
import uk.co.alumeni.prism.services.NotificationService;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.SystemService;
import uk.co.alumeni.prism.services.UserService;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;
import uk.co.alumeni.prism.utils.PrismTemplateUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;

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
    private AdvertService advertService;

    @Inject
    private CommentService commentService;

    @Inject
    private DocumentService documentService;

    @Inject
    private MessageService messageService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private SystemService systemService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private UserService userService;

    @Inject
    private PrismTemplateUtils prismTemplateUtils;

    @Inject
    private ApplicationContext applicationContext;

    public void sendEmail(MailMessageDTO messageDTO) {
        NotificationDefinitionDTO notificationDefinitionDTO = getNotificationDefinitionDTO(messageDTO);
        NotificationConfiguration notificationConfiguration = getNotificationConfiguration(messageDTO);
        try {
            User recipient = notificationDefinitionDTO.getRecipient();
            Resource resource = notificationDefinitionDTO.getResource();
            NotificationDefinition definition = notificationConfiguration.getDefinition();

            Map<String, Object> model = createNotificationModel(definition, notificationDefinitionDTO);
            String definitionReference = notificationConfiguration.getDefinition().getId().name();
            String subject = prismTemplateUtils.getContent(definitionReference + "_subject", notificationConfiguration.getSubject(), model);
            String content = prismTemplateUtils.getContent(definitionReference + "_content", notificationConfiguration.getContent(), model);

            String htmlContent = getMessage(resource, subject, content, model);
            String plainContent = htmlToPlainText(htmlContent) + "\n\n" + propertyLoader.loadLazy(SYSTEM_EMAIL_LINK_MESSAGE);

            if (emailStrategy.equals("send")) {
                logger.info("Sending Production Email: " + messageDTO.toString());

                AWSCredentials credentials = systemService.getAmazonCredentials();
                AmazonSimpleEmailServiceClient amazonClient = new AmazonSimpleEmailServiceClient(credentials);
                amazonClient.setRegion(Region.getRegion(EU_WEST_1));

                Properties mailSessionProperties = new Properties();
                mailSessionProperties.setProperty("mail.transport.protocol", "aws");
                mailSessionProperties.setProperty("mail.aws.user", credentials.getAWSAccessKeyId());
                mailSessionProperties.setProperty("mail.aws.password", credentials.getAWSSecretKey());
                Session mailSession = getInstance(mailSessionProperties);

                MimeMessage message = new MimeMessage(mailSession);
                message.setFrom(new InternetAddress(emailSource));
                message.setRecipient(Message.RecipientType.TO, convertToInternetAddresses(recipient));
                message.setSubject(subject);

                MimeBodyPart messageBodyPart = new MimeBodyPart();
                MimeMultipart messageBodyParts = new MimeMultipart("alternative");
                messageBodyParts.addBodyPart(getMessagePart(plainContent, "text/plain"));
                messageBodyParts.addBodyPart(getMessagePart(htmlContent, "text/html"));
                messageBodyPart.setContent(messageBodyParts);

                MimeMultipart messageParts = new MimeMultipart("related");
                messageParts.addBodyPart(messageBodyPart);

                for (Document document : messageDTO.getDocumentAttachments()) {
                    messageParts.addBodyPart(getMessagePart(documentService.getDocumentContent(document), document.getContentType(), document.getFileName()));
                }

                message.setContent(messageParts);
                amazonClient.sendRawEmail(new SendRawEmailRequest(new RawMessage(getMessageData(message))));
            } else if (emailStrategy.equals("log")) {
                logger.info("Sending Development Email: " + messageDTO.toString() + "\n" + subject + "\nContent:\n" + htmlContent);
                messageDTO.getDocumentAttachments().forEach(document -> {
                    logger.info("Sending Development Attachment: " + document.getFileName());
                });
            } else {
                logger.info("Sending Development Email: " + messageDTO.toString());
            }

            notificationService.createUserNotification(notificationDefinitionDTO.getResource(), recipient, definition);
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

    public NotificationDefinitionDTO getNotificationDefinitionDTO(MailMessageDTO mailMessageDTO) {
        NotificationDefinitionDTO notificationDefinitionDTO = mailMessageDTO.getNotificationDefinitionDTO();
        notificationDefinitionDTO.setInitiator(getUser(notificationDefinitionDTO.getInitiator()));
        notificationDefinitionDTO.setRecipient(getUser(notificationDefinitionDTO.getRecipient()));
        notificationDefinitionDTO.setSignatory(getUser(notificationDefinitionDTO.getSignatory()));
        notificationDefinitionDTO.setCandidate(getUser(notificationDefinitionDTO.getCandidate()));
        notificationDefinitionDTO.setResource(getResource(notificationDefinitionDTO.getResource()));

        Comment comment = notificationDefinitionDTO.getComment();
        notificationDefinitionDTO.setComment(comment == null ? null : commentService.getById(comment.getId()));

        uk.co.alumeni.prism.domain.message.Message message = notificationDefinitionDTO.getMessage();
        notificationDefinitionDTO.setMessage(message == null ? null : messageService.getMessageById(message.getId()));

        AdvertTarget advertTarget = notificationDefinitionDTO.getAdvertTarget();
        notificationDefinitionDTO.setAdvertTarget(advertTarget == null ? null : advertService.getAdvertTargetById(advertTarget.getId()));

        notificationDefinitionDTO.setInvitedResource((ResourceParent) getResource(notificationDefinitionDTO.getInvitedResource()));
        return notificationDefinitionDTO;
    }

    private User getUser(User initiator) {
        return initiator == null ? null : userService.getById(initiator.getId());
    }

    private Resource getResource(Resource resource) {
        return resource == null ? null : resourceService.getById(resource.getResourceScope(), resource.getId());
    }

    private NotificationConfiguration getNotificationConfiguration(MailMessageDTO mailMessageDTO) {
        NotificationConfiguration notificationConfiguration = mailMessageDTO.getNotificationConfiguration();
        return notificationService.getNotificationConfigurationById(notificationConfiguration.getId());
    }

    private Map<String, Object> createNotificationModel(NotificationDefinition notificationDefinition, NotificationDefinitionDTO notificationDefinitionDTO) {
        Map<String, Object> model = newHashMap();
        Set<PrismNotificationDefinitionPropertyCategory> categories = notificationDefinition.getId().getPropertyCategories();
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
