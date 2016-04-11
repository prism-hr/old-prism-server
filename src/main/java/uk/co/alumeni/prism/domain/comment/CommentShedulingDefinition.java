package uk.co.alumeni.prism.domain.comment;

import org.joda.time.LocalDateTime;

import com.google.common.base.Objects;

public abstract class CommentShedulingDefinition {

    public abstract Comment getComment();

    public abstract void setComment(Comment comment);

    public abstract LocalDateTime getDateTime();

    public abstract void setDateTime(LocalDateTime dateTime);

    @Override
    public int hashCode() {
        return Objects.hashCode(getComment(), getDateTime());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        CommentAppointmentTimeslot other = (CommentAppointmentTimeslot) object;
        return Objects.equal(getComment(), other.getComment()) && Objects.equal(getDateTime(), other.getDateTime());
    }

}
