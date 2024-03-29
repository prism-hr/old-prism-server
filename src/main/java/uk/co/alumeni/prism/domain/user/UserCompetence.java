package uk.co.alumeni.prism.domain.user;

import uk.co.alumeni.prism.domain.Competence;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.workflow.user.UserCompetenceReassignmentProcessor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "user_competence", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "competence_id"})})
public class UserCompetence implements UniqueEntity, UserAssignment<UserCompetenceReassignmentProcessor> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "competence_id", nullable = false)
    private Competence competence;

    @Column(name = "rating_count", nullable = false)
    private Integer ratingCount;

    @Column(name = "rating_average", nullable = false)
    private BigDecimal ratingAverage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Competence getCompetence() {
        return competence;
    }

    public void setCompetence(Competence competence) {
        this.competence = competence;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public BigDecimal getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(BigDecimal ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

    public UserCompetence withUser(User user) {
        this.user = user;
        return this;
    }

    public UserCompetence withCompetence(Competence competence) {
        this.competence = competence;
        return this;
    }

    public UserCompetence withRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
        return this;
    }

    public UserCompetence withRatingAverage(BigDecimal ratingAverage) {
        this.ratingAverage = ratingAverage;
        return this;
    }

    @Override
    public Class<UserCompetenceReassignmentProcessor> getUserReassignmentProcessor() {
        return UserCompetenceReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("user", user).addProperty("competence", competence);
    }

}
