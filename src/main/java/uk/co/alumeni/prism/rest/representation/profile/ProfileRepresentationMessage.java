package uk.co.alumeni.prism.rest.representation.profile;

public class ProfileRepresentationMessage {

    private Integer readMessageCount = 0;

    private Integer unreadMessageCount = 0;

    public Integer getReadMessageCount() {
        return readMessageCount;
    }

    public void setReadMessageCount(Integer readMessageCount) {
        this.readMessageCount = readMessageCount;
    }

    public Integer getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(Integer unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public Integer getMessageCount() {
        return readMessageCount + unreadMessageCount;
    }

    public ProfileRepresentationMessage withReadMessageCount(Integer readMessageCount) {
        this.readMessageCount = readMessageCount;
        return this;
    }

    public ProfileRepresentationMessage withUnreadMessageCount(Integer unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
        return this;
    }

}
