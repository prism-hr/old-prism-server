package com.zuehlke.pgadmissions.domain.comment;

import com.google.common.base.Objects;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "comment_appointment_preference", uniqueConstraints = { @UniqueConstraint(columnNames = { "comment_id", "preference_datetime" }) })
public class CommentAppointmentPreference {

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

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public final LocalDateTime getDateTime() {
        return dateTime;
    }

    public final void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public CommentAppointmentPreference withDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(comment, dateTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CommentAppointmentPreference other = (CommentAppointmentPreference) obj;
        return Objects.equal(comment, other.getComment()) && Objects.equal(dateTime, other.getDateTime());
    }

}
