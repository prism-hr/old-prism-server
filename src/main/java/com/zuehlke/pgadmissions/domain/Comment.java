package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.IndexColumn;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.validators.ATASConstraint;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "COMMENT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "action_id", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "COMMENT")
public class Comment implements Serializable {

    private static final long serialVersionUID = 2861325991249900547L;

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_form_id")
    private ApplicationForm application;

    @Size(max = 50000, message = "A maximum of 50000 characters are allowed.")
    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comment")
    private Set<CommentAssignedUser> assignedUsers = Sets.newHashSet();

    @Column(name = "created_timestamp", insertable = false)
    @Generated(GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTimestamp;

    @OneToMany
    @JoinColumn(name = "comment_id")
    private List<Document> documents = new ArrayList<Document>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id")
    @IndexColumn(name = "score_position")
    private List<Score> scores = new ArrayList<Score>();

    @Column(name = "qualified_for_phd")
    private ValidationQuestionOptions qualifiedForPhd;

    @Column(name = "english_competency_ok")
    private ValidationQuestionOptions englishCompetencyOk;

    @Enumerated(EnumType.STRING)
    @Column(name = "home_or_overseas")
    private HomeOrOverseas homeOrOverseas;

    @Column(name = "project_description_available")
    private Boolean projectDescriptionAvailable;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 255)
    @Column(name = "project_title")
    private String projectTitle;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 2000)
    @ATASConstraint
    @Column(name = "project_abstract")
    private String projectAbstract;

    @Temporal(value = TemporalType.DATE)
    @Column(name = "recommended_start_date")
    private Date recommendedStartDate;

    @Column(name = "recommended_conditions_available")
    private Boolean recommendedConditionsAvailable;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 1000)
    @Column(name = "recommended_conditions")
    private String recommendedConditions;

    @Column(name = "suitable_for_institution")
    private Boolean suitableForInstitution;

    @Column(name = "suitable_for_programme")
    private Boolean suitableForProgramme;

    @Column(name = "willing_to_interview")
    private Boolean willingToInterview;

    @Column(name = "willing_to_supervise")
    private Boolean willingToSupervise;

    @Column(name = "rating")
    private Integer rating;

    @Temporal(TemporalType.DATE)
    @Column(name = "appointment_datetime")
    private Date appointmentDate;

    @Column(name = " appointment_datetime ", nullable = false)
    private TimeZone appointmentTimezone = TimeZone.getTimeZone("GMT");

    @Column(name = "appointment_duration")
    private Integer appointmentDuration;

    @Column(name = "appointment_instructions")
    private String appointmentInstructions;

    @Column(name = "location_url")
    private String locationUrl;

    @Column(name = "declined")
    private Boolean declined;

    @Column(name = "recommend_alternative_opportunity")
    private Boolean recommendAlternativeOpportunity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_status")
    private ApplicationFormStatus nextStatus = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegate_provider_id")
    private User delegateProvider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegate_administrator_id")
    private User delegateAdministrator;

    @Column(name = "use_custom_questions")
    private Boolean useCustomQuestions;

    @Column(name = "use_custom_reference_questions")
    private Boolean useCustomReferenceQuestions;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 500)
    @Column(name = "missing_qualification_explanation")
    private String missingQualificationExplanation;

    @Transient
    private Boolean confirmNextStage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ApplicationForm getApplication() {
        return application;
    }

    public void setApplication(ApplicationForm application) {
        this.application = application;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public ValidationQuestionOptions getQualifiedForPhd() {
        return qualifiedForPhd;
    }

    public void setQualifiedForPhd(ValidationQuestionOptions qualifiedForPhd) {
        this.qualifiedForPhd = qualifiedForPhd;
    }

    public ValidationQuestionOptions getEnglishCompetencyOk() {
        return englishCompetencyOk;
    }

    public void setEnglishCompetencyOk(ValidationQuestionOptions englishCompetencyOk) {
        this.englishCompetencyOk = englishCompetencyOk;
    }

    public HomeOrOverseas getHomeOrOverseas() {
        return homeOrOverseas;
    }

    public void setHomeOrOverseas(HomeOrOverseas homeOrOverseas) {
        this.homeOrOverseas = homeOrOverseas;
    }

    public Boolean getProjectDescriptionAvailable() {
        return projectDescriptionAvailable;
    }

    public void setProjectDescriptionAvailable(Boolean projectDescriptionAvailable) {
        this.projectDescriptionAvailable = projectDescriptionAvailable;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectAbstract() {
        return projectAbstract;
    }

    public void setProjectAbstract(String projectAbstract) {
        this.projectAbstract = projectAbstract;
    }

    public Date getRecommendedStartDate() {
        return recommendedStartDate;
    }

    public void setRecommendedStartDate(Date recommendedStartDate) {
        this.recommendedStartDate = recommendedStartDate;
    }

    public Boolean getRecommendedConditionsAvailable() {
        return recommendedConditionsAvailable;
    }

    public void setRecommendedConditionsAvailable(Boolean recommendedConditionsAvailable) {
        this.recommendedConditionsAvailable = recommendedConditionsAvailable;
    }

    public String getRecommendedConditions() {
        return recommendedConditions;
    }

    public void setRecommendedConditions(String recommendedConditions) {
        this.recommendedConditions = recommendedConditions;
    }

    public Boolean getSuitableForInstitution() {
        return suitableForInstitution;
    }

    public void setSuitableForInstitution(Boolean suitableForInstitution) {
        this.suitableForInstitution = suitableForInstitution;
    }

    public Boolean getSuitableForProgramme() {
        return suitableForProgramme;
    }

    public void setSuitableForProgramme(Boolean suitableForProgramme) {
        this.suitableForProgramme = suitableForProgramme;
    }

    public Boolean getWillingToInterview() {
        return willingToInterview;
    }

    public void setWillingToInterview(Boolean willingToInterview) {
        this.willingToInterview = willingToInterview;
    }

    public Boolean getWillingToSupervise() {
        return willingToSupervise;
    }

    public void setWillingToSupervise(Boolean willingToSupervise) {
        this.willingToSupervise = willingToSupervise;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public TimeZone getAppointmentTimezone() {
        return appointmentTimezone;
    }

    public void setAppointmentTimezone(TimeZone appointmentTimezone) {
        this.appointmentTimezone = appointmentTimezone;
    }

    public Integer getAppointmentDuration() {
        return appointmentDuration;
    }

    public void setAppointmentDuration(Integer appointmentDuration) {
        this.appointmentDuration = appointmentDuration;
    }

    public String getAppointmentInstructions() {
        return appointmentInstructions;
    }

    public void setAppointmentInstructions(String appointmentInstructions) {
        this.appointmentInstructions = appointmentInstructions;
    }

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    public Boolean getDeclined() {
        return declined;
    }

    public void setDeclined(Boolean declined) {
        this.declined = declined;
    }

    public Boolean getRecommendAlternativeOpportunity() {
        return recommendAlternativeOpportunity;
    }

    public void setRecommendAlternativeOpportunity(Boolean recommendAlternativeOpportunity) {
        this.recommendAlternativeOpportunity = recommendAlternativeOpportunity;
    }

    public ApplicationFormStatus getNextStatus() {
        return nextStatus;
    }

    public void setNextStatus(ApplicationFormStatus nextStatus) {
        this.nextStatus = nextStatus;
    }

    public User getDelegateProvider() {
        return delegateProvider;
    }

    public void setDelegateProvider(User delegateProvider) {
        this.delegateProvider = delegateProvider;
    }

    public User getDelegateAdministrator() {
        return delegateAdministrator;
    }

    public void setDelegateAdministrator(User delegateAdministrator) {
        this.delegateAdministrator = delegateAdministrator;
    }

    public Boolean getUseCustomQuestions() {
        return useCustomQuestions;
    }

    public void setUseCustomQuestions(Boolean useCustomQuestions) {
        this.useCustomQuestions = useCustomQuestions;
    }

    public Boolean getUseCustomReferenceQuestions() {
        return useCustomReferenceQuestions;
    }

    public void setUseCustomReferenceQuestions(Boolean useCustomReferenceQuestions) {
        this.useCustomReferenceQuestions = useCustomReferenceQuestions;
    }

    public Boolean getConfirmNextStage() {
        return confirmNextStage;
    }

    public void setConfirmNextStage(Boolean confirmNextStage) {
        this.confirmNextStage = confirmNextStage;
    }

    public String getMissingQualificationExplanation() {
        return missingQualificationExplanation;
    }

    public void setMissingQualificationExplanation(String missingQualificationExplanation) {
        this.missingQualificationExplanation = missingQualificationExplanation;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public List<Score> getScores() {
        return scores;
    }

    public Set<CommentAssignedUser> getAssignedUsers() {
        return assignedUsers;
    }

    public void addDocument(Document document) {
        document.setIsReferenced(true);
        this.documents.add(document);
    }

    public void setDocument(Document document) {
        this.documents.clear();
        if (document != null) {
            addDocument(document);
        }
    }

    public boolean isAtLeastOneAnswerUnsure() {
        return getHomeOrOverseas() == HomeOrOverseas.UNSURE || getQualifiedForPhd() == ValidationQuestionOptions.UNSURE
                || getEnglishCompetencyOk() == ValidationQuestionOptions.UNSURE;
    }

    public String getTooltipMessage(final String role) {
        return String.format("%s %s (%s) as: %s", user.getFirstName(), user.getLastName(), user.getEmail(), StringUtils.capitalize(role));
    }

    public CommentAssignedUser getPrimaryAssignedUser() {
        for (CommentAssignedUser user : getAssignedUsers()) {
            if (user.isPrimary()) {
                return user;
            }
        }
        return null;
    }

    public CommentAssignedUser getSecondaryAssignedUser() {
        for (CommentAssignedUser user : getAssignedUsers()) {
            if (!user.isPrimary()) {
                return user;
            }
        }
        return null;
    }

}