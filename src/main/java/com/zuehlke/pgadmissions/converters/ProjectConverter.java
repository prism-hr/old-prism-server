package com.zuehlke.pgadmissions.converters;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.ProjectDTO;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class ProjectConverter {
	private final UserService userService;
	private ProgramsService programService;

	@Autowired
	public ProjectConverter(UserService userService,ProgramsService programService) {
		this.userService = userService;
		this.programService = programService;
	}

	public Project toDomainObject(ProjectDTO projectAdvertDTO) {
		if(projectAdvertDTO==null){
			return null;
		}
		Project project = projectAdvertDTO.getId() == null ?  new Project() : programService.getProject(projectAdvertDTO.getId());
		Advert advert = project.getAdvert()==null ? new Advert() : project.getAdvert();
		project.setAdvert(advert);
		updateProjectFromDTO(project, projectAdvertDTO);
		updateProjectAdvert(advert, projectAdvertDTO);
		return project;
	}

	private void updateProjectFromDTO(Project project, ProjectDTO projectAdvertDTO) {
		if (projectAdvertDTO.getClosingDateSpecified()) {
			project.setClosingDate(projectAdvertDTO.getClosingDate());
		}
		project.setProgram(projectAdvertDTO.getProgram());
		
		RegisteredUser primarySupervisor = loadSupervisor(projectAdvertDTO.getPrimarySupervisor());
		project.setPrimarySupervisor(primarySupervisor);
		
		RegisteredUser secondarySupervisor = loadSupervisor(projectAdvertDTO.getSecondarySupervisor());
		project.setSecondarySupervisor(secondarySupervisor);
	}

	private RegisteredUser loadSupervisor(Person person) {
		if(person == null || StringUtils.isBlank(person.getEmail())){
			return null;
		}
		return userService.getUserByEmailIncludingDisabledAccounts(person.getEmail());
	}

	private void updateProjectAdvert(Advert advert, ProjectDTO projectAdvertDTO) {
		advert.setTitle(projectAdvertDTO.getTitle());
	    advert.setDescription(projectAdvertDTO.getDescription());
	    advert.setStudyDuration(projectAdvertDTO.getStudyDuration());
	    advert.setFunding(projectAdvertDTO.getFunding());
	    advert.setActive(projectAdvertDTO.getActive());
	}

}