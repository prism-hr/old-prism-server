package uk.co.alumeni.prism.dto;

import org.joda.time.DateTime;

import uk.co.alumeni.prism.domain.message.MessageThread;

public class MessageThreadDTO {

    private MessageThread thread;

    private DateTime updatedTimestamp;

    public MessageThread getThread() {
        return thread;
    }

    public void setThread(MessageThread thread) {
        this.thread = thread;
    }

    public DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

}
