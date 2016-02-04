package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_SUPERVISOR_GROUP;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertFilterCategory;
import com.zuehlke.pgadmissions.domain.advert.AdvertTheme;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.AdvertRecommendationDTO;
import com.zuehlke.pgadmissions.rest.dto.OpportunitiesQueryDTO;

@Repository
@SuppressWarnings("unchecked")
public class AdvertDAO {

	@Autowired
	private SessionFactory sessionFactory;

	public Advert getAdvert(PrismScope resourceScope, Integer resourceId) {
		String reference = resourceScope.getLowerCamelName();
		return (Advert) sessionFactory.getCurrentSession().createCriteria(Advert.class)
				.createAlias(reference, reference, JoinType.LEFT_OUTER_JOIN)
				.add(Restrictions.eq(reference + ".id", resourceId)).uniqueResult();
	}

	public List<Integer> getExcludedAdvertsForApplicationRejectionRecommendation(Integer userId,
			String currentApplicationYear, Integer rejectedApplicationId) {
		return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
				.setProjection(Projections.groupProperty("advert.id")) //
				.add(Restrictions.eq("user.id", userId)) //
				.add(Restrictions.disjunction() //
						.add(Restrictions.eq("applicationYear", currentApplicationYear)) //
						.add(Restrictions.ne("id", rejectedApplicationId))) //
				.list();
	}

	public List<Integer> getAdverts(List<PrismState> programStates, List<PrismState> projectStates,
			OpportunitiesQueryDTO queryDTO) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class) //
				.setProjection(Projections.groupProperty("id")) //
				.createAlias("address", "address", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("program", "program", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("program.partner", "programPartner", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("programPartner.advert", "programPartnerAdvert", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("programPartnerAdvert.address", "programPartnerAddress", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("program.resourceStates", "programState", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("program.resourceConditions", "programCondition", JoinType.LEFT_OUTER_JOIN, //
						getResourceConditionConstraint("programCondition")) //
				.createAlias("program.institution", "programInstitution", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("program.department", "programDepartment", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("program.opportunityType", "programOpportunityType", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("program.studyOptions", "programStudyOptions", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("programStudyOptions.studyOption", "programStudyOption", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("project", "project", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("project.partner", "projectPartner", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("projectPartner.advert", "projectPartnerAdvert", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("projectPartnerAdvert.address", "projectPartnerAddress", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("project.resourceStates", "projectState", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("project.resourceConditions", "projectCondition", JoinType.LEFT_OUTER_JOIN, //
						getResourceConditionConstraint("projectCondition")) //
				.createAlias("project.program", "projectProgram", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("project.institution", "projectInstitution", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("project.department", "projectDepartment", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("project.opportunityType", "projectOpportunityType", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("project.studyOptions", "projectStudyOptions", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("projectStudyOptions.studyOption", "projectStudyOption", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("project.userRoles", "projectUserRole", JoinType.LEFT_OUTER_JOIN, //
						Restrictions.in("projectUserRole.role.id", Arrays.asList(PROJECT_SUPERVISOR_GROUP))) //
				.createAlias("projectUserRole.user", "projectUser", JoinType.LEFT_OUTER_JOIN) //
				.add(Restrictions.disjunction() //
						.add(Restrictions.conjunction() //
								.add(Restrictions.isNotNull("program.id")) //
								.add(Restrictions.isNotNull("programCondition.id")) //
								.add(Restrictions.in("programState.state.id", programStates))) //
						.add(Restrictions.conjunction() //
								.add(Restrictions.isNotNull("project.id")) //
								.add(Restrictions.isNotNull("projectCondition.id")) //
								.add(Restrictions.in("projectState.state.id", projectStates))));

		appendExclusionsConstraint(criteria, queryDTO);
		appendLocationConstraint(criteria, queryDTO);
		appendKeywordConstraint(queryDTO, criteria);

		appendOpportunityTypeConstraint(criteria, queryDTO);
		appendStudyOptionConstraint(queryDTO, criteria);
		appendActionConditionConstraint(queryDTO, criteria);

		appendFeeConstraint(criteria, queryDTO);
		appendPayConstraint(criteria, queryDTO);
		appendDurationConstraint(criteria, queryDTO);

		appendInstitutionsConstraint(criteria, queryDTO);
		appendDepartmentsConstraint(queryDTO, criteria);
		appendProgramsConstraint(queryDTO, criteria);
		appendProjectsConstraint(queryDTO, criteria);

		String lastSequenceIdentifier = queryDTO.getLastSequenceIdentifier();
		if (lastSequenceIdentifier != null) {
			criteria.add(Restrictions.lt("sequenceIdentifier", lastSequenceIdentifier));
		}

		return (List<Integer>) criteria.addOrder(Order.desc("sequenceIdentifier")) //
				.setMaxResults(25) //
				.list();
	}

	public List<Advert> getActiveAdverts(List<Integer> adverts, boolean prioritizeProgram) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Advert.class);

		if (prioritizeProgram) {
			criteria.createAlias("program", "program", JoinType.LEFT_OUTER_JOIN);
		}

		criteria.add(Restrictions.in("id", adverts));

		if (prioritizeProgram) {
			criteria.addOrder(Order.desc("program.id"));
		}

		return (List<Advert>) criteria.addOrder(Order.desc("sequenceIdentifier")) //
				.list();
	}

	public List<AdvertRecommendationDTO> getRecommendedAdverts(User user, List<PrismState> activeProgramStates,
			List<PrismState> activeProjectStates, List<Integer> advertsRecentlyAppliedFor) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Application.class, "application") //
				.setProjection(Projections.projectionList() //
						.add(Projections.groupProperty("otherUserApplication.advert"), "advert") //
						.add(Projections.countDistinct("otherUserApplication.user").as("applicationCount"),
								"applicationCount")) //
				.createAlias("advert", "advert", JoinType.INNER_JOIN) //
				.createAlias("advert.applications", "advertApplication", JoinType.INNER_JOIN) //
				.createAlias("advertApplication.user", "otherUser", JoinType.INNER_JOIN) //
				.createAlias("otherUser.applications", "otherUserApplication", JoinType.INNER_JOIN) //
				.createAlias("otherUserApplication.advert", "recommendedAdvert", JoinType.INNER_JOIN) //
				.createAlias("recommendedAdvert.program", "program", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("program.resourceStates", "programState", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("program.resourceConditions", "programCondition", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("recommendedAdvert.project", "project", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("project.resourceStates", "projectState", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("project.resourceConditions", "projectCondition", JoinType.LEFT_OUTER_JOIN) //
				.add(Restrictions.eq("user", user)) //
				.add(Restrictions.ne("advertApplication.user", user)) //
				.add(Restrictions.neProperty("advert", "otherUserApplication.advert")) //
				.add(Restrictions.isNotNull("otherUserApplication.submittedTimestamp")) //
				.add(Restrictions.disjunction() //
						.add(Restrictions.conjunction() //
								.add(Restrictions.isNotNull("program.id")) //
								.add(Restrictions.isNotNull("programCondition.id")) //
								.add(Restrictions.in("programState.state.id", activeProgramStates))) //
						.add(Restrictions.conjunction() //
								.add(Restrictions.isNotNull("project.id")) //
								.add(Restrictions.isNotNull("projectCondition.id")) //
								.add(Restrictions.in("projectState.state.id", activeProjectStates))));

		if (!advertsRecentlyAppliedFor.isEmpty()) {
			criteria.add(Restrictions.not( //
					Restrictions.in("recommendedAdvert.id", advertsRecentlyAppliedFor)));
		}

		return (List<AdvertRecommendationDTO>) criteria.addOrder(Order.desc("applicationCount")) //
				.addOrder(Order.desc("recommendedAdvert.sequenceIdentifier")) //
				.setMaxResults(10) //
				.setResultTransformer(Transformers.aliasToBean(AdvertRecommendationDTO.class)) //
				.list();
	}

	public List<Integer> getAdvertsRecentlyAppliedFor(User user, LocalDate baseline) {
		return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
				.setProjection(Projections.groupProperty("advert.id")) //
				.add(Restrictions.disjunction() //
						.add(Restrictions.isNull("completionDate")) //
						.add(Restrictions.ge("completionDate", baseline))) //
				.add(Restrictions.eq("user", user)) //
				.list();
	}

	public AdvertClosingDate getNextAdvertClosingDate(Advert advert, LocalDate baseline) {
		return (AdvertClosingDate) sessionFactory.getCurrentSession().createCriteria(AdvertClosingDate.class) //
				.add(Restrictions.eq("advert", advert)) //
				.add(Restrictions.ge("closingDate", baseline)) //
				.addOrder(Order.asc("closingDate")) //
				.setMaxResults(1) //
				.uniqueResult();
	}

	public List<Integer> getAdvertsWithElapsedCurrencyConversions(LocalDate baseline,
			List<PrismState> activeProgramStates, List<PrismState> activeProjectStates) {
		return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
				.setProjection(Projections.property("id")) //
				.createAlias("program", "program", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("program.resourceStates", "programState", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("program.resourceConditions", "programCondition", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("project", "project", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("project.resourceStates", "projectState", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("project.resourceConditions", "projectCondition", JoinType.LEFT_OUTER_JOIN) //
				.add(Restrictions.disjunction() //
						.add(Restrictions.conjunction() //
								.add(Restrictions.isNotNull("program.id")) //
								.add(Restrictions.isNotNull("programCondition.id")) //
								.add(Restrictions.in("programState.state.id", activeProgramStates))) //
						.add(Restrictions.conjunction() //
								.add(Restrictions.isNotNull("project.id")) //
								.add(Restrictions.isNotNull("projectCondition.id")) //
								.add(Restrictions.in("projectState.state.id", activeProjectStates)))) //
				.add(Restrictions.lt("lastCurrencyConversionDate", baseline)) //
				.add(Restrictions.disjunction() //
						.add(Restrictions.conjunction() //
								.add(Restrictions.isNotNull("fee.currencySpecified")) //
								.add(Restrictions.isNotNull("fee.currencyAtLocale")) //
								.add(Restrictions.neProperty("fee.currencySpecified", "fee.currencyAtLocale")))
						.add(Restrictions.conjunction() //
								.add(Restrictions.isNotNull("pay.currencySpecified")) //
								.add(Restrictions.isNotNull("pay.currencyAtLocale")) //
								.add(Restrictions.neProperty("pay.currencySpecified", "pay.currencyAtLocale"))))
				.list();
	}

	public List<String> getAdvertTags(Institution institution, Class<? extends AdvertFilterCategory> clazz) {
		String propertyName = clazz.getSimpleName().replace("Advert", "").toLowerCase();
		return (List<String>) sessionFactory.getCurrentSession().createCriteria(clazz) //
				.setProjection(Projections.groupProperty(propertyName)) //
				.createAlias("advert", "advert", JoinType.INNER_JOIN) //
				.createAlias("advert.institution", "institution", JoinType.INNER_JOIN) //
				.createAlias("advert.program", "program", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("advert.project", "project", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("project.program", "projectProgram", JoinType.LEFT_OUTER_JOIN) //
				.add(Restrictions.disjunction() //
						.add(Restrictions.conjunction() //
								.add(Restrictions.isNull("program.institution")) //
								.add(Restrictions.isNull("project.institution")) //
								.add(Restrictions.eqProperty("advert", "institution.advert"))) //
						.add(Restrictions.conjunction() //
								.add(Restrictions.isNull("advert.institution")) //
								.add(Restrictions.isNull("project.institution")) //
								.add(Restrictions.eq("program.institution", institution))) //
						.add(Restrictions.conjunction() //
								.add(Restrictions.isNull("advert.institution")) //
								.add(Restrictions.isNull("program.institution")) //
								.add(Restrictions.eq("projectProgram.institution", institution)))) //
				.list();
	}

	public List<String> getAdvertThemes(Advert advert) {
		return (List<String>) sessionFactory.getCurrentSession().createCriteria(AdvertTheme.class) //
				.setProjection(Projections.property("theme")) //
				.add(Restrictions.eq("advert", advert)) //
				.list();
	}

	public List<Integer> getAdvertsWithElapsedClosingDates(LocalDate baseline) {
		return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
				.setProjection(Projections.property("id")) //
				.createAlias("closingDate", "closingDate", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("closingDates", "otherClosingDate", JoinType.LEFT_OUTER_JOIN) //
				.add(Restrictions.disjunction() //
						.add(Restrictions.lt("closingDate.closingDate", baseline)) //
						.add(Restrictions.conjunction().add(Restrictions.isNull("closingDate.id")) //
								.add(Restrictions.ge("otherClosingDate.closingDate", baseline)))) //
				.list();
	}

	public List<Advert> getAdvertsWithFeesAndPays(Institution institution) {
		String currency = institution.getCurrency();
		return (List<Advert>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
				.createAlias("project", "program", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("program", "project", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("institution", "institution", JoinType.LEFT_OUTER_JOIN) //
				.add(getInstitutionConstraint(institution)) //
				.add(Restrictions.disjunction() //
						.add(Restrictions.eq("fee.currencySpecified", currency)) //
						.add(Restrictions.eq("pay.currencySpecified", currency))) //
				.list();
	}

	public List<Advert> getAdvertsWithSponsorship(Institution institution) {
		return (List<Advert>) sessionFactory.getCurrentSession().createCriteria(Advert.class) //
				.createAlias("project", "program", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("program", "project", JoinType.LEFT_OUTER_JOIN) //
				.createAlias("institution", "institution", JoinType.LEFT_OUTER_JOIN) //
				.add(getInstitutionConstraint(institution)) //
				.add(Restrictions.disjunction() //
						.add(Restrictions.gt("sponsorshipTarget", new BigDecimal(0.00))) //
						.add(Restrictions.gt("sponsorshipSecured", new BigDecimal(0.00)))) //
				.list();
	}

	private Junction getResourceConditionConstraint(String tableReference) {
		return Restrictions.disjunction() //
				.add(Restrictions.eq(tableReference + ".partnerMode", true)) //
				.add(Restrictions.eq(tableReference + ".actionCondition", ACCEPT_APPLICATION));
	}

	private void appendExclusionsConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
		List<Integer> exclusions = queryDTO.getExclusions();
		if (isNotEmpty(exclusions)) {
			criteria.add(Restrictions.not( //
					Restrictions.in("id", exclusions)));
		}
	}

	private void appendLocationConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
		if (queryDTO.getNeLat() != null) {
			criteria.add(Restrictions.disjunction() //
					.add(Restrictions.conjunction() //
							.add(Restrictions.between("address.location.locationX", queryDTO.getSwLat(),
									queryDTO.getNeLat())) //
							.add(Restrictions.between("address.location.locationY", queryDTO.getSwLon(),
									queryDTO.getNeLon()))) //
					.add(Restrictions.conjunction() //
							.add(Restrictions.between("programPartnerAddress.location.locationX", queryDTO.getSwLat(),
									queryDTO.getNeLat())) //
							.add(Restrictions.between("programPartnerAddress.location.locationY", queryDTO.getSwLon(),
									queryDTO.getNeLon())))
					.add(Restrictions.conjunction() //
							.add(Restrictions.between("projectPartnerAddress.location.locationX", queryDTO.getSwLat(),
									queryDTO.getNeLat())) //
							.add(Restrictions.between("projectPartnerAddress.location.locationY", queryDTO.getSwLon(),
									queryDTO.getNeLon()))));
		}
	}

	private void appendKeywordConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
		String keyword = queryDTO.getKeyword();
		if (keyword != null) {
			criteria.add(Restrictions.disjunction() //
					.add(Restrictions.ilike("title", keyword, MatchMode.ANYWHERE)) //
					.add(Restrictions.ilike("summary", keyword, MatchMode.ANYWHERE)) //
					.add(Restrictions.ilike("description", keyword, MatchMode.ANYWHERE)) //
					.add(Restrictions.ilike("program.title", keyword, MatchMode.ANYWHERE)) //
					.add(Restrictions.ilike("programPartner.title", keyword, MatchMode.ANYWHERE)) //
					.add(Restrictions.ilike("project.title", keyword, MatchMode.ANYWHERE)) //
					.add(Restrictions.ilike("projectPartner.title", keyword, MatchMode.ANYWHERE)) //
					.add(Restrictions.ilike("programDepartment.title", keyword, MatchMode.ANYWHERE)) //
					.add(Restrictions.ilike("programInstitution.title", keyword, MatchMode.ANYWHERE)) //
					.add(Restrictions.ilike("projectProgram.title", keyword, MatchMode.ANYWHERE)) //
					.add(Restrictions.ilike("projectDepartment.title", keyword, MatchMode.ANYWHERE)) //
					.add(Restrictions.ilike("projectInstitution.title", keyword, MatchMode.ANYWHERE)) //
					.add(Restrictions.ilike("projectUser.firstName", keyword, MatchMode.ANYWHERE)) //
					.add(Restrictions.ilike("projectUser.lastName", keyword, MatchMode.ANYWHERE)) //
					.add(Restrictions.ilike("projectUser.email", keyword, MatchMode.ANYWHERE))); //
		}
	}

	private void appendOpportunityTypeConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
		Collection<PrismOpportunityType> opportunityTypes = queryDTO.getOpportunityTypes();
		if (opportunityTypes == null) {
			opportunityTypes = Lists.newLinkedList();
			if (queryDTO.getProgramCategories() != null) {
				for (PrismOpportunityCategory category : queryDTO.getProgramCategories()) {
					for (PrismOpportunityType opportunityType : PrismOpportunityType.getOpportunityTypes(category)) {
						opportunityTypes.add(opportunityType);
					}
				}
			}
		}

		Disjunction opportunityTypeConstraint = Restrictions.disjunction();
		for (PrismOpportunityType opportunityType : opportunityTypes) {
			String opportunityTypeReference = opportunityType.name();
			opportunityTypeConstraint //
					.add(Restrictions.eq("programOpportunityType.code", opportunityTypeReference)) //
					.add(Restrictions.eq("projectOpportunityType.code", opportunityTypeReference));

		}
		criteria.add(opportunityTypeConstraint);
	}

	private void appendStudyOptionConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
		List<PrismStudyOption> studyOptions = queryDTO.getStudyOptions();
		if (CollectionUtils.isNotEmpty(studyOptions)) {
			Disjunction studyOptionConstraint = Restrictions.disjunction();
			for (PrismStudyOption studyOption : studyOptions) {
				studyOptionConstraint //
						.add(Restrictions.eq("programStudyOption.code", studyOption.name())) //
						.add(Restrictions.eq("projectStudyOption.code", studyOption.name())); //
			}
			criteria.add(studyOptionConstraint);
		}
	}

	private void appendActionConditionConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
		List<PrismActionCondition> actionConditions = queryDTO.getActionConditions();
		if (CollectionUtils.isNotEmpty(actionConditions)) {
			Disjunction actionConditionConstraint = Restrictions.disjunction();
			for (PrismActionCondition actionCondition : actionConditions) {
				actionConditionConstraint //
						.add(Restrictions.eq("programCondition.actionCondition", actionCondition)) //
						.add(Restrictions.eq("projectCondition.actionCondition", actionCondition)); //
			}
			criteria.add(actionConditionConstraint);
		}

	}

	private void appendFeeConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
		appendRangeConstraint(criteria, "fee.monthMinimumAtLocale", "fee.monthMaximumAtLocale", queryDTO.getMinFee(),
				queryDTO.getMaxFee(), true);
	}

	private void appendPayConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
		appendRangeConstraint(criteria, "pay.monthMinimumAtLocale", "pay.monthMaximumAtLocale", queryDTO.getMinSalary(),
				queryDTO.getMaxSalary(), true);
	}

	private void appendDurationConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
		Junction disjunction = Restrictions.disjunction();
		appendRangeConstraint(disjunction, "program.durationMinimum", "program.durationMaximum",
				queryDTO.getMinDuration(), queryDTO.getMaxDuration(), false);
		appendRangeConstraint(disjunction, "project.durationMinimum", "project.durationMaximum",
				queryDTO.getMinDuration(), queryDTO.getMaxDuration(), false);
		criteria.add(disjunction);
	}

	private void appendInstitutionsConstraint(Criteria criteria, OpportunitiesQueryDTO queryDTO) {
		Integer[] institutions = queryDTO.getInstitutions();
		if (institutions != null) {
			criteria.add(Restrictions.disjunction() //
					.add(Restrictions.in("programInstitution.id", institutions)) //
					.add(Restrictions.in("programPartner.id", institutions)) //
					.add(Restrictions.in("projectInstitution.id", institutions)) //
					.add(Restrictions.in("projectPartner.id", institutions))); //
		}
	}

	private void appendDepartmentsConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
		Integer[] departments = queryDTO.getDepartments();
		if (departments != null) {
			criteria.add(Restrictions.disjunction() //
					.add(Restrictions.in("programDepartment.id", departments)) //
					.add(Restrictions.in("projectDepartment.id", departments)));
		}
	}

	private void appendProgramsConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
		Integer[] programs = queryDTO.getPrograms();
		if (programs != null) {
			criteria.add(Restrictions.disjunction() //
					.add(Restrictions.in("program.id", programs)) //
					.add(Restrictions.in("projectProgram.id", programs)));
		}
	}

	private void appendProjectsConstraint(OpportunitiesQueryDTO queryDTO, Criteria criteria) {
		Integer[] projects = queryDTO.getProjects();
		if (projects != null) {
			criteria.add(Restrictions.in("project.id", projects));
		}
	}

	private void appendRangeConstraint(Criteria criteria, String loColumn, String hiColumn, Integer loValue,
			Integer hiValue, boolean decimal) {
		Junction constraint = buildRangeConstraint(loColumn, hiColumn, loValue, hiValue, decimal);
		if (constraint != null) {
			criteria.add(constraint);
		}
	}

	private void appendRangeConstraint(Junction junction, String loColumn, String hiColumn, Integer loValue,
			Integer hiValue, boolean decimal) {
		Junction constraint = buildRangeConstraint(loColumn, hiColumn, loValue, hiValue, decimal);
		if (constraint != null) {
			junction.add(constraint);
		}
	}

	private Junction buildRangeConstraint(String loColumn, String hiColumn, Integer loValue, Integer hiValue,
			boolean decimal) {
		if ((loValue == null || loValue == 0) && (hiValue == null || hiValue == 0)) {
			return null;
		}

		Junction conjunction = Restrictions.conjunction();
		if (loValue != null) {
			conjunction.add(Restrictions.ge(loColumn,
					hiValue != null && hiValue < loValue ? hiValue : decimal ? new BigDecimal(loValue) : loValue));
		}

		if (hiValue != null) {
			conjunction.add(Restrictions.le(hiColumn,
					loValue != null && loValue > hiValue ? loValue : decimal ? new BigDecimal(hiValue) : hiValue));
		}
		return conjunction;
	}

	private Junction getInstitutionConstraint(Institution institution) {
		return Restrictions.disjunction() //
				.add(Restrictions.conjunction() //
						.add(Restrictions.isNull("project.partner")) //
						.add(Restrictions.isNull("program.partner")) //
						.add(Restrictions.disjunction() //
								.add(Restrictions.eq("project.institution", institution)) //
								.add(Restrictions.eq("program.institution", institution)) //
								.add(Restrictions.eq("institution.id", institution.getId())))) //
				.add(Restrictions.eq("project.partner", institution)) //
				.add(Restrictions.eq("program.partner", institution));
	}

}
