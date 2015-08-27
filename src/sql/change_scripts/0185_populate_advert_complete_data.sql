update institution inner join advert
	on institution.advert_id = advert.id
set institution.advert_incomplete_section = "SYSTEM_RESOURCE_SUMMARY_HEADER"
where institution.logo_image_id is null
	or advert.background_image_id is null
	or advert.summary is null
	or advert.telephone is null
;

update institution inner join advert
	on institution.advert_id = advert.id
set institution.advert_incomplete_section = concat(institution.advert_incomplete_section, "|SYSTEM_RESOURCE_ADVERT_HEADER")
where institution.advert_incomplete_section is not null
	and advert.description is null
;

update institution inner join advert
	on institution.advert_id = advert.id
set institution.advert_incomplete_section = "SYSTEM_RESOURCE_ADVERT_HEADER"
where institution.advert_incomplete_section is null
	and advert.description is null
;

update institution inner join advert
	on institution.advert_id = advert.id
left join advert_function 
	on advert.id = advert_function.advert_id
left join advert_industry
	on advert.id = advert_industry.advert_id
left join advert_theme
	on advert.id = advert_theme.advert_id
set institution.advert_incomplete_section = concat(institution.advert_incomplete_section, "|SYSTEM_RESOURCE_ADVERT_CATEGORIES_HEADER")
where institution.advert_incomplete_section is not null
	and advert_function.id is null
	and advert_industry.id is null
	and advert_theme.id is null
;

update institution inner join advert
	on institution.advert_id = advert.id
left join advert_function 
	on advert.id = advert_function.advert_id
left join advert_industry
	on advert.id = advert_industry.advert_id
left join advert_theme
	on advert.id = advert_theme.advert_id
set institution.advert_incomplete_section = "SYSTEM_RESOURCE_ADVERT_CATEGORIES_HEADER"
where institution.advert_incomplete_section is null
	and advert_function.id is null
	and advert_industry.id is null
	and advert_theme.id is null
;

update institution inner join advert
	on institution.advert_id = advert.id
left join advert_institution 
	on advert.id = advert_institution.advert_id
left join advert_department
	on advert.id = advert_department.advert_id
set institution.advert_incomplete_section = concat(institution.advert_incomplete_section, "|SYSTEM_RESOURCE_TARGETS_HEADER")
where institution.advert_incomplete_section is not null
	and advert_institution.id is null
	and advert_department.id is null
;

update institution inner join advert
	on institution.advert_id = advert.id
left join advert_institution 
	on advert.id = advert_institution.advert_id
left join advert_department
	on advert.id = advert_department.advert_id
set institution.advert_incomplete_section = "SYSTEM_RESOURCE_TARGETS_HEADER"
where institution.advert_incomplete_section is null
	and advert_institution.id is null
	and advert_department.id is null
;

update institution inner join advert
	on institution.advert_id = advert.id
left join advert_competence 
	on advert.id = advert_competence.advert_id
set institution.advert_incomplete_section = concat(institution.advert_incomplete_section, "|SYSTEM_RESOURCE_COMPETENCES_HEADER")
where institution.advert_incomplete_section is not null
	and advert_competence.id is null
;

update institution inner join advert
	on institution.advert_id = advert.id
left join advert_competence 
	on advert.id = advert_competence.advert_id
set institution.advert_incomplete_section = "SYSTEM_RESOURCE_COMPETENCES_HEADER"
where institution.advert_incomplete_section is null
	and advert_competence.id is null
;

update department inner join advert
	on department.advert_id = advert.id
inner join institution
	on department.institution_id = institution.id
inner join advert as institution_advert
	on institution.advert_id = institution_advert.id
set department.advert_incomplete_section = "SYSTEM_RESOURCE_SUMMARY_HEADER"
where advert.background_image_id is null
	and institution_advert.background_image_id is null
	or advert.summary is null
	or advert.telephone is null
;

update department inner join advert
	on department.advert_id = advert.id
set department.advert_incomplete_section = concat(department.advert_incomplete_section, "|SYSTEM_RESOURCE_ADVERT_HEADER")
where department.advert_incomplete_section is not null
	and advert.description is null
;

update department inner join advert
	on department.advert_id = advert.id
set department.advert_incomplete_section = "SYSTEM_RESOURCE_ADVERT_HEADER"
where department.advert_incomplete_section is null
	and advert.description is null
;

update department inner join advert
	on department.advert_id = advert.id
left join advert_function 
	on advert.id = advert_function.advert_id
left join advert_industry
	on advert.id = advert_industry.advert_id
left join advert_theme
	on advert.id = advert_theme.advert_id
set department.advert_incomplete_section = concat(department.advert_incomplete_section, "|SYSTEM_RESOURCE_ADVERT_CATEGORIES_HEADER")
where department.advert_incomplete_section is not null
	and advert_function.id is null
	and advert_industry.id is null
	and advert_theme.id is null
;

update department inner join advert
	on department.advert_id = advert.id
left join advert_function 
	on advert.id = advert_function.advert_id
left join advert_industry
	on advert.id = advert_industry.advert_id
left join advert_theme
	on advert.id = advert_theme.advert_id
set department.advert_incomplete_section = "SYSTEM_RESOURCE_ADVERT_CATEGORIES_HEADER"
where department.advert_incomplete_section is null
	and advert_function.id is null
	and advert_industry.id is null
	and advert_theme.id is null
;

update department inner join advert
	on department.advert_id = advert.id
left join advert_institution 
	on advert.id = advert_institution.advert_id
left join advert_department
	on advert.id = advert_department.advert_id
set department.advert_incomplete_section = concat(department.advert_incomplete_section, "|SYSTEM_RESOURCE_TARGETS_HEADER")
where department.advert_incomplete_section is not null
	and advert_institution.id is null
	and advert_department.id is null
;

update department inner join advert
	on department.advert_id = advert.id
left join advert_institution 
	on advert.id = advert_institution.advert_id
left join advert_department
	on advert.id = advert_department.advert_id
set department.advert_incomplete_section = "SYSTEM_RESOURCE_TARGETS_HEADER"
where department.advert_incomplete_section is null
	and advert_institution.id is null
	and advert_department.id is null
;

update department inner join advert
	on department.advert_id = advert.id
left join advert_competence 
	on advert.id = advert_competence.advert_id
set department.advert_incomplete_section = concat(department.advert_incomplete_section, "|SYSTEM_RESOURCE_COMPETENCES_HEADER")
where department.advert_incomplete_section is not null
	and advert_competence.id is null
;

update department inner join advert
	on department.advert_id = advert.id
left join advert_competence 
	on advert.id = advert_competence.advert_id
set department.advert_incomplete_section = "SYSTEM_RESOURCE_COMPETENCES_HEADER"
where department.advert_incomplete_section is null
	and advert_competence.id is null
;

update program inner join advert
	on program.advert_id = advert.id
inner join institution
	on program.institution_id = institution.id
inner join advert as institution_advert
	on institution.advert_id = institution_advert.id
left join department
	on program.department_id = department.id
left join advert as department_advert
	on department.advert_id = department_advert.id
set program.advert_incomplete_section = "SYSTEM_RESOURCE_SUMMARY_HEADER"
where advert.background_image_id is null
	and institution_advert.background_image_id is null
	and department_advert.background_image_id is null
	or advert.summary is null
	or advert.telephone is null
;

update program inner join advert
	on program.advert_id = advert.id
set program.advert_incomplete_section = concat(program.advert_incomplete_section, "|SYSTEM_RESOURCE_ADVERT_HEADER")
where program.advert_incomplete_section is not null
	and advert.description is null
;

update program inner join advert
	on program.advert_id = advert.id
set program.advert_incomplete_section = "SYSTEM_RESOURCE_ADVERT_HEADER"
where program.advert_incomplete_section is null
	and advert.description is null
;

update program inner join advert
	on program.advert_id = advert.id
left join advert_function 
	on advert.id = advert_function.advert_id
left join advert_industry
	on advert.id = advert_industry.advert_id
left join advert_theme
	on advert.id = advert_theme.advert_id
set program.advert_incomplete_section = concat(program.advert_incomplete_section, "|SYSTEM_RESOURCE_ADVERT_CATEGORIES_HEADER")
where program.advert_incomplete_section is not null
	and advert_function.id is null
	and advert_industry.id is null
	and advert_theme.id is null
;

update program inner join advert
	on program.advert_id = advert.id
left join advert_function 
	on advert.id = advert_function.advert_id
left join advert_industry
	on advert.id = advert_industry.advert_id
left join advert_theme
	on advert.id = advert_theme.advert_id
set program.advert_incomplete_section = "SYSTEM_RESOURCE_ADVERT_CATEGORIES_HEADER"
where program.advert_incomplete_section is null
	and advert_function.id is null
	and advert_industry.id is null
	and advert_theme.id is null
;

update program inner join advert
	on program.advert_id = advert.id
left join advert_institution 
	on advert.id = advert_institution.advert_id
left join advert_department
	on advert.id = advert_department.advert_id
set program.advert_incomplete_section = concat(program.advert_incomplete_section, "|SYSTEM_RESOURCE_TARGETS_HEADER")
where program.advert_incomplete_section is not null
	and advert_institution.id is null
	and advert_department.id is null
;

update program inner join advert
	on program.advert_id = advert.id
left join advert_institution 
	on advert.id = advert_institution.advert_id
left join advert_department
	on advert.id = advert_department.advert_id
set program.advert_incomplete_section = "SYSTEM_RESOURCE_TARGETS_HEADER"
where program.advert_incomplete_section is null
	and advert_institution.id is null
	and advert_department.id is null
;

update program inner join advert
	on program.advert_id = advert.id
left join advert_competence 
	on advert.id = advert_competence.advert_id
set program.advert_incomplete_section = concat(program.advert_incomplete_section, "|SYSTEM_RESOURCE_COMPETENCES_HEADER")
where program.advert_incomplete_section is not null
	and advert_competence.id is null
;

update program inner join advert
	on program.advert_id = advert.id
left join advert_competence 
	on advert.id = advert_competence.advert_id
set program.advert_incomplete_section = "SYSTEM_RESOURCE_COMPETENCES_HEADER"
where program.advert_incomplete_section is null
	and advert_competence.id is null
;

update project inner join advert
	on project.advert_id = advert.id
inner join institution
	on project.institution_id = institution.id
inner join advert as institution_advert
	on institution.advert_id = institution_advert.id
left join department
	on project.department_id = department.id
left join advert as department_advert
	on department.advert_id = department_advert.id
left join program
	on project.program_id = program.id
left join advert as program_advert
	on program.advert_id = program_advert.id
set project.advert_incomplete_section = "SYSTEM_RESOURCE_SUMMARY_HEADER"
where advert.background_image_id is null
	and institution_advert.background_image_id is null
	and department_advert.background_image_id is null
	and program_advert.background_image_id is null
	or advert.summary is null
	or advert.telephone is null
;

update project inner join advert
	on project.advert_id = advert.id
set project.advert_incomplete_section = concat(project.advert_incomplete_section, "|SYSTEM_RESOURCE_ADVERT_HEADER")
where project.advert_incomplete_section is not null
	and advert.description is null
;

update project inner join advert
	on project.advert_id = advert.id
set project.advert_incomplete_section = "SYSTEM_RESOURCE_ADVERT_HEADER"
where project.advert_incomplete_section is null
	and advert.description is null
;

update project inner join advert
	on project.advert_id = advert.id
left join advert_function 
	on advert.id = advert_function.advert_id
left join advert_industry
	on advert.id = advert_industry.advert_id
left join advert_theme
	on advert.id = advert_theme.advert_id
set project.advert_incomplete_section = concat(project.advert_incomplete_section, "|SYSTEM_RESOURCE_ADVERT_CATEGORIES_HEADER")
where project.advert_incomplete_section is not null
	and advert_function.id is null
	and advert_industry.id is null
	and advert_theme.id is null
;

update project inner join advert
	on project.advert_id = advert.id
left join advert_function 
	on advert.id = advert_function.advert_id
left join advert_industry
	on advert.id = advert_industry.advert_id
left join advert_theme
	on advert.id = advert_theme.advert_id
set project.advert_incomplete_section = "SYSTEM_RESOURCE_ADVERT_CATEGORIES_HEADER"
where project.advert_incomplete_section is null
	and advert_function.id is null
	and advert_industry.id is null
	and advert_theme.id is null
;

update project inner join advert
	on project.advert_id = advert.id
left join advert_institution 
	on advert.id = advert_institution.advert_id
left join advert_department
	on advert.id = advert_department.advert_id
set project.advert_incomplete_section = concat(project.advert_incomplete_section, "|SYSTEM_RESOURCE_TARGETS_HEADER")
where project.advert_incomplete_section is not null
	and advert_institution.id is null
	and advert_department.id is null
;

update project inner join advert
	on project.advert_id = advert.id
left join advert_institution 
	on advert.id = advert_institution.advert_id
left join advert_department
	on advert.id = advert_department.advert_id
set project.advert_incomplete_section = "SYSTEM_RESOURCE_TARGETS_HEADER"
where project.advert_incomplete_section is null
	and advert_institution.id is null
	and advert_department.id is null
;

update project inner join advert
	on project.advert_id = advert.id
left join advert_competence 
	on advert.id = advert_competence.advert_id
set project.advert_incomplete_section = concat(project.advert_incomplete_section, "|SYSTEM_RESOURCE_COMPETENCES_HEADER")
where project.advert_incomplete_section is not null
	and advert_competence.id is null
;

update project inner join advert
	on project.advert_id = advert.id
left join advert_competence 
	on advert.id = advert_competence.advert_id
set project.advert_incomplete_section = "SYSTEM_RESOURCE_COMPETENCES_HEADER"
where project.advert_incomplete_section is null
	and advert_competence.id is null
;
