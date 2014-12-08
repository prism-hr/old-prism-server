package com.zuehlke.pgadmissions.domain.document;

import com.zuehlke.pgadmissions.domain.application.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.application.ApplicationFunding;
import com.zuehlke.pgadmissions.domain.application.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import org.bouncycastle.util.Arrays;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Entity
@Table(name = "DOCUMENT")
public class Document {

    @Id
    @GeneratedValue
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private FileCategory category;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Lob
    @Column(name = "file_content", nullable = false)
    @Type(type = "binary")
    private byte[] content;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    private Comment comment;

    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;

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

    @OneToOne(mappedBy = "portraitDocument")
    private User userPortrait;

    @OneToOne(mappedBy = "logoDocument")
    private Institution institutionLogo;

    @Transient
    private MultipartFile fileData;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
    
    public final FileCategory getCategory() {
        return category;
    }

    public final void setCategory(FileCategory category) {
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
        if (content != null) {
            this.content = Arrays.copyOf(content, content.length);
        }
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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

    public User getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(User userPortrait) {
        this.userPortrait = userPortrait;
    }

    public Institution getInstitutionLogo() {
        return institutionLogo;
    }

    public void setInstitutionLogo(Institution institutionLogo) {
        this.institutionLogo = institutionLogo;
    }

    public MultipartFile getFileData() {
        return fileData;
    }

    public void setFileData(MultipartFile fileData) {
        this.fileData = fileData;
    }

    public Document withId(Integer id) {
        this.id = id;
        return this;
    }

    public Document withCategory(final FileCategory category) {
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

    public Document withUser(User user) {
        this.user = user;
        return this;
    }

    public Document withCreatedTimestamp(DateTime dateTime) {
        this.createdTimestamp = dateTime;
        return this;
    }

    public Resource getResource() {
        if (applicationLanguageQualification != null) {
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

}
