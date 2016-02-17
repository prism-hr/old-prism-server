package uk.co.alumeni.prism.domain.document;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.application.ApplicationDocument;
import uk.co.alumeni.prism.domain.application.ApplicationQualification;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.message.MessageDocument;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAccount;
import uk.co.alumeni.prism.domain.user.UserAssignment;
import uk.co.alumeni.prism.domain.user.UserDocument;
import uk.co.alumeni.prism.domain.user.UserQualification;
import uk.co.alumeni.prism.domain.workflow.NotificationConfigurationDocument;
import uk.co.alumeni.prism.workflow.user.DocumentReassignmentProcessor;

@Entity
@Table(name = "document")
public class Document implements UniqueEntity, UserAssignment<DocumentReassignmentProcessor> {

    @Id
    @GeneratedValue
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private PrismFileCategory category;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Lob
    @Column(name = "file_content")
    @Type(type = "binary")
    private byte[] content;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "exported", nullable = false)
    private Boolean exported;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;

    @OneToOne
    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    private Comment comment;

    @OneToOne(mappedBy = "document")
    private ApplicationQualification applicationQualification;

    @OneToOne(mappedBy = "cv")
    private ApplicationDocument applicationCv;

    @OneToOne(mappedBy = "coveringLetter")
    private ApplicationDocument applicationCoveringLetter;

    @OneToOne(mappedBy = "document")
    private UserQualification userQualification;

    @OneToOne(mappedBy = "cv")
    private UserDocument userCv;

    @OneToOne(mappedBy = "portraitImage")
    private UserAccount portraitImage;

    @OneToOne(mappedBy = "logoImage")
    private Institution logoImage;

    @OneToOne(mappedBy = "logoImageEmail")
    private Institution logoImageEmail;

    @OneToOne(mappedBy = "backgroundImage")
    private Advert backgroundImage;

    @OneToOne(mappedBy = "document")
    private NotificationConfigurationDocument notificationConfigurationDocument;

    @OneToOne(mappedBy = "document")
    private MessageDocument messageDocument;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public final PrismFileCategory getCategory() {
        return category;
    }

    public final void setCategory(PrismFileCategory category) {
        this.category = category;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public final Boolean getExported() {
        return exported;
    }

    public final void setExported(Boolean exported) {
        this.exported = exported;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Comment getComment() {
        return comment;
    }

    public ApplicationQualification getApplicationQualification() {
        return applicationQualification;
    }

    public ApplicationDocument getApplicationCv() {
        return applicationCv;
    }

    public ApplicationDocument getApplicationCoveringLetter() {
        return applicationCoveringLetter;
    }

    public UserQualification getUserQualification() {
        return userQualification;
    }

    public UserDocument getUserCv() {
        return userCv;
    }

    public UserAccount getPortraitImage() {
        return portraitImage;
    }

    public Institution getLogoImage() {
        return logoImage;
    }

    public Institution getLogoImageEmail() {
        return logoImageEmail;
    }

    public Advert getBackgroundImage() {
        return backgroundImage;
    }

    public NotificationConfigurationDocument getNotificationConfigurationDocument() {
        return notificationConfigurationDocument;
    }

    public MessageDocument getMessageDocument() {
        return messageDocument;
    }

    public Document withId(Integer id) {
        this.id = id;
        return this;
    }

    public Document withCategory(final PrismFileCategory category) {
        this.category = category;
        return this;
    }

    public Document withFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public Document withContent(byte[] content) {
        this.content = content;
        return this;
    }

    public Document withContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Document withExported(Boolean exported) {
        this.exported = exported;
        return this;
    }

    public Document withUser(User user) {
        this.user = user;
        return this;
    }

    public Document withCreatedTimestamp(DateTime dateTime) {
        this.createdTimestamp = dateTime;
        return this;
    }

    public Resource getResource() {
        if (comment != null) {
            return comment.getResource();
        } else if (applicationQualification != null) {
            return applicationQualification.getAssociation();
        } else if (applicationCv != null) {
            return applicationCv.getAssociation();
        } else if (applicationCoveringLetter != null) {
            return applicationCoveringLetter.getAssociation();
        }
        return null;
    }

    public String getExportFilenameAmazon() {
        return id.toString();
    }

    @Override
    public Class<DocumentReassignmentProcessor> getUserReassignmentProcessor() {
        return DocumentReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        if (comment == null) {
            return false;
        }

        Resource resource = getResource();
        resource = resource == null ? comment.getResource() : resource;

        if (resource == null) {
            return false;
        }

        return resource.getUser().equals(user);
    }

    @Override
    public EntitySignature getEntitySignature() {
        return null;
    }

}
