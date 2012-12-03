package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.Title;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "APPLICATION_FORM_PERSONAL_DETAIL")
@Access(AccessType.FIELD)
public class PersonalDetails extends DomainObject<Integer> implements FormSectionObject {

	private static final long serialVersionUID = 6549850558507667533L;
	
	@Transient
	private boolean acceptedTerms;
	
	@Column(name = "skype")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
	private String messenger;
	
	@Column(name = "phone")
	@ESAPIConstraint(rule = "PhoneNumber", maxLength = 35, message = "{text.field.notphonenumber}")
	private String phoneNumber;
	
	@Column(name = "english_first_language")
	private Boolean englishFirstLanguage;
	
	@Column(name = "language_qualification_available")
	private Boolean languageQualificationAvailable;
	
	@Valid
	@OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE, javax.persistence.CascadeType.MERGE }, orphanRemoval = true)
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "application_form_personal_detail_id")
	private List<LanguageQualification> languageQualifications = new ArrayList<LanguageQualification>();
	
	@Column(name = "requires_visa")
	private Boolean requiresVisa;
	
	@Valid
	@OneToOne(orphanRemoval = true, mappedBy = "personalDetails", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private PassportInformation passportInformation;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CANDIDATE_NATIONALITY_LINK", joinColumns = { @JoinColumn(name = "candidate_personal_details_id") }, inverseJoinColumns = { @JoinColumn(name = "candidate_language_id") })
	private List<Language> candidateNationalities = new ArrayList<Language>();
	
	@Column(name = "title")
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.TitleEnumUserType")
    private Title title;

    @Column(name = "first_name")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
	private String firstName;

	@Column(name = "last_name")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 40)
	private String lastName;

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.GenderEnumUserType")
	private Gender gender;
	
	@ESAPIConstraint(rule = "Email", maxLength = 255, message = "{text.email.notvalid}")
	private String email;

	@Column(name = "date_of_birth")
	@Temporal(TemporalType.DATE)
	private Date dateOfBirth;

	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country country;

	@ManyToOne
	@JoinColumn(name = "ethnicity_id")
	private Ethnicity ethnicity;
	
	@ManyToOne
	@JoinColumn(name = "disability_id")
	private Disability disability;
	
	@ManyToOne
	@JoinColumn(name = "domicile_id")
	private Domicile residenceCountry;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_form_id")
	private ApplicationForm application = null;

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	@Id
	@GeneratedValue
	@Access(AccessType.PROPERTY)
	public Integer getId() {
		return id;
	}

	public Title getTitle() {
	    return title;
	}

	public void setTitle(Title title) {
	    this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Domicile getResidenceCountry() {
		return residenceCountry;
	}

	public void setResidenceCountry(Domicile residenceCountry) {
		this.residenceCountry = residenceCountry;
	}

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public void setEthnicity(Ethnicity eth) {
		this.ethnicity = eth;
	}

	public Ethnicity getEthnicity() {
		return ethnicity;
	}

	public void setDisability(Disability disability) {
		this.disability = disability;
	}
	
	public Disability getDisability() {
		return disability;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Language> getCandidateNationalities() {
		return candidateNationalities;
	}

	public void setCandidateNationalities(List<Language> candiateNationalities) {
		this.candidateNationalities.clear();
		for (Language nationality : candiateNationalities) {
			if(nationality != null){
				this.candidateNationalities.add(nationality);
			}
		}
	}

	public String getMessenger() {
		return messenger;
	}

	public void setMessenger(String messenger) {
		if(StringUtils.isBlank(messenger)){
			this.messenger = null;
		}
		this.messenger = messenger;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	//convenience metod for Freemarker	
	public boolean isEnglishFirstLanguageSet() {
		return (englishFirstLanguage != null);
	}	
	
	public Boolean getEnglishFirstLanguage() {
		return englishFirstLanguage;
	}

	public void setEnglishFirstLanguage(Boolean englishFirstLanguage) {
		this.englishFirstLanguage = englishFirstLanguage;
	}
	
	//convenience metod for Freemarker
	public boolean isRequiresVisaSet() {
		return (requiresVisa != null);
	}
	
	//convenience metod for Freemarker
    public boolean isLanguageQualificationAvailableSet() {
        return (languageQualificationAvailable != null);
    }
	
	public Boolean getRequiresVisa() {
		return requiresVisa;
	}

	public void setRequiresVisa(Boolean requiresVisa) {
		this.requiresVisa = requiresVisa;
		if (BooleanUtils.isFalse(requiresVisa)) {
		    this.passportInformation = null;
		}
	}

	public boolean isAcceptedTerms() {
		return acceptedTerms;
	}

	public void setAcceptedTerms(boolean acceptedTerms) {
		this.acceptedTerms = acceptedTerms;
	}

    public Boolean getLanguageQualificationAvailable() {
        return languageQualificationAvailable;
    }

    public void setLanguageQualificationAvailable(Boolean languageQualificationAvailable) {
        this.languageQualificationAvailable = languageQualificationAvailable;
        if (BooleanUtils.isFalse(languageQualificationAvailable)) {
            this.languageQualifications = new ArrayList<LanguageQualification>();
        }
    }

    public PassportInformation getPassportInformation() {
        return passportInformation;
    }

    public void setPassportInformation(PassportInformation passportInformation) {
        this.passportInformation = passportInformation;
        if (this.passportInformation != null) {
            this.passportInformation.setPersonalDetails(this);
        }
    }

    public List<LanguageQualification> getLanguageQualifications() {
        return languageQualifications;
    }

    public void setLanguageQualifications(List<LanguageQualification> languageQualifications) {
        this.languageQualifications = languageQualifications;
    }
    
    public void addLanguageQualification(LanguageQualification languageQualification) {
        if (languageQualification != null && languageQualification.getPersonalDetails() == null) {
            languageQualification.setPersonalDetails(this);
            this.languageQualifications.add(languageQualification);
        } else if (languageQualification != null) {
            this.languageQualifications.add(languageQualification);
        }
    }
    
    public List<LanguageQualification> getLanguageQualificationToSend() {
        List<LanguageQualification> result = new ArrayList<LanguageQualification>(1);
        for (LanguageQualification languageQualification : languageQualifications) {
            if (BooleanUtils.isTrue(languageQualification.getSendToUCL())) {
                Validate.notNull(languageQualification.getLanguageQualificationDocument(),
                        "LanguageQualification with id: " + languageQualification.getId()
                                + " is marked for sending to UCL but has no document assosiated with it.");
                result.add(languageQualification);
            }
        }
        return result;
    }
}
