package uk.co.alumeni.prism.domain.application;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.profile.ProfileQualification;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAssignment;
import uk.co.alumeni.prism.workflow.user.ApplicationQualificationReassignmentProcessor;

import javax.persistence.*;

@Entity
@Table(name = "application_qualification", uniqueConstraints = {@UniqueConstraint(columnNames = {"application_id", "advert_id", "start_year", "start_month"})})
public class ApplicationQualification extends ApplicationAdvertRelationSection
        implements ProfileQualification<Application>, UserAssignment<ApplicationQualificationReassignmentProcessor> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false, insertable = false, updatable = false)
    private Application association;

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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "document_id", unique = true)
    private Document document;

    @Column(name = "completed", nullable = false)
    private Boolean completed;

    @Column(name = "last_updated_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdatedTimestamp;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Application getAssociation() {
        return association;
    }

    @Override
    public void setAssociation(Application association) {
        this.association = association;
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
    public DateTime getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    @Override
    public void setLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    @Override
    public Class<ApplicationQualificationReassignmentProcessor> getUserReassignmentProcessor() {
        return ApplicationQualificationReassignmentProcessor.class;
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
