package uk.co.alumeni.prism.dto;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;

import java.util.Set;

public class UserResourceDTO {

    public HashMultimap<PrismScope, ResourceAdvertDTO> resourcesDirect;

    public HashMultimap<PrismScope, ResourceAdvertDTO> resourcesIndirect;

    public HashMultimap<PrismScope, ResourceAdvertDTO> getResourcesDirect() {
        return resourcesDirect;
    }

    public void setResourcesDirect(HashMultimap<PrismScope, ResourceAdvertDTO> resourcesDirect) {
        this.resourcesDirect = resourcesDirect;
    }

    public HashMultimap<PrismScope, ResourceAdvertDTO> getResourcesIndirect() {
        return resourcesIndirect;
    }

    public void setResourcesIndirect(HashMultimap<PrismScope, ResourceAdvertDTO> resourcesIndirect) {
        this.resourcesIndirect = resourcesIndirect;
    }

    public HashMultimap<PrismScope, Integer> getResourcesAll() {
        HashMultimap<PrismScope, Integer> target = HashMultimap.create();
        appendResources(resourcesDirect, target);
        appendResources(resourcesIndirect, target);
        return target;
    }

    public Set<Integer> getAdvertsDirect() {
        Set<Integer> target = Sets.newHashSet();
        if (resourcesDirect != null) {
            resourcesDirect.keySet().stream().forEach(scope -> {
                resourcesDirect.get(scope).stream().forEach(resource -> target.add(resource.getAdvertId()));
            });
        }
        return target;
    }

    public UserResourceDTO withResourcesDirect(HashMultimap<PrismScope, ResourceAdvertDTO> resourcesDirect) {
        this.resourcesDirect = resourcesDirect;
        return this;
    }

    public UserResourceDTO withResourcesIndirect(HashMultimap<PrismScope, ResourceAdvertDTO> resourcesIndirect) {
        this.resourcesIndirect = resourcesIndirect;
        return this;
    }

    private void appendResources(HashMultimap<PrismScope, ResourceAdvertDTO> source, HashMultimap<PrismScope, Integer> target) {
        if (source != null) {
            resourcesDirect.keySet().stream().forEach(scope -> {
                resourcesDirect.get(scope).stream().forEach(resource -> target.put(scope, resource.getResourceId()));
            });
        }
    }

}
