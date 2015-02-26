package com.zuehlke.pgadmissions.domain.advert;

import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourcePreviousState;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.State;

public class ClosingDate extends Resource {

	@Override
	public ResourceSignature getResourceSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(Integer id) {
		// TODO Auto-generated method stub

	}

	@Override
	public User getUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUser(User user) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCode(String code) {
		// TODO Auto-generated method stub

	}

	@Override
	public PrismLocale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public System getSystem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSystem(System system) {
		// TODO Auto-generated method stub

	}

	@Override
	public Institution getInstitution() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInstitution(Institution institution) {
		// TODO Auto-generated method stub

	}

	@Override
	public Program getProgram() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProgram(Program program) {
		// TODO Auto-generated method stub

	}

	@Override
	public Project getProject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProject(Project project) {
		// TODO Auto-generated method stub

	}

	@Override
	public Application getApplication() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReferrer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setReferrer(String referrer) {
		// TODO Auto-generated method stub

	}

	@Override
	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(State state) {
		// TODO Auto-generated method stub

	}

	@Override
	public State getPreviousState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPreviousState(State previousState) {
		// TODO Auto-generated method stub

	}

	@Override
	public LocalDate getDueDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDueDate(LocalDate dueDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public DateTime getCreatedTimestamp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCreatedTimestamp(DateTime createdTimestamp) {
		// TODO Auto-generated method stub

	}

	@Override
	public DateTime getUpdatedTimestamp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUpdatedTimestamp(DateTime updatedTimestamp) {
		// TODO Auto-generated method stub

	}

	@Override
	public LocalDate getLastRemindedRequestIndividual() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastRemindedRequestIndividual(LocalDate lastRemindedRequestIndividual) {
		// TODO Auto-generated method stub

	}

	@Override
	public LocalDate getLastRemindedRequestSyndicated() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastRemindedRequestSyndicated(LocalDate lastRemindedRequestSyndicated) {
		// TODO Auto-generated method stub

	}

	@Override
	public LocalDate getLastNotifiedUpdateSyndicated() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastNotifiedUpdateSyndicated(LocalDate lastNotifiedUpdateSyndicated) {
		// TODO Auto-generated method stub

	}

	@Override
	public Integer getWorkflowPropertyConfigurationVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setWorkflowPropertyConfigurationVersion(Integer workflowResourceConfigurationVersion) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSequenceIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSequenceIdentifier(String sequenceIdentifier) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<ResourceState> getResourceStates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ResourcePreviousState> getResourcePreviousStates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Comment> getComments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<UserRole> getUserRoles() {
		// TODO Auto-generated method stub
		return null;
	}

}
