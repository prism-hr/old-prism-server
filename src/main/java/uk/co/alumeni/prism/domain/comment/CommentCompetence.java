package uk.co.alumeni.prism.domain.comment;

import com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.Competence;

import javax.persistence.*;

@Entity
@Table(name = "comment_competence", uniqueConstraints = {@UniqueConstraint(columnNames = {"comment_id", "competence_id"})})
public class CommentCompetence {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false, insertable = false, updatable = false)
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "competence_id", nullable = false)
    private Competence competence;

    @Column(name = "importance", nullable = false)
    private Integer importance;

    @Column(name = "fulfil")
    private Boolean fulfil;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "remark")
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Competence getCompetence() {
        return competence;
    }

    public void setCompetence(Competence competence) {
        this.competence = competence;
    }

    public Integer getImportance() {
        return importance;
    }

    public void setImportance(Integer importance) {
        this.importance = importance;
    }

    public Boolean getFulfil() {
        return fulfil;
    }

    public void setFulfil(Boolean fulfil) {
        this.fulfil = fulfil;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public CommentCompetence withComment(Comment comment) {
        this.comment = comment;
        return this;
    }

    public CommentCompetence withCompetence(Competence competence) {
        this.competence = competence;
        return this;
    }

    public CommentCompetence withImportance(final Integer importance) {
        this.importance = importance;
        return this;
    }

    public CommentCompetence withFulfil(Boolean fulfil) {
        this.fulfil = fulfil;
        return this;
    }

    public CommentCompetence withRating(Integer rating) {
        this.rating = rating;
        return this;
    }

    public CommentCompetence withRemark(String remark) {
        this.remark = remark;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(comment, competence);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        CommentCompetence other = (CommentCompetence) object;
        return Objects.equal(comment, other.getComment()) && Objects.equal(competence, other.getCompetence());
    }

}
