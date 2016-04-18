package uk.co.alumeni.prism.dto;

import java.util.List;

public class UserAdvertDTO {

    private boolean allVisible;

    private List<Integer> visibleAdverts;

    private List<Integer> invisibleAdverts;

    public boolean isAllVisible() {
        return allVisible;
    }

    public void setAllVisible(boolean allVisible) {
        this.allVisible = allVisible;
    }

    public List<Integer> getVisibleAdverts() {
        return visibleAdverts;
    }

    public void setVisibleAdverts(List<Integer> visibleAdverts) {
        this.visibleAdverts = visibleAdverts;
    }

    public List<Integer> getInvisibleAdverts() {
        return invisibleAdverts;
    }

    public void setInvisibleAdverts(List<Integer> invisibleAdverts) {
        this.invisibleAdverts = invisibleAdverts;
    }

    public UserAdvertDTO withAllVisible(boolean allVisible) {
        this.allVisible = allVisible;
        return this;
    }

    public UserAdvertDTO withVisibleAdverts(List<Integer> visibleAdverts) {
        this.visibleAdverts = visibleAdverts;
        return this;
    }

    public UserAdvertDTO withInvisibleAdverts(List<Integer> revokedAdverts) {
        this.invisibleAdverts = revokedAdverts;
        return this;
    }

}
