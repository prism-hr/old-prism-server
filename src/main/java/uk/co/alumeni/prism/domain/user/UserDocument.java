package uk.co.alumeni.prism.domain.user;

import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.profile.ProfileDocument;

import javax.persistence.*;

@Entity
@Table(name = "user_document")
public class UserDocument implements ProfileDocument<UserAccount> {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "document")
    private UserAccount association;

    @Column(name = "personal_summary")
    private String personalSummary;

    @OneToOne
    @JoinColumn(name = "cv_id", unique = true)
    private Document cv;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public UserAccount getAssociation() {
        return association;
    }

    @Override
    public void setAssociation(UserAccount association) {
        this.association = association;
    }

    @Override
    public String getPersonalSummary() {
        return personalSummary;
    }

    @Override
    public void setPersonalSummary(String personalSummary) {
        this.personalSummary = personalSummary;
    }

    @Override
    public Document getCv() {
        return cv;
    }

    @Override
    public void setCv(Document cv) {
        this.cv = cv;
    }

}
