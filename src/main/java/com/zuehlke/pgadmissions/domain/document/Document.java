package com.zuehlke.pgadmissions.domain.document;

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

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.application.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.application.ApplicationFunding;
import com.zuehlke.pgadmissions.domain.application.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccount;
import com.zuehlke.pgadmissions.utils.PrismConstants;

@Entity
@Table(name = "document")
public class Document {

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
    private ApplicationLanguageQualification applicationLanguageQualification;

    @OneToOne(mappedBy = "document")
    private ApplicationQualification applicationQualification;

    @OneToOne(mappedBy = "document")
    private ApplicationFunding applicationFunding;

    @OneToOne(mappedBy = "personalStatement")
    private ApplicationDocument applicationPersonalStatement;

    @OneToOne(mappedBy = "researchStatement")
    private ApplicationDocument applicationResearchStatement;

    @OneToOne(mappedBy = "coveringLetter")
    private ApplicationDocument applicationCoveringLetter;

    @OneToOne(mappedBy = "cv")
    private ApplicationDocument applicationCv;

    @OneToOne(mappedBy = "portraitImage")
    private UserAccount portraitImage;

    @OneToOne(mappedBy = "logoImage")
    private Institution logoImage;

    @OneToOne(mappedBy = "backgroundImage")
    private Advert backgroundImage;

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

    public ApplicationLanguageQualification getApplicationLanguageQualification() {
        return applicationLanguageQualification;
    }

    public ApplicationQualification getApplicationQualification() {
        return applicationQualification;
    }

    public ApplicationFunding getApplicationFunding() {
        return applicationFunding;
    }

    public ApplicationDocument getApplicationPersonalStatement() {
        return applicationPersonalStatement;
    }

    public ApplicationDocument getApplicationResearchStatement() {
        return applicationResearchStatement;
    }

    public ApplicationDocument getApplicationCv() {
        return applicationCv;
    }

    public ApplicationDocument getApplicationCoveringLetter() {
        return applicationCoveringLetter;
    }

    public UserAccount getPortraitImage() {
        return portraitImage;
    }

    public Institution getLogoImage() {
        return logoImage;
    }

    public Advert getBackgroundImage() {
        return backgroundImage;
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
        } else if (applicationLanguageQualification != null) {
            return applicationLanguageQualification.getPersonalDetail().getApplication();
        } else if (applicationQualification != null) {
            return applicationQualification.getApplication();
        } else if (applicationFunding != null) {
            return applicationFunding.getApplication();
        } else if (applicationPersonalStatement != null) {
            return applicationPersonalStatement.getApplication();
        } else if (applicationResearchStatement != null) {
            return applicationResearchStatement.getApplication();
        } else if (applicationCoveringLetter != null) {
            return applicationCoveringLetter.getApplication();
        } else if (applicationCv != null) {
            return applicationCv.getApplication();
        } else {
            return null;
        }
    }

    public String getExportFilenameAmazon() {
        return id.toString();
    }

    public String getExportFilenameSits() {
        return getExportFilenameAmazon() + PrismConstants.DOT + PrismConstants.FILE_EXTENSION_PDF;
    }

}
