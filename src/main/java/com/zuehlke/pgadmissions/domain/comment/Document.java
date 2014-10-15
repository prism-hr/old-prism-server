package com.zuehlke.pgadmissions.domain.comment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.bouncycastle.util.Arrays;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.web.multipart.MultipartFile;

import com.zuehlke.pgadmissions.domain.application.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.application.ApplicationFunding;
import com.zuehlke.pgadmissions.domain.application.ApplicationLanguageQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.user.User;

@Entity
@Table(name = "DOCUMENT")
public class Document {

    @Id
    @GeneratedValue
    private Integer id;

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
    
    @OneToOne(mappedBy = "cv")
    private ApplicationDocument applicationCv;
    
    @OneToOne(mappedBy = "personalStatement")
    private ApplicationDocument applicationPersonalStatement;

    @Transient
    private MultipartFile fileData;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
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

    public final User getUser() {
        return user;
    }

    public final void setUser(User user) {
        this.user = user;
    }

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public final Comment getComment() {
        return comment;
    }

    public final ApplicationLanguageQualification getApplicationLanguageQualification() {
        return applicationLanguageQualification;
    }

    public final ApplicationQualification getApplicationQualification() {
        return applicationQualification;
    }

    public final ApplicationFunding getApplicationFunding() {
        return applicationFunding;
    }

    public final ApplicationDocument getApplicationCv() {
        return applicationCv;
    }

    public final ApplicationDocument getApplicationPersonalStatement() {
        return applicationPersonalStatement;
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
    
}
