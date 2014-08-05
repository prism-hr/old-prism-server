package com.zuehlke.pgadmissions.domain;

import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name = "COMMENT_APPOINTMENT_TIMESLOT", uniqueConstraints = { @UniqueConstraint(columnNames = { "comment_id", "timeslot_datetime" }) })
public class CommentAppointmentTimeslot {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    private Comment comment;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "timeslot_datetime", nullable = false)
    private DateTime dateTime;
    
    @OneToMany(mappedBy = "appointmentTimeslot")
    private Set<CommentAppointmentPreference> appointmentPreferences;

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

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public final Set<CommentAppointmentPreference> getAppointmentPreferences() {
        return appointmentPreferences;
    }

}
