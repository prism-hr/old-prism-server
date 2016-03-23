package uk.co.alumeni.prism.rest.representation.profile;

public class ProfileRepresentationMessage {

    private Integer readMessageCount;
    
    private Integer unreadMessageCount;

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
    
}
