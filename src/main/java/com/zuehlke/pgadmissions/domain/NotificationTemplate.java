package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.zuehlke.pgadmissions.domain.enums.NotificationTemplateId;

@Entity
@Table(name = "NOTIFICATION_TEMPLATE")
public class NotificationTemplate implements Serializable {

    private static final long serialVersionUID = -3640707667534813533L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private NotificationTemplateId name;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "version")
    @Temporal(TemporalType.TIMESTAMP)
    private Date version;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "subject")
    private String subject;

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Boolean getActive() {
        return active;
    }

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }

    public NotificationTemplateId getName() {
        return name;
    }

    public void setName(NotificationTemplateId name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
