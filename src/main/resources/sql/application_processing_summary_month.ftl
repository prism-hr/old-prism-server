select application_year as applicationYear,
	application_month as applicationMonth,
	${columnExpression}
from application inner join application_program_detail
	on application.application_program_detail_id = application_program_detail.id
inner join application_personal_detail
	on application.application_personal_detail_id = application_personal_detail.id
${constraintExpression}
group by application.application_year, application.application_month_sequence
order by application.application_year desc, application.application_month_sequence desc;
