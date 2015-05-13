select application.application_year as applicationYear,
	count(distinct application.advert_id) as advertCount,
	sum(if(application.submitted_timestamp is not null, 1, 0)) as submittedApplicationCount,
	sum(if(application.state_id like "APPLICATION_APPROVED_%", 1, 0)) as approvedApplicationCount,
	sum(if(application.state_id like "APPLICATION_REJECTED_%", 1, 0)) as rejectedApplicationCount,
	sum(if(application.state_id like "APPLICATION_WITHDRAWN%", 1, 0)) as withdrawnApplicationCount,
	round(sum(if(application.submitted_timestamp is not null, 1, 0)) / count(distinct application.advert_id), 2) as submittedApplicationRatio,
	round(sum(if(application.state_id like "APPLICATION_APPROVED_%", 1, 0)) / count(distinct application.advert_id), 2) as approvedApplicationRatio,
	round(sum(if(application.state_id like "APPLICATION_REJECTED_%", 1, 0)) / count(distinct application.advert_id), 2) as rejectedApplicationRatio,
	round(sum(if(application.state_id like "APPLICATION_WITHDRAWN%", 1, 0)) / count(distinct application.advert_id), 2) as withdrawnApplicationRatio,
	round(avg(application.application_rating_average), 2) as averageRating,
	round(avg(datediff(date(application.submitted_timestamp), date(application.created_timestamp))), 2) as averagePreparationTime,
	round(avg(datediff(application.completion_date, date(application.submitted_timestamp))), 2) as averageProcessingTime
from application inner join application_program_detail
	on application.application_program_detail_id = application_program_detail.id
inner join application_personal_detail
	on application.application_personal_detail_id = application_personal_detail.id
${constraintExpression}
group by application.application_year desc
order by application.application_year;
