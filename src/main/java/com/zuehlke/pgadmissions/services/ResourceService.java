package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.zuehlke.pgadmissions.dao.ResourceDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateDuration;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.dto.ResourceConsoleListRowDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.ProgramDTO;

@Service
@Transactional
public class ResourceService {

    @Autowired
    private ResourceDAO resourceDAO;
    
    @Autowired 
    private RoleService roleService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserService userService;

    public <T extends Resource> List<ResourceConsoleListRowDTO> getConsoleListBlock(Class<T> resourceType, int page, int perPage) {
        return resourceDAO.getConsoleListBlock(userService.getCurrentUser(), resourceType, page, perPage);
    }

    public <T extends Resource> void reassignState(Class<T> resourceClass, State state, State degradationState) {
        resourceDAO.reassignState(resourceClass, state, degradationState);
    }

    public Resource createNewInstitution(System system, User user, InstitutionDTO institutionDTO) {
        InstitutionDomicile domicile = entityService.getByProperty(InstitutionDomicile.class, "id", institutionDTO.getDomicileId());
        InstitutionAddressDTO addressDTO = institutionDTO.getAddress();
        InstitutionAddress address = new InstitutionAddress().withCountry(domicile).withAddressLine1(addressDTO.getAddressLine1())
                .withAddressLine2(addressDTO.getAddressLine2()).withAddressTown(addressDTO.getAddressTown()).withAddressCode(addressDTO.getAddressCode());
        return new Institution().withSystem(system).withUser(user).withDomicile(domicile).withName(institutionDTO.getName())
                .withHomepage(institutionDTO.getHomepage()).withAddress(address);
    }
    
    public Resource createNewProgram(Institution institution, User user, ProgramDTO programDTO) {
        return new Program().withInstitution(institution).withUser(user);
    }

    public Resource createNewApplication(Advert advert, User user) {
        return new Application().withProgram(advert.getProgram()).withProject(advert.getProject()).withUser(user);
    }
    
    public void setTransitionState(Resource resource, State transitionState) {
        resource.setPreviousState(resource.getState());
        resource.setState(transitionState);
    }
    
    public void setDueDate(Resource resource, Comment comment, StateDuration stateDuration) {
        LocalDate dueDate = comment.getUserSpecifiedDueDate();
        if (dueDate == null && comment.getAction().getActionType() == PrismActionType.SYSTEM_ESCALATION) {
            LocalDate dueDateBaseline = resource.getDueDateBaseline();
            dueDate = dueDateBaseline.plusDays(stateDuration == null ? 0 : stateDuration.getDuration());
        }
        resource.setDueDate(dueDate);
    }
    
    public Resource getOperativeResource(Resource resource, Action action) {
        return action.isCreationAction() ? resource.getParentResource() : resource;
    }
    
    public void commitResourceCreation(Resource resource, Action action, Comment comment) {
        resource.setCreatedTimestamp(new DateTime());
        resource.setUpdatedTimestamp(new DateTime());
        entityService.save(resource);
        comment.setRole(roleService.getResourceCreatorRole(resource.getParentResource(), action).getAuthority().toString());
    }

    public void commitResourceUpdate(Resource resource, Action action, Comment comment) {
        if (action.getActionType().isSystemAction()) {
            comment.setRole(PrismRole.SYSTEM_ADMINISTRATOR.toString());
        } else {
            comment.setRole(Joiner.on(", ").join(roleService.getActionOwnerRoles(comment.getUser(), resource, action)));
            if (comment.getDelegateUser() != null) {
                comment.setDelegateRole(Joiner.on(", ").join(roleService.getDelegateActionOwnerRoles(comment.getDelegateUser(), resource, action)));
            }
        }
    }
    
    public void transitionResourceState(Resource resource, Comment comment, State transitionState, StateDuration transitionStateDuration) {
        setTransitionState(resource, transitionState);
        comment.setTransitionState(transitionState);
        setDueDate(resource, comment, transitionStateDuration);
        resource.setUpdatedTimestamp(new DateTime());
    }
    
}
