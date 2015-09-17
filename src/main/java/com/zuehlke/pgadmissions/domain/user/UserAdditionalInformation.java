package com.zuehlke.pgadmissions.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

    @Column(name = "convictions_text")
    private String convictionsText;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserAccount getAssociation() {
        return association;
    }

    public void setAssociation(UserAccount application) {
        this.association = application;
    }

    public String getConvictionsText() {
        return convictionsText;
    }

    public void setConvictionsText(String convictionsText) {
        this.convictionsText = convictionsText;
    }

}
