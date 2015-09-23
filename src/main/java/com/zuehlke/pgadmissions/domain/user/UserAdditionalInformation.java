package com.zuehlke.pgadmissions.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.profile.ProfileAdditionalInformation;

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
