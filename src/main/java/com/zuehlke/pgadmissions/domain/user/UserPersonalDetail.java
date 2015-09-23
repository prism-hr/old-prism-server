package com.zuehlke.pgadmissions.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.definitions.PrismGender;
import com.zuehlke.pgadmissions.domain.profile.ProfilePersonalDetail;

@Entity
@Table(name = "user_personal_detail")
public class UserPersonalDetail implements ProfilePersonalDetail<UserAccount> {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "personalDetail")
    private UserAccount association;

    @Column(name = "gender_id")
    @Enumerated(EnumType.STRING)
    private PrismGender gender;

    @Column(name = "date_of_birth", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate dateOfBirth;

    @ManyToOne
    @JoinColumn(name = "nationality_id")
    private Domicile nationality;

    @ManyToOne
    @JoinColumn(name = "domicile_id")
    private Domicile domicile;

    @Column(name = "visa_required")
    private Boolean visaRequired;

    @Column(name = "skype")
    @Size(min = 6, max = 32)
    private String skype;

    @Column(name = "phone", nullable = false)
    @Size(max = 50)
    private String phone;

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

    public PrismGender getGender() {
        return gender;
    }

    public void setGender(PrismGender gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Domicile getNationality() {
        return nationality;
    }

    public void setNationality(Domicile nationality) {
        this.nationality = nationality;
    }

    public Domicile getDomicile() {
        return domicile;
    }

    public void setDomicile(Domicile domicile) {
        this.domicile = domicile;
    }

    public Boolean getVisaRequired() {
        return visaRequired;
    }

    public void setVisaRequired(Boolean visaRequired) {
        this.visaRequired = visaRequired;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

}
