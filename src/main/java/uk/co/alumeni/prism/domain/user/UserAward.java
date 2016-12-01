package uk.co.alumeni.prism.domain.user;

import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.application.ApplicationAward;
import uk.co.alumeni.prism.domain.profile.ProfileAward;

import javax.persistence.*;

@Entity
@Table(name = "user_award", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_account_id", "name", "award_year", "award_month"})})
public class UserAward extends UserSection implements ProfileAward<UserAccount> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_account_id", nullable = false, insertable = false, updatable = false)
    private UserAccount association;

    @OneToOne
    @JoinColumn(name = "application_award_id")
    private ApplicationAward applicationAward;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "award_year", nullable = false)
    private Integer awardYear;

    @Column(name = "award_month", nullable = false)
    private Integer awardMonth;

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

    public ApplicationAward getApplicationAward() {
        return applicationAward;
    }

    public void setApplicationAward(ApplicationAward applicationAward) {
        this.applicationAward = applicationAward;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
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
    public UniqueEntity.EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("name", name).addProperty("awardYear", awardYear).addProperty("awardMonth", awardMonth);
    }

}
