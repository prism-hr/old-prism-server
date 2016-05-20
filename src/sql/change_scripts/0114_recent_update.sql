alter table application
	drop index do_retain,
	add index (shared, sequence_identifier),
	add column recent_update int(1) unsigned after previous_state_id,
	add index (recent_update, sequence_identifier)
;

update application
set recent_update = 1
where updated_timestamp > current_timestamp() - interval 1 day
;

alter table project
	add column recent_update int(1) unsigned after previous_state_id,
	add index (recent_update, sequence_identifier)
;

update project
set recent_update = 1
where updated_timestamp > current_timestamp() - interval 1 day
;

alter table program
	add column recent_update int(1) unsigned after previous_state_id,
	add index (recent_update, sequence_identifier)
;

update program
set recent_update = 1
where updated_timestamp > current_timestamp() - interval 1 day
;

alter table department
	add column recent_update int(1) unsigned after previous_state_id,
	add index (recent_update, sequence_identifier)
;

update department
set recent_update = 1
where updated_timestamp > current_timestamp() - interval 1 day
;

alter table institution
	add column recent_update int(1) unsigned after previous_state_id,
	add index (recent_update, sequence_identifier)
;

update institution
set recent_update = 1
where updated_timestamp > current_timestamp() - interval 1 day
;

alter table system
	add column recent_update int(1) unsigned after previous_state_id,
	add index (recent_update, sequence_identifier)
;

update system
set recent_update = 1
where updated_timestamp > current_timestamp() - interval 1 day
;
