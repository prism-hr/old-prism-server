drop table comment_custom_response
;

drop table action_custom_question_configuration
;

alter table action
	drop index action_custom_question_definition_id,
	drop foreign key action_ibfk_6,
	drop column action_custom_question_definition_id
;	

drop table action_custom_question_definition
;

alter table advert_competence
	add column importance int(1) unsigned not null
;

alter table comment_competence
	add column importance int(1) not null after competence_id
;

alter table comment_competence
	modify column rating int(1) unsigned not null
;

alter table advert
	modify column summary text
;

drop table user_institution
;

drop table user_subject_area
;

alter table user_program
	drop column relation_strength
;

drop table department_imported_subject_area
;

update state
set state_duration_evaluation = null
where state_duration_evaluation like "%_END_DATE"
;
