update imported_program
set imported_qualification_type_id = (
	select id
	from imported_entity
	where imported_entity_type = "IMPORTED_QUALIFICATION_TYPE"
	and name = "Other examinations and/or information")
where imported_qualification_type_id is null
and qualification is null
;

update imported_program
set imported_qualification_type_id = (
	select id
	from imported_entity
	where imported_entity_type = "IMPORTED_QUALIFICATION_TYPE"
	and name = "Other examinations and/or information")
where imported_qualification_type_id is null
and level = "OTHER"
	or level like "HE_LEVEL_%"
;

update imported_program
set imported_qualification_type_id = (
	select id
	from imported_entity
	where imported_entity_type = "IMPORTED_QUALIFICATION_TYPE"
	and name = "PhD degree (postgraduate)")
where imported_qualification_type_id is null
and qualification = "Qualification PhD"
;

update imported_program
set imported_qualification_type_id = (
	select id
	from imported_entity
	where imported_entity_type = "IMPORTED_QUALIFICATION_TYPE"
	and name = "MPhil degree (Postgraduate)")
where imported_qualification_type_id is null
and qualification = "Qualification MPhil"
;

update imported_program
set imported_qualification_type_id = (
	select id
	from imported_entity
	where imported_entity_type = "IMPORTED_QUALIFICATION_TYPE"
	and name = "Masters degree with credit / merit grading scheme")
where imported_qualification_type_id is null
and level = "POSTGRADUATE"
;

update imported_program
set imported_qualification_type_id = (
	select id
	from imported_entity
	where imported_entity_type = "IMPORTED_QUALIFICATION_TYPE"
	and name = "MEng UK degree with honours grading scheme")
where imported_qualification_type_id is null
and qualification in ("Qualification MEng", "Qualification MEng (Hons)")
;

update imported_program
set imported_qualification_type_id = (
	select id
	from imported_entity
	where imported_entity_type = "IMPORTED_QUALIFICATION_TYPE"
	and name = "MSci UK degree with honours grading scheme")
where imported_qualification_type_id is null
and qualification in ("Qualification MSci", "Qualification MSci (Hons)")
;

update imported_program
set imported_qualification_type_id = (
	select id
	from imported_entity
	where imported_entity_type = "IMPORTED_QUALIFICATION_TYPE"
	and name = "Masters degree with grading scheme of honours (pass)")
where imported_qualification_type_id is null
	and level = "UNDERGRADUATE"
	and qualification like "Qualification M%"
;

update imported_program
set imported_qualification_type_id = (
	select id
	from imported_entity
	where imported_entity_type = "IMPORTED_QUALIFICATION_TYPE"
	and name = "Masters degree with grading scheme of honours (pass)")
where imported_qualification_type_id is null
	and level = "UNDERGRADUATE"
	and qualification like "Qualification IPM%"
;

update imported_program
set imported_qualification_type_id = (
	select id
	from imported_entity
	where imported_entity_type = "IMPORTED_QUALIFICATION_TYPE"
	and name = "Bachelors degree with UK honours grading scheme")
where imported_qualification_type_id is null
	and level = "UNDERGRADUATE"
	and qualification like "Qualification B%"
;

update imported_program
set imported_qualification_type_id = (
	select id
	from imported_entity
	where imported_entity_type = "IMPORTED_QUALIFICATION_TYPE"
	and name = "Bachelors degree with UK honours grading scheme")
where imported_qualification_type_id is null
	and level = "UNDERGRADUATE"
	and qualification like "Qualification L%"
;

alter table imported_program
	modify column imported_qualification_type_id int(10) unsigned not null
;
