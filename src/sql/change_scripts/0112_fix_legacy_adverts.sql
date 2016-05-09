update advert inner join project
	on advert.project_id = project.id
set advert.pay_option = "COMPETITIVE_SALARY"
where project.state_id in ("PROJECT_APPROVED", "PROJECT_DISABLED_COMPLETED")
	and advert.pay_option is null
;
