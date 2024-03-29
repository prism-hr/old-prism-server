package uk.co.alumeni.prism.domain.user;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import uk.co.alumeni.prism.domain.Domicile;
import uk.co.alumeni.prism.domain.definitions.PrismDisability;
import uk.co.alumeni.prism.domain.definitions.PrismEthnicity;
import uk.co.alumeni.prism.domain.definitions.PrismGender;
import uk.co.alumeni.prism.domain.profile.ProfilePersonalDetail;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user_personal_detail")
public class UserPersonalDetail implements ProfilePersonalDetail<UserAccount> {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "personalDetail")
    private UserAccount association;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private PrismGender gender;

    @Column(name = "date_of_birth")
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

    @Column(name = "phone", nullable = false)
    @Size(max = 50)
    private String phone;

    @Column(name = "skype")
    @Size(min = 6, max = 32)
    private String skype;

    @Column(name = "ethnicity")
    @Enumerated(EnumType.STRING)
    private PrismEthnicity ethnicity;

    @Column(name = "disability")
    @Enumerated(EnumType.STRING)
    private PrismDisability disability;

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
    public PrismGender getGender() {
        return gender;
    }

    @Override
    public void setGender(PrismGender gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public Domicile getNationality() {
        return nationality;
    }

    @Override
    public void setNationality(Domicile nationality) {
        this.nationality = nationality;
    }

    @Override
    public Domicile getDomicile() {
        return domicile;
    }

    @Override
    public void setDomicile(Domicile domicile) {
        this.domicile = domicile;
    }

    @Override
    public Boolean getVisaRequired() {
        return visaRequired;
    }

    @Override
    public void setVisaRequired(Boolean visaRequired) {
        this.visaRequired = visaRequired;
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
    public String getSkype() {
        return skype;
    }

    @Override
    public void setSkype(String skype) {
        this.skype = skype;
    }

    @Override
    public PrismEthnicity getEthnicity() {
        return ethnicity;
    }

    @Override
    public void setEthnicity(PrismEthnicity ethnicity) {
        this.ethnicity = ethnicity;
    }

    @Override
    public PrismDisability getDisability() {
        return disability;
    }

    @Override
    public void setDisability(PrismDisability disability) {
        this.disability = disability;
    }

}
