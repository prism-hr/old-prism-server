package uk.co.alumeni.prism.domain.user;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.application.ApplicationQualification;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.profile.ProfileQualification;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.workflow.user.UserQualificationReassignmentProcessor;

@Entity
@Table(name = "user_qualification", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_account_id", "advert_id", "start_year" }) })
public class UserQualification extends UserAdvertRelationSection implements ProfileQualification<UserAccount>, UserAssignment<UserQualificationReassignmentProcessor> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_account_id", nullable = false, insertable = false, updatable = false)
    private UserAccount association;

    @OneToOne
    @JoinColumn(name = "application_qualification_id")
    private ApplicationQualification applicationQualification;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "advert_id")
    private Advert advert;

    @Column(name = "start_year", nullable = false)
    private Integer startYear;

    @Column(name = "start_month", nullable = false)
    private Integer startMonth;

    @Column(name = "award_year", nullable = false)
    private Integer awardYear;

    @Column(name = "award_month", nullable = false)
    private Integer awardMonth;

    @Column(name = "grade", nullable = false)
    private String grade;

    @Column(name = "completed", nullable = false)
    private Boolean completed;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "document_id", unique = true)
    private Document document;

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

    public ApplicationQualification getApplicationQualification() {
        return applicationQualification;
    }

    public void setApplicationQualification(ApplicationQualification applicationQualification) {
        this.applicationQualification = applicationQualification;
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
    public Advert getAdvert() {
        return advert;
    }

    @Override
    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    @Override
    public Integer getStartYear() {
        return startYear;
    }

    @Override
    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    @Override
    public Integer getStartMonth() {
        return startMonth;
    }

    @Override
    public void setStartMonth(Integer startMonth) {
        this.startMonth = startMonth;
    }

    @Override
    public Integer getAwardYear() {
        return awardYear;
    }

    @Override
    public void setAwardYear(Integer awardYear) {
        this.awardYear = awardYear;
    }

    @Override
    public Integer getAwardMonth() {
        return awardMonth;
    }

    @Override
    public void setAwardMonth(Integer awardMonth) {
        this.awardMonth = awardMonth;
    }

    @Override
    public String getGrade() {
        return grade;
    }

    @Override
    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public Boolean getCompleted() {
        return completed;
    }

    @Override
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    @Override
    public Document getDocument() {
        return document;
    }

    @Override
    public void setDocument(Document document) {
        this.document = document;
    }

    @Override
    public Class<UserQualificationReassignmentProcessor> getUserReassignmentProcessor() {
        return UserQualificationReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public UniqueEntity.EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("startYear", startYear).addProperty("startMonth", startMonth);
    }

}
