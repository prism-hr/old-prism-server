package uk.co.alumeni.prism.dto;

import java.util.List;

public class UserAdvertDTO {

    private boolean allVisible;

    private List<Integer> visibleAdverts;

    private List<Integer> revokedAdverts;

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

    public List<Integer> getRevokedAdverts() {
        return revokedAdverts;
    }

    public void setRevokedAdverts(List<Integer> revokedAdverts) {
        this.revokedAdverts = revokedAdverts;
    }

    public UserAdvertDTO withAllVisible(boolean allVisible) {
        this.allVisible = allVisible;
        return this;
    }

    public UserAdvertDTO withVisibleAdverts(List<Integer> visibleAdverts) {
        this.visibleAdverts = visibleAdverts;
        return this;
    }

    public UserAdvertDTO withRevokedAdverts(List<Integer> revokedAdverts) {
        this.revokedAdverts = revokedAdverts;
        return this;
    }

}
