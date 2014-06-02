package com.zuehlke.pgadmissions.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "APPLICATION_DOCUMENT")
public class ApplicationDocument {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "personal_statement_id")
    private Document personalStatement;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cv_id", nullable = false)
    private Document cv;

    @OneToOne(mappedBy = "applicationDocument")
    private Application application;

    @Transient
    private boolean acceptedTerms;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Document getPersonalStatement() {
        return personalStatement;
    }

    public void setPersonalStatement(Document personalStatement) {
        this.personalStatement = personalStatement;
    }

    public Document getCv() {
        return cv;
    }

    public void setCv(Document cv) {
        this.cv = cv;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

    public ApplicationDocument withPersonalStatement(Document document) {
        this.personalStatement = document;
        return this;
    }

    public ApplicationDocument withCv(Document document) {
        this.cv = document;
        return this;
    }

}
