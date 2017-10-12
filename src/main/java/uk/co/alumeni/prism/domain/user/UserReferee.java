package uk.co.alumeni.prism.domain.user;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.application.ApplicationReferee;
import uk.co.alumeni.prism.domain.profile.ProfileReferee;
import uk.co.alumeni.prism.workflow.user.UserRefereeReassignmentProcessor;

import javax.persistence.*;

@Entity
@Table(name = "user_referee", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_account_id", "user_id"})})
public class UserReferee extends UserAdvertRelationSection implements ProfileReferee<UserAccount>, UserAssignment<UserRefereeReassignmentProcessor> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_account_id", nullable = false, insertable = false, updatable = false)
    private UserAccount association;

    @ManyToOne
    @JoinColumn(name = "application_referee_id")
    private ApplicationReferee applicationReferee;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "skype")
    private String skype;
    
    @Column(name = "supervisor", nullable = false)
    private Boolean supervisor;

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
    public void setAssociation(UserAccount UserAccount) {
        this.association = UserAccount;
    }

    public ApplicationReferee getApplicationReferee() {
        return applicationReferee;
    }

    public void setApplicationReferee(ApplicationReferee applicationReferee) {
        this.applicationReferee = applicationReferee;
    }

    @Override
    public Advert getAdvert() {
        return advert;
    }

    @Override
    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    @Override
    public String getSkype() {
        return skype;
    }

    @Override
    public void setSkype(String skype) {
        this.skype = skype;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    @Override
    public Boolean getSupervisor() {
        return supervisor;
    }
    
    @Override
    public void setSupervisor(Boolean supervisor) {
        this.supervisor = supervisor;
    }
    
    @Override
    public Class<UserRefereeReassignmentProcessor> getUserReassignmentProcessor() {
        return UserRefereeReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("association", getAssociation()).addProperty("user", getUser());
    }

}
