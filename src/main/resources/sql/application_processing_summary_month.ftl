select application.application_year as applicationYear,
	application.application_month as applicationMonth,
	${columnExpression}
from application left join application_program_detail
	on application.application_program_detail_id = application_program_detail.id
left join application_personal_detail
	on application.application_personal_detail_id = application_personal_detail.id
${constraintExpression}
	and application.submitted_timestamp is not null
group by application.application_year, application.application_month_sequence
order by application.application_year asc, application.application_month_sequence asc;
