alter table application
  drop index institution_id_4,
  drop index program_id_4,
  drop index project_id_4,
  drop index institution_id_5,
  drop index program_id_5,
  drop index project_id_5,
  add index (department_id, application_rating_count),
  add index (department_id, application_rating_average),
  add index (department_id, application_year, application_month, application_week),
  add index (institution_id, application_year, application_month, application_week),
  add index (program_id, application_year, application_month, application_week),
  add index (project_id, application_year, application_month, application_week),
  add index (system_id, application_rating_count),
  add index (system_id, application_rating_average),
  add index (system_id, application_year, application_month, application_week)
;

alter table application
  add column business_year_start_month int(2) unsigned not null after submitted_timestamp,
  add index (project_id, application_year, business_year_start_month),
  add index (program_id, application_year, business_year_start_month),
  add index (department_id, application_year, business_year_start_month),
  add index (institution_id, application_year, business_year_start_month),
  add index (system_id, application_year, business_year_start_month)
;

update application
  inner join institution
    on application.institution_id = institution.id
set application.business_year_start_month = institution.business_year_start_month
;

update institution
set business_year_start_month = if(opportunity_category like "STUDY%", 10, 4)
;

update institution
inner join application
    on institution.id = application.institution_id
set application.business_year_start_month = 10
;
