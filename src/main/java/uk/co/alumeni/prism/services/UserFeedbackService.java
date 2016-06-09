package uk.co.alumeni.prism.services;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.alumeni.prism.dao.UserFeedbackDAO;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserFeedback;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.rest.dto.user.UserContactDTO;
import uk.co.alumeni.prism.rest.dto.user.UserFeedbackContentDTO;
import uk.co.alumeni.prism.rest.dto.user.UserFeedbackDTO;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;

@Service
@Transactional
public class UserFeedbackService {

    @Inject
    private UserFeedbackDAO userFeedbackDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private EntityService entityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private SystemService systemService;

    @Value("${email.source}")
    private String emailSource;

    @Value("${system.user.email}")
    private String systemUserEmail;

    @Value("${application.url}")
    private String applicationUrl;

    public void createFeedback(UserFeedbackDTO userFeedbackDTO) {
        Resource resource = resourceService.getById(userFeedbackDTO.getResourceScope().getResourceClass(), userFeedbackDTO.getResourceId());
        Action action = actionService.getById(userFeedbackDTO.getAction());
        User user = userService.getCurrentUser();

        UserFeedbackContentDTO contentDTO = userFeedbackDTO.getContent();
        boolean declined = contentDTO == null;
        UserFeedback userFeedback = new UserFeedback().withResource(resource).withUser(user).withRoleCategory(userFeedbackDTO.getRoleCategory())
                .withAction(action).withDeclinedResponse(declined).withCreatedTimestamp(new DateTime());

        if (!declined) {
            userFeedback.setRating(contentDTO.getRating());
            userFeedback.setContent(contentDTO.getContent());
            userFeedback.setFeatureRequest(contentDTO.getFeatureRequest());
        }

        entityService.save(userFeedback);
        setLastSequenceIdentifier(userFeedback);
    }

    public List<UserFeedback> getUserFeedback(Integer ratingThreshold, String lastSequenceIdentifier) {
        return userFeedbackDAO.getUserFeedback(ratingThreshold, lastSequenceIdentifier);
    }

    public PrismRoleCategory getRequiredFeedbackRoleCategory(User user) {
        DateTime latestFeedbackTimestamp = userFeedbackDAO.getLatestUserFeedbackTimestamp(user);
        if (latestFeedbackTimestamp == null || latestFeedbackTimestamp.isBefore(new DateTime().minusYears(1))) {
            for (PrismRoleCategory prismRoleCategory : PrismRoleCategory.values()) {
                if (!roleService.getUserRolesByRoleCategory(user, prismRoleCategory, SYSTEM).isEmpty()) {
                    return prismRoleCategory;
                }
            }
        }
        return null;
    }

    public void postContactMessage(UserContactDTO userContactDTO) {

//        HttpClient captchaClient = HttpClientBuilder.create().build();
//        try {
//            HttpPost httpPost = new HttpPost("https://www.google.com/recaptcha/api/siteverify");
//            List<NameValuePair> captchaParameters = Arrays.asList(
//                    new BasicNameValuePair("secret", "6LeUOSITAAAAAB3I1E80w-w5x-Ic2hvClIX8Zu4v"),
//                    new BasicNameValuePair("response", userContactDTO.getRecaptchaResponse()));
//            httpPost.setEntity(new UrlEncodedFormEntity(captchaParameters));
//            InputStream captchaResponse = captchaClient.execute(httpPost).getEntity().getContent();
//            new Gson().fromJson(Streams.asString(captchaResponse), Map.class);
//        } catch (IOException e) {
//            throw new Error("Could not connect to Google");
//        }

        AWSCredentials credentials = systemService.getAmazonCredentials();
        AmazonSimpleEmailServiceClient amazonClient = new AmazonSimpleEmailServiceClient(credentials);
        amazonClient.setRegion(Region.getRegion(Regions.EU_WEST_1));

        String body = "Source: " + applicationUrl + "\n"
                + "From: " + userContactDTO.getName() + "\n"
                + "Email: " + userContactDTO.getEmail() + "\n"
                + "Phone: " + MoreObjects.firstNonNull(Strings.emptyToNull(userContactDTO.getPhone()), "Not specified") + "\n"
                + "Subject: " + userContactDTO.getTitle() + "\n\n"
                + "Content:\n" + userContactDTO.getContent();

        Content subject = new Content("Prism Message - " + userContactDTO.getTitle());
        Message message = new Message(subject, new Body(new Content(body)));
        String recipient = systemUserEmail.replace("@", "+contact@");
        Destination destination = new Destination(Collections.singletonList(recipient));
        SendEmailRequest request = new SendEmailRequest(emailSource, destination, message);

        amazonClient.sendEmail(request);
    }

    private void setLastSequenceIdentifier(UserFeedback userFeedback) {
        userFeedback.setSequenceIdentifier(Long.toString(new DateTime().getMillis()) + String.format("%010d", userFeedback.getId()));
    }

}
