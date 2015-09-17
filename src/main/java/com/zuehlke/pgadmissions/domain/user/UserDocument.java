package com.zuehlke.pgadmissions.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.profile.ProfileDocument;

@Entity
@Table(name = "user_document")
public class UserDocument implements ProfileDocument<UserAccount> {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "document")
    private UserAccount association;

    @Column(name = "personal_summary", nullable = false)
    private String personalSummary;

    @OneToOne
    @JoinColumn(name = "cv_id", unique = true)
    private Document cv;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserAccount getAssociation() {
        return association;
    }

    public void setAssociation(UserAccount association) {
        this.association = association;
    }

    public String getPersonalSummary() {
        return personalSummary;
    }

    public void setPersonalSummary(String personalSummary) {
        this.personalSummary = personalSummary;
    }

    public Document getCv() {
        return cv;
    }

    public void setCv(Document cv) {
        this.cv = cv;
    }

}
