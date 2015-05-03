alter table advert
	add column sponsorship_target decimal(10, 2) unsigned,
	add index (institution_partner_id, sponsorship_target, sequence_identifier)
;

create table advert_sponsor (
	id int(10) unsigned not null auto_increment,
	advert_id int(10) unsigned not null,
	sponsor_id int(10) unsigned not null,
	sponsorship_commited decimal(10, 2) unsigned not null,
	sponsorship_provided decimal(10, 2) unsigned not null,
	created_timestamp datetime not null,
	primary key (id),
	index (advert_id, sponsor_id),
	index (sponsor_id),
	index (advert_id, created_timestamp),
	foreign key (advert_id) references advert (id),
	foreign key (sponsor_id) references institution (id)
) collate = utf8_general_ci,
engine = innodb
;

drop table program_relation
;

alter table comment
	drop column creator_ip_address
;

alter table advert_sponsor
	add column sponsorship_currency varchar(10) not null after sponsor_id,
	change column sponsorship_commited sponsorship_committed_specified decimal(10, 2) not null,
	add column sponsorship_committed_converted decimal(10, 2) not null after sponsorship_committed_specified,
	change column sponsorship_provided sponsorship_provided_specified decimal(10, 2) not null,
	add column sponsorship_provided_converted decimal(10, 2) not null after sponsorship_provided_specified
;

alter table comment
	add column sponsorship_currency varchar(10) after transition_state_id,
	add column sponsorship_amount_specified decimal(10, 2) unsigned after sponsorship_currency,
	add column sponsorship_amount_converted decimal(10, 2) unsigned after sponsorship_amount_specified,
	add column sponsorship_confirmed int(1) unsigned after sponsorship_amount_converted
;

update comment
set state_id = replace(state_id, "DEACTIVATED", "APPROVED"),
	transition_state_id = replace(transition_state_id, "DEACTIVATED", "APPROVED")
;

update comment_state
set state_id = replace(state_id, "DEACTIVATED", "APPROVED")
;

update comment_transition_state
set state_id = replace(state_id, "DEACTIVATED", "APPROVED")
;

delete resource_condition.*
from resource_condition inner join program
	on resource_condition.program_id = program.id
where program.state_id like "%_DEACTIVATED"
	and resource_condition.action_condition = "ACCEPT_APPLICATION"
;

update program
set state_id = replace(state_id, "DEACTIVATED", "APPROVED"),
	previous_state_id = replace(previous_state_id, "DEACTIVATED", "APPROVED")
;

delete resource_condition.*
from resource_condition inner join project
	on resource_condition.project_id = project.id
where project.state_id like "%_DEACTIVATED"
	and resource_condition.action_condition = "ACCEPT_APPLICATION"
;

update project
set state_id = replace(state_id, "DEACTIVATED", "APPROVED"),
	previous_state_id = replace(previous_state_id, "DEACTIVATED", "APPROVED")
;

update resource_state
set state_id = replace(state_id, "DEACTIVATED", "APPROVED")
;

update resource_previous_state
set state_id = replace(state_id, "DEACTIVATED", "APPROVED")
;

delete
from role_transition
where state_transition_id in (
	select id
	from state_transition
	where transition_state_id like "%_DEACTIVATED"
		or state_action_id in (
			select id
			from state_action
			where state_id like "%_DEACTIVATED"))
;

delete
from state_transition_propagation
where state_transition_id in (
	select id
	from state_transition
	where transition_state_id like "%_DEACTIVATED"
		or state_action_id in (
			select id
			from state_action
			where state_id like "%_DEACTIVATED"))
;

delete
from state_transition
where transition_state_id like "%_DEACTIVATED"
	or state_action_id in (
		select id
		from state_action
		where state_id like "%_DEACTIVATED")
;

delete
from state_action_assignment
where state_action_id in (
	select id
	from state_action
	where state_id like "%_DEACTIVATED")
;

delete
from state_action_notification
where state_action_id in (
	select id
	from state_action
	where state_id like "%_DEACTIVATED")
;

delete
from state_action
where state_id like "%_DEACTIVATED"
;

delete
from state
where id like "%_DEACTIVATED"
;

alter table comment
	add column sponsorship_requirement_fulfilled int(1) unsigned after sponsorship_confirmed
;

alter table advert
	add column sponsorship_secured decimal(10, 2) after sponsorship_target,
	add index (sponsorship_target, sponsorship_secured)
;

drop table advert_sponsor
;

alter table comment
	add column sponsor_id int(10) unsigned after transition_state_id,
	add index (sponsor_id),
	add foreign key (sponsor_id) references institution (id)
;

alter table comment
	modify column sponsorship_amount_converted decimal(10, 2)
;
