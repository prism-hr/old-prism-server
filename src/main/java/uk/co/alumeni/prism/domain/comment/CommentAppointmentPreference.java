package uk.co.alumeni.prism.domain.comment;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "comment_appointment_preference", uniqueConstraints = {@UniqueConstraint(columnNames = {"comment_id", "preference_datetime"})})
public class CommentAppointmentPreference extends CommentShedulingDefinition {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false, insertable = false, updatable = false)
    private Comment comment;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @Column(name = "preference_datetime", nullable = false)
    private LocalDateTime dateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Comment getComment() {
        return comment;
    }

    @Override
    public void setComment(Comment comment) {
        this.comment = comment;
    }

    @Override
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public CommentAppointmentPreference withDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

}
