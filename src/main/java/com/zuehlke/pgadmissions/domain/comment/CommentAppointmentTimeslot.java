package com.zuehlke.pgadmissions.domain.comment;

import com.google.common.base.Objects;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "comment_appointment_timeslot", uniqueConstraints = { @UniqueConstraint(columnNames = { "comment_id", "timeslot_datetime" }) })
public class CommentAppointmentTimeslot {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false, insertable = false, updatable = false)
    private Comment comment;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @Column(name = "timeslot_datetime", nullable = false)
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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public CommentAppointmentTimeslot withDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public String getDateTimeDisplay(String dateTimeFormat) {
        return dateTime.toString(dateTimeFormat);
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
        CommentAppointmentTimeslot other = (CommentAppointmentTimeslot) obj;
        return Objects.equal(comment, other.getComment()) && Objects.equal(dateTime, other.getDateTime());
    }

}
