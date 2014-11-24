package com.zuehlke.pgadmissions.domain.application;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.zuehlke.pgadmissions.domain.document.Document;

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
    @JoinColumn(name = "research_statement_id")
    private Document researchStatement;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cv_id", nullable = false)
    private Document cv;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "covering_letter_id", nullable = false)
    private Document coveringLetter;

    @OneToOne(mappedBy = "document")
    private Application application;

    @Transient
    private Boolean acceptedTerms;

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

    public final Document getResearchStatement() {
        return researchStatement;
    }

    public final void setResearchStatement(Document researchStatement) {
        this.researchStatement = researchStatement;
    }

    public Document getCv() {
        return cv;
    }

    public void setCv(Document cv) {
        this.cv = cv;
    }

    public final Document getCoveringLetter() {
        return coveringLetter;
    }

    public final void setCoveringLetter(Document coveringLetter) {
        this.coveringLetter = coveringLetter;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public ApplicationDocument withPersonalStatement(Document document) {
        this.personalStatement = document;
        return this;
    }
    
    public ApplicationDocument withResearchStatement(Document researchStatement) {
        this.researchStatement = researchStatement;
        return this;
    }

    public ApplicationDocument withCv(Document document) {
        this.cv = document;
        return this;
    }

    public ApplicationDocument withCoveringLetter(Document coveringLetter) {
        this.coveringLetter = coveringLetter;
        return this;
    }

}
