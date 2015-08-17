alter table project
	add column updated_timestamp_sitemap datetime after updated_timestamp,
	add index (updated_timestamp_sitemap)
;

alter table program
	add column updated_timestamp_sitemap datetime after updated_timestamp,
	add index (updated_timestamp_sitemap)
;

alter table institution
	add column updated_timestamp_sitemap datetime after updated_timestamp,
	add index (updated_timestamp_sitemap)
;

update project
set updated_timestamp_sitemap = updated_timestamp
;

update program
set updated_timestamp_sitemap = updated_timestamp
;

update program inner join (
	select program_id as program_id,
		max(updated_timestamp_sitemap) as updated_timestamp_sitemap
	from project
	group by program_id) as program_project
	on program.id = program_project.program_id
set program.updated_timestamp_sitemap = program_project.updated_timestamp_sitemap
where program_project.updated_timestamp_sitemap > program.updated_timestamp_sitemap
;

update institution inner join (
	select institution_id as institution_id,
		max(updated_timestamp_sitemap) as updated_timestamp_sitemap
	from project
	group by project.institution_id) as institution_project
	on institution.id = institution_project.institution_id
set institution.updated_timestamp_sitemap = institution_project.updated_timestamp_sitemap
;

update institution inner join (
	select institution_id as institution_id,
		max(updated_timestamp_sitemap) as updated_timestamp_sitemap
	from program
	group by program.institution_id) as institution_program
	on institution.id = institution_program.institution_id
set institution.updated_timestamp_sitemap = institution_program.updated_timestamp_sitemap
where institution_program.updated_timestamp_sitemap > institution.updated_timestamp_sitemap
	or institution.updated_timestamp_sitemap is null
;

update institution
set updated_timestamp_sitemap = current_timestamp()
where updated_timestamp_sitemap is null
;

alter table project
	modify column updated_timestamp_sitemap datetime not null
;

alter table program
	modify column updated_timestamp_sitemap datetime not null
;

alter table institution
	modify column updated_timestamp_sitemap datetime not null
;
