select application.application_year as applicationYear,
	${columnExpression}
from application inner join application_program_detail
	on application.application_program_detail_id = application_program_detail.id
inner join application_personal_detail
	on application.application_personal_detail_id = application_personal_detail.id
${constraintExpression}
group by application.application_year desc
order by application.application_year;
