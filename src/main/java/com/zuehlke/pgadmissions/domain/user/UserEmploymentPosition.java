package com.zuehlke.pgadmissions.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.profile.ProfileEmploymentPosition;
import com.zuehlke.pgadmissions.workflow.user.UserEmploymentPositionReassignmentProcessor;

@Entity
@Table(name = "user_employment_position", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_account_id", "advert_id", "start_year" }) })
public class UserEmploymentPosition extends UserAdvertRelationSection
        implements ProfileEmploymentPosition<UserAccount>, UserAssignment<UserEmploymentPositionReassignmentProcessor> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_account_id", nullable = false, insertable = false, updatable = false)
    private UserAccount association;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @Column(name = "start_year", nullable = false)
    private Integer startYear;

    @Column(name = "start_month", nullable = false)
    private Integer startMonth;

    @Column(name = "end_year")
    private Integer endYear;

    @Column(name = "end_month")
    private Integer endMonth;

    @Column(name = "current", nullable = false)
    private Boolean current;

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
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
    public Integer getEndYear() {
        return endYear;
    }

    @Override
    public void setEndYear(Integer endYear) {
        this.endYear = endYear;
    }

    @Override
    public Integer getEndMonth() {
        return endMonth;
    }

    @Override
    public void setEndMonth(Integer endMonth) {

        this.endMonth = endMonth;
    }

    @Override
    public Boolean getCurrent() {
        return current;
    }

    @Override
    public void setCurrent(Boolean current) {
        this.current = current;
    }

    @Override
    public Class<UserEmploymentPositionReassignmentProcessor> getUserReassignmentProcessor() {
        return UserEmploymentPositionReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("startYear", startYear).addProperty("startMonth", startMonth);
    }

}
