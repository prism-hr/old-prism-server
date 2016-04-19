package uk.co.alumeni.prism.dto;

import java.util.Set;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;

public class UserResourceDTO {

    public HashMultimap<PrismScope, ResourceAdvertDTO> visibleDirect;

    public HashMultimap<PrismScope, ResourceAdvertDTO> visibleIndirect;

    public HashMultimap<PrismScope, ResourceAdvertDTO> getVisibleDirect() {
        return visibleDirect;
    }

    public void setVisibleDirect(HashMultimap<PrismScope, ResourceAdvertDTO> visibleDirect) {
        this.visibleDirect = visibleDirect;
    }

    public HashMultimap<PrismScope, ResourceAdvertDTO> getVisibleIndirect() {
        return visibleIndirect;
    }

    public void setVisibleIndirect(HashMultimap<PrismScope, ResourceAdvertDTO> visibleIndirect) {
        this.visibleIndirect = visibleIndirect;
    }

    public HashMultimap<PrismScope, Integer> getVisibleResources() {
        HashMultimap<PrismScope, Integer> target = HashMultimap.create();
        appendVisibleResources(visibleDirect, target);
        appendVisibleResources(visibleIndirect, target);
        return target;
    }

    public Set<Integer> getVisibleAdvertsDirect() {
        Set<Integer> target = Sets.newHashSet();
        if (visibleDirect != null) {
            visibleDirect.keySet().stream().forEach(scope -> {
                visibleDirect.get(scope).stream().forEach(resource -> target.add(resource.getAdvertId()));
            });
        }
        return target;
    }

    public UserResourceDTO withVisibleDirect(HashMultimap<PrismScope, ResourceAdvertDTO> visibleDirect) {
        this.visibleDirect = visibleDirect;
        return this;
    }

    public UserResourceDTO withVisibleIndirect(HashMultimap<PrismScope, ResourceAdvertDTO> visibleIndirect) {
        this.visibleIndirect = visibleIndirect;
        return this;
    }

    private void appendVisibleResources(HashMultimap<PrismScope, ResourceAdvertDTO> source, HashMultimap<PrismScope, Integer> target) {
        if (source != null) {
            visibleDirect.keySet().stream().forEach(scope -> {
                visibleDirect.get(scope).stream().forEach(resource -> target.put(scope, resource.getResourceId()));
            });
        }
    }

}
