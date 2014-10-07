package com.zuehlke.pgadmissions.mail;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplateProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.dto.MailMessageDTO;
import com.zuehlke.pgadmissions.dto.NotificationTemplateModelDTO;
import com.zuehlke.pgadmissions.services.NotificationTemplatePropertyService;
import com.zuehlke.pgadmissions.services.builders.pdf.mail.AttachmentInputSource;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
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
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private NotificationTemplatePropertyService notificationTemplatePropertyService;

    public void sendEmail(final MailMessageDTO message) {
        final NotificationConfiguration configuration = message.getConfiguration();
        try {
            Map<String, Object> model = createNotificationModel(message.getConfiguration().getNotificationTemplate(), message.getModelDTO());
            final String subject = processTemplate(configuration.getNotificationTemplate().getId(), configuration.getSubject(), model);
            final String htmlText = processTemplate(configuration.getNotificationTemplate().getId(), configuration.getContent(), model);
            final String plainText = mailToPlainTextConverter.getPlainText(htmlText) + "\n\n" + emailBrokenLinkMessage;

            if (contextEnvironment.equals("prod") || contextEnvironment.equals("uat")) {
                logger.info(String.format("Sending Production Email: %s", message.toString()));
                javaMailSender.send(new MimeMessagePreparator() {
                    @Override
                    public void prepare(final MimeMessage mimeMessage) throws Exception {
                        final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                        messageHelper.setTo(convertToInternetAddresses(message.getModelDTO().getUser()));
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
            if (configuration.getNotificationTemplate().getNotificationType() == PrismNotificationType.INDIVIDUAL) {
                throw new Error(e);
            } else {
                logger.error(String.format("Failed to send email %s", message.toString()), e);
            }
        }

    }

    public String processTemplate(PrismNotificationTemplate templateId, String templateValue, Map<String, Object> model) throws IOException, TemplateException {
        Template template = new Template(templateId.name(), new StringReader(templateValue), freemarkerConfig.getConfiguration());
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    public Map<String, Object> createNotificationModel(NotificationTemplate notificationTemplate, NotificationTemplateModelDTO modelDTO) {
        Map<String, Object> model = Maps.newHashMap();
        List<PrismNotificationTemplatePropertyCategory> categories = Lists.asList(PrismNotificationTemplatePropertyCategory.GLOBAL, notificationTemplate.getId().getPropertyCategories());
        for (PrismNotificationTemplatePropertyCategory propertyCategory : categories) {
            for (PrismNotificationTemplateProperty property : propertyCategory.getProperties()) {
                List<Object> arguments = Lists.newLinkedList();
                arguments.add(modelDTO);
                if (property.getMethodArguments().length > 0) {
                    arguments.add(property.getMethodArguments());
                }
                Object value = ReflectionUtils.invokeMethod(notificationTemplatePropertyService, property.getGetterMethod(), arguments.toArray());
                model.put(property.name(), value);
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
