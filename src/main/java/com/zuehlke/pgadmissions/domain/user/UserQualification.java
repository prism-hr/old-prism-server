package com.zuehlke.pgadmissions.domain.user;

import static com.zuehlke.pgadmissions.PrismConstants.BACK_SLASH;

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

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.profile.ProfileQualification;

@Entity
@Table(name = "user_qualification", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_account_id", "advert_id", "start_year" }) })
public class UserQualification extends UserAdvertRelationSection implements ProfileQualification<UserAccount> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_account_id", nullable = false, insertable = false, updatable = false)
    private UserAccount association;

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

    @Override
    public Advert getAdvert() {
        return advert;
    }

    @Override
    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    public Integer getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(Integer startMonth) {
        this.startMonth = startMonth;
    }

    public Integer getAwardYear() {
        return awardYear;
    }

    public void setAwardYear(Integer awardYear) {
        this.awardYear = awardYear;
    }

    public Integer getAwardMonth() {
        return awardMonth;
    }

    public void setAwardMonth(Integer awardMonth) {
        this.awardMonth = awardMonth;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
    
    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getStartDateDisplay(String dateFormat) {
        return startYear == null ? null : startMonth.toString() + BACK_SLASH + startYear.toString();
    }

    public String getAwardDateDisplay(String dateFormat) {
        return awardYear == null ? null : awardMonth.toString() + BACK_SLASH + awardYear.toString();
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("startYear", startYear);
    }

}