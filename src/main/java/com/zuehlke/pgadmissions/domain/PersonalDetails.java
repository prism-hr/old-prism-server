package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.domain.enums.Title;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "APPLICATION_FORM_PERSONAL_DETAIL")
public class PersonalDetails implements FormSectionObject, Serializable {

    private static final long serialVersionUID = 6549850558507667533L;

    @Id
    @GeneratedValue
    private Integer id;

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

    @Column(name = "language_qualification_type")
    @Enumerated(EnumType.STRING)
    private LanguageQualificationEnum qualificationType;

    @Column(name = "language_qualification_type_name")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    private String qualificationTypeName;

    @Column(name = "language_exam_date")
    private Date examDate;

    @Column(name = "language_overall_score")
    private String overallScore;

    @Column(name = "language_reading_score")
    private String readingScore;

    @Column(name = "language_writing_score")
    private String writingScore;

    @Column(name = "language_speaking_score")
    private String speakingScore;

    @Column(name = "language_listening_score")
    private String listeningScore;

    @Column(name = "language_exam_online")
    private Boolean examOnline;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "language_qualification_document_id")
    private Document languageQualificationDocument;
    
    @Column(name = "requires_visa")
    private Boolean requiresVisa;

    @Column(name = "passport_available")
    private Boolean passportAvailable;
    
    @ESAPIConstraint(rule = "LettersAndNumbersOnly", maxLength = 35, message = "{text.field.nonlettersandnumbers}")
    @Column(name = "passport_number")
    private String passportNumber;
    
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    @Column(name = "passport_name")
    private String nameOnPassport;
    
    @Column(name = "passport_issue_date")
    @Temporal(TemporalType.DATE)
    private Date passportIssueDate;
    
    @Column(name = "passport_expiry_date")
    @Temporal(TemporalType.DATE)
    private Date passportExpiryDate;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "first_nationality")
    private Language firstNationality = null;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "second_nationality")
    private Language secondNationality = null;

    @Column(name = "title")
    @Enumerated(EnumType.STRING)
    private Title title;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ethnicity_id")
    private Ethnicity ethnicity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "disability_id")
    private Disability disability;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "domicile_id")
    private Domicile residenceCountry;

    @OneToOne(mappedBy = "personalDetails", fetch = FetchType.LAZY)
    private ApplicationForm application = null;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Language getFirstNationality() {
        return firstNationality;
    }

    public void setFirstNationality(Language firstNationality) {
        this.firstNationality = firstNationality;
    }

    public Language getSecondNationality() {
        return secondNationality;
    }

    public void setSecondNationality(Language secondNationality) {
        this.secondNationality = secondNationality;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
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

    public String getMessenger() {
        return messenger;
    }

    public void setMessenger(String messenger) {
        if (StringUtils.isBlank(messenger)) {
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

    public Boolean getEnglishFirstLanguage() {
        return englishFirstLanguage;
    }

    public void setEnglishFirstLanguage(Boolean englishFirstLanguage) {
        this.englishFirstLanguage = englishFirstLanguage;
    }

    public Boolean getRequiresVisa() {
        return requiresVisa;
    }

    public void setRequiresVisa(Boolean requiresVisa) {
        this.requiresVisa = requiresVisa;
    }

    public Boolean getPassportAvailable() {
        return passportAvailable;
    }

    public void setPassportAvailable(Boolean passportAvailable) {
        this.passportAvailable = passportAvailable;
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
    }

    public LanguageQualificationEnum getQualificationType() {
        return qualificationType;
    }

    public void setQualificationType(LanguageQualificationEnum qualificationType) {
        this.qualificationType = qualificationType;
    }

    public String getQualificationTypeName() {
        return qualificationTypeName;
    }

    public void setQualificationTypeName(String qualificationTypeName) {
        this.qualificationTypeName = qualificationTypeName;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    public String getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(String overallScore) {
        this.overallScore = overallScore;
    }

    public String getReadingScore() {
        return readingScore;
    }

    public void setReadingScore(String readingScore) {
        this.readingScore = readingScore;
    }

    public String getWritingScore() {
        return writingScore;
    }

    public void setWritingScore(String writingScore) {
        this.writingScore = writingScore;
    }

    public String getSpeakingScore() {
        return speakingScore;
    }

    public void setSpeakingScore(String speakingScore) {
        this.speakingScore = speakingScore;
    }

    public String getListeningScore() {
        return listeningScore;
    }

    public void setListeningScore(String listeningScore) {
        this.listeningScore = listeningScore;
    }

    public Boolean getExamOnline() {
        return examOnline;
    }

    public void setExamOnline(Boolean examOnline) {
        this.examOnline = examOnline;
    }

    public Document getLanguageQualificationDocument() {
        return languageQualificationDocument;
    }

    public void setLanguageQualificationDocument(Document languageQualificationDocument) {
        this.languageQualificationDocument = languageQualificationDocument;
    }
    
    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getNameOnPassport() {
        return nameOnPassport;
    }

    public void setNameOnPassport(String nameOnPassport) {
        this.nameOnPassport = nameOnPassport;
    }

    public Date getPassportIssueDate() {
        return passportIssueDate;
    }

    public void setPassportIssueDate(Date passportIssueDate) {
        this.passportIssueDate = passportIssueDate;
    }

    public Date getPassportExpiryDate() {
        return passportExpiryDate;
    }

    public void setPassportExpiryDate(Date passportExpiryDate) {
        this.passportExpiryDate = passportExpiryDate;
    }

}
