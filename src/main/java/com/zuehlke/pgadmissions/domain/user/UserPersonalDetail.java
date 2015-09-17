package com.zuehlke.pgadmissions.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.imported.ImportedDomicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.profile.ProfilePersonalDetail;

@Entity
@Table(name = "user_personal_detail")
public class UserPersonalDetail implements ProfilePersonalDetail<UserAccount> {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "personalDetail")
    private UserAccount association;

    @ManyToOne
    @JoinColumn(name = "imported_title_id")
    private ImportedEntitySimple title;

    @ManyToOne
    @JoinColumn(name = "imported_gender_id")
    private ImportedEntitySimple gender;

    @Column(name = "date_of_birth", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate dateOfBirth;

    @ManyToOne
    @JoinColumn(name = "imported_domicile_id")
    private ImportedDomicile domicile;

    @ManyToOne
    @JoinColumn(name = "imported_nationality_id")
    private ImportedDomicile nationality;

    @Column(name = "visa_required")
    private Boolean visaRequired;

    @Column(name = "skype")
    @Size(min = 6, max = 32)
    private String skype;

    @Column(name = "phone", nullable = false)
    @Size(max = 50)
    private String phone;

    @ManyToOne
    @JoinColumn(name = "imported_ethnicity_id")
    private ImportedEntitySimple ethnicity;

    @ManyToOne
    @JoinColumn(name = "imported_disability_id")
    private ImportedEntitySimple disability;

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

    public ImportedEntitySimple getTitle() {
        return title;
    }

    public void setTitle(ImportedEntitySimple title) {
        this.title = title;
    }

    public ImportedEntitySimple getGender() {
        return gender;
    }

    public void setGender(ImportedEntitySimple gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public ImportedDomicile getDomicile() {
        return domicile;
    }

    public void setDomicile(ImportedDomicile domicile) {
        this.domicile = domicile;
    }

    public ImportedDomicile getNationality() {
        return nationality;
    }

    public void setNationality(ImportedDomicile nationality) {
        this.nationality = nationality;
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

    public ImportedEntitySimple getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(ImportedEntitySimple ethnicity) {
        this.ethnicity = ethnicity;
    }

    public ImportedEntitySimple getDisability() {
        return disability;
    }

    public void setDisability(ImportedEntitySimple disability) {
        this.disability = disability;
    }

}
