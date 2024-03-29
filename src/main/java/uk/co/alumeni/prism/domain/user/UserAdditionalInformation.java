package uk.co.alumeni.prism.domain.user;

import uk.co.alumeni.prism.domain.profile.ProfileAdditionalInformation;

import javax.persistence.*;

@Entity
@Table(name = "user_additional_information")
public class UserAdditionalInformation implements ProfileAdditionalInformation<UserAccount> {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "additionalInformation")
    private UserAccount association;

    @Lob
    @Column(name = "requirements")
    private String requirements;

    @Lob
    @Column(name = "convictions")
    private String convictions;

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
    public void setAssociation(UserAccount application) {
        this.association = application;
    }

    @Override
    public String getRequirements() {
        return requirements;
    }

    @Override
    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    @Override
    public String getConvictions() {
        return convictions;
    }

    @Override
    public void setConvictions(String convictions) {
        this.convictions = convictions;
    }

}
