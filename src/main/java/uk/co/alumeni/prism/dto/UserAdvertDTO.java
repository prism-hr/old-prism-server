package uk.co.alumeni.prism.dto;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

public class UserAdvertDTO {

    private boolean allVisible;

    private List<Integer> visibleDirect;

    private List<Integer> visibleIndirect;

    private List<Integer> invisible;

    public boolean isAllVisible() {
        return allVisible;
    }

    public void setAllVisible(boolean allVisible) {
        this.allVisible = allVisible;
    }

    public List<Integer> getVisibleDirect() {
        return visibleDirect;
    }

    public void setVisibleDirect(List<Integer> visibleDirect) {
        this.visibleDirect = visibleDirect;
    }

    public List<Integer> getVisibleIndirect() {
        return visibleIndirect;
    }

    public void setVisibleIndirect(List<Integer> visibleIndirect) {
        this.visibleIndirect = visibleIndirect;
    }

    public List<Integer> getInvisible() {
        return invisible;
    }

    public void setInvisible(List<Integer> invisible) {
        this.invisible = invisible;
    }

    public UserAdvertDTO withAllVisible(boolean allVisible) {
        this.allVisible = allVisible;
        return this;
    }

    public Set<Integer> getVisible() {
        Set<Integer> target = Sets.newHashSet();
        assignVisibleAdverts(target, visibleDirect);
        assignVisibleAdverts(target, visibleIndirect);
        return target;
    }

    public UserAdvertDTO withVisibleDirect(List<Integer> visibleDirect) {
        this.visibleDirect = visibleDirect;
        return this;
    }

    public UserAdvertDTO withVisibleIndirect(List<Integer> visibleIndirect) {
        this.visibleIndirect = visibleIndirect;
        return this;
    }

    public UserAdvertDTO withInvisibleAdverts(List<Integer> invisible) {
        this.invisible = invisible;
        return this;
    }

    private void assignVisibleAdverts(Set<Integer> target, List<Integer> source) {
        if (source != null) {
            target.addAll(source);
        }
    }

}
