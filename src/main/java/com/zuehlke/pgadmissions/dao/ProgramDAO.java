package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_WITHDRAWN;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_DEACTIVATED;

import java.util.Arrays;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.program.ResourceStudyLocation;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.ResourceSearchEngineDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ProgramRepresentation;

@Repository
@SuppressWarnings("unchecked")
public class ProgramDAO {

	@Autowired
	private SessionFactory sessionFactory;

	public Program getProgramByCode(String code) {
		return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class) //
		        .setFetchMode("studyOptions", FetchMode.JOIN) //
		        .add(Restrictions.eq("code", code)) //
		        .uniqueResult();
	}

	public Program getProgramByImportedCode(Institution institution, String importedCode) {
		return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class) //
		        .setFetchMode("studyOptions", FetchMode.JOIN) //
		        .add(Restrictions.eq("institution", institution)) //
		        .add(Restrictions.eq("importedCode", importedCode)) //
		        .uniqueResult();
	}

	public List<Program> getPrograms() {
		return sessionFactory.getCurrentSession().createCriteria(Program.class) //
		        .list();
	}

	public List<ProgramRepresentation> getSimilarPrograms(Integer institutionId, String searchTerm) {
		return (List<ProgramRepresentation>) sessionFactory.getCurrentSession().createCriteria(Program.class, "program") //
		        .setProjection(Projections.projectionList() //
		                .add(Projections.property("id"), "id") //
		                .add(Projections.property("title"), "title")) //
		        .add(Restrictions.eq("institution.id", institutionId)) //
		        .add(Restrictions.not( //
		                Restrictions.in("state.id", Arrays.asList(PROGRAM_REJECTED, PROGRAM_WITHDRAWN, PROGRAM_DISABLED_COMPLETED)))) //
		        .add(Restrictions.ilike("title", searchTerm, MatchMode.ANYWHERE)) //
		        .addOrder(Order.desc("title")) //
		        .list();
	}

	public State getPreviousState(Program program) {
		return (State) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
		        .setProjection(Projections.property("state")) //
		        .add(Restrictions.eq("program", program)) // /
		        .add(Restrictions.isNotNull("state")) //
		        .add(Restrictions.ne("state", program.getState())) //
		        .add(Restrictions.in("state.id", Arrays.asList(PROGRAM_APPROVED, PROJECT_DEACTIVATED))) //
		        .addOrder(Order.desc("createdTimestamp")) //
		        .addOrder(Order.desc("id")) //
		        .setMaxResults(1) //
		        .uniqueResult();
	}

	public List<String> getSuggestedDivisions(Program program, String location) {
		return (List<String>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
		        .setProjection(Projections.groupProperty("studyDetail.studyDivision")) //
		        .add(Restrictions.eq("program", program)) //
		        .add(Restrictions.eq("studyDetail.studyLocation", location)) //
		        .add(Subqueries.in(location, DetachedCriteria.forClass(ResourceStudyLocation.class) //
		                .setProjection(Projections.property("location")) //
		                .add(Restrictions.eq("program", program)))) //
		        .list();
	}

	public List<String> getSuggestedStudyAreas(Program program, String location, String division) {
		return (List<String>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
		        .setProjection(Projections.groupProperty("studyDetail.studyArea")) //
		        .add(Restrictions.eq("program", program)) //
		        .add(Restrictions.eq("studyDetail.studyLocation", location)) //
		        .add(Restrictions.eq("studyDetail.studyDivision", division)) //
		        .add(Subqueries.in(location, DetachedCriteria.forClass(ResourceStudyLocation.class) //
		                .setProjection(Projections.property("location")) //
		                .add(Restrictions.eq("program", program)))) //
		        .list();
	}

	public Long getActiveProgramCount(Institution institution) {
		return (Long) sessionFactory.getCurrentSession().createCriteria(Program.class) //
		        .setProjection(Projections.countDistinct("id")) //
		        .createAlias("institution", "institution", JoinType.INNER_JOIN) //
		        .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
		        .createAlias("resourceState.state", "state", JoinType.INNER_JOIN) //
		        .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
		        .add(Restrictions.eq("institution", institution)) //
		        .add(Restrictions.eq("stateAction.action.id", PROGRAM_CREATE_APPLICATION)) //
		        .uniqueResult();
	}

	public DateTime getLatestUpdatedTimestampSitemap(List<PrismState> states) {
		return (DateTime) sessionFactory.getCurrentSession().createCriteria(Program.class) //
		        .setProjection(Projections.property("updatedTimestampSitemap")) //
		        .add(Restrictions.in("state.id", states)) //
		        .addOrder(Order.desc("updatedTimestampSitemap")) //
		        .setMaxResults(1) //
		        .uniqueResult();
	}

	public List<SitemapEntryDTO> getSitemapEntries(List<PrismState> states) {
		return (List<SitemapEntryDTO>) sessionFactory.getCurrentSession().createCriteria(Program.class, "program") //
		        .setProjection(Projections.projectionList() //
		                .add(Projections.property("id"), "resourceId") //
		                .add(Projections.property("updatedTimestampSitemap"), "lastModifiedTimestamp")) //
		        .add(Restrictions.in("state.id", states)) //
		        .addOrder(Order.desc("updatedTimestampSitemap")) //
		        .setMaxResults(50000) //
		        .setResultTransformer(Transformers.aliasToBean(SitemapEntryDTO.class)) //
		        .list();
	}

	public SearchEngineAdvertDTO getSearchEngineAdvert(Integer programId, List<PrismState> states) {
		return (SearchEngineAdvertDTO) sessionFactory.getCurrentSession().createCriteria(Program.class, "program") //
		        .setProjection(Projections.projectionList() //
		                .add(Projections.property("id"), "programId") //
		                .add(Projections.property("advert.title"), "programTitle") //
		                .add(Projections.property("advert.summary"), "programSummary") //
		                .add(Projections.property("advert.description"), "programDescription") //
		                .add(Projections.property("institution.id"), "institutionId") //
		                .add(Projections.property("institution.title"), "institutionTitle") //
		                .add(Projections.property("institution.summary"), "institutionSummary") //
		                .add(Projections.property("institution.homepage"), "institutionHomepage")) //
		        .createAlias("advert", "advert", JoinType.INNER_JOIN) //
		        .createAlias("institution", "institution", JoinType.INNER_JOIN) //
		        .add(Restrictions.eq("id", programId)) //
		        .add(Restrictions.in("state.id", states)) //
		        .setResultTransformer(Transformers.aliasToBean(SearchEngineAdvertDTO.class)) //
		        .uniqueResult();
	}

	public List<ResourceSearchEngineDTO> getActiveProgramsByInstitution(Integer institutionId, List<PrismState> states) {
		return (List<ResourceSearchEngineDTO>) sessionFactory.getCurrentSession().createCriteria(Program.class, "program") //
		        .setProjection(Projections.projectionList() //
		                .add(Projections.property("id"), "id") //
		                .add(Projections.property("title"), "title")) //
		        .add(Restrictions.eq("institution.id", institutionId)) //
		        .add(Restrictions.in("state.id", states)) //
		        .addOrder(Order.desc("updatedTimestampSitemap")) //
		        .setResultTransformer(Transformers.aliasToBean(ResourceSearchEngineDTO.class)) //
		        .list();
	}
	
	public List<Integer> getProjects(Integer program) {
		return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Project.class) //
				.setProjection(Projections.property("id")) //
				.add(Restrictions.eq("program.id", program)) //
				.list();
	}
	
	public List<Integer> getApplications(Integer program) {
		return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
				.setProjection(Projections.property("id")) //
				.add(Restrictions.eq("program.id", program)) //
				.list();
	}

}
