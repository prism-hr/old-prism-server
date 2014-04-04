package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity(name="APPLICATION_FORM_DOCUMENT")
public class ApplicationFormDocument implements Serializable, FormSectionObject {

    private static final long serialVersionUID = 1088530727424344593L;
    
    @Id
    @GeneratedValue
    private Integer id;
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "personal_statement_id")
    private Document personalStatement;
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cv_id")
    private Document cv = null;
    
    @OneToOne(mappedBy = "applicationFormDocument", fetch = FetchType.LAZY)
    private ApplicationForm application;
    
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

    public ApplicationForm getApplication() {
        return application;
    }

    public void setApplication(ApplicationForm application) {
        this.application = application;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }
    
    public ApplicationFormDocument withPersonalStatement(Document document) {
        this.personalStatement = document;
        return this;
    }
    
    public ApplicationFormDocument withCv(Document document) {
        this.cv = document;
        return this;
    }

}
