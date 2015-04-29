select application_year as applicationYear,
	count(distinct advert_id) as advertCount,
	count(id) as createdApplicationCount,
	sum(if(submitted_timestamp is not null, 1, 0)) as submittedApplicationCount,
	sum(if(state_id like "APPLICATION_APPROVED_%", 1, 0)) as approvedApplicationCount,
	sum(if(state_id like "APPLICATION_REJECTED_%", 1, 0)) as rejectedApplicationCount,
	sum(if(state_id like "APPLICATION_WITHDRAWN%", 1, 0)) as withdrawnApplicationCount,
	round(count(id) / count(distinct advert_id), 2) as createdApplicationRatio,
	round(sum(if(submitted_timestamp is not null, 1, 0)) / count(distinct advert_id), 2) as submittedApplicationRatio,
	round(sum(if(state_id like "APPLICATION_APPROVED_%", 1, 0)) / count(distinct advert_id), 2) as approvedApplicationRatio,
	round(sum(if(state_id like "APPLICATION_REJECTED_%", 1, 0)) / count(distinct advert_id), 2) as rejectedApplicationRatio,
	round(sum(if(state_id like "APPLICATION_WITHDRAWN%", 1, 0)) / count(distinct advert_id), 2) as withdrawnApplicationRatio,
	round(avg(application_rating_average), 2) as averageRating,
	round(avg(datediff(date(submitted_timestamp), date(created_timestamp))), 2) as averagePreparationTime,
	round(avg(datediff(completion_date, date(submitted_timestamp))), 2) as averageProcessingTime
from application
where ${resourceReference}_id = :resourceId
group by application_year desc
order by application_year;
