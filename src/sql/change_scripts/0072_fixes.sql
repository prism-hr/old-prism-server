alter table action
	drop column conclude_parent_action
;

alter table comment
	drop foreign key comment_ibfk_16,
	drop column parent_resource_transition_state_id
;

insert into institution_address(institution_domicile_id, institution_id, address_line_1, address_line_2, 
	address_town, address_region, address_code, location_x, location_y, location_view_ne_x, location_view_ne_y,
	location_view_sw_x, location_view_sw_y)
	select institution_address.institution_domicile_id, institution_address.institution_id, 
		institution_address.address_line_1, institution_address.address_line_2, institution_address.address_town, 
		institution_address.address_region, institution_address.address_code, institution_address.location_x, 
		institution_address.location_y, institution_address.location_view_ne_x, institution_address.location_view_ne_y,
		institution_address.location_view_sw_x, institution_address.location_view_sw_y
	from institution inner join institution_address
		on institution.institution_address_id = institution_address.id
	where institution.id = 5243
;

update advert
set institution_address_id = last_insert_id()
where id = 925
;

insert into institution_address(institution_domicile_id, institution_id, address_line_1, address_line_2, 
	address_town, address_region, address_code, location_x, location_y, location_view_ne_x, location_view_ne_y,
	location_view_sw_x, location_view_sw_y)
	select institution_address.institution_domicile_id, institution_address.institution_id, 
		institution_address.address_line_1, institution_address.address_line_2, institution_address.address_town, 
		institution_address.address_region, institution_address.address_code, institution_address.location_x, 
		institution_address.location_y, institution_address.location_view_ne_x, institution_address.location_view_ne_y,
		institution_address.location_view_sw_x, institution_address.location_view_sw_y
	from institution inner join institution_address
		on institution.institution_address_id = institution_address.id
	where institution.id = 5243
;

update advert
set institution_address_id = last_insert_id()
where id = 1842
;

insert into institution_address(institution_domicile_id, institution_id, address_line_1, address_line_2, 
	address_town, address_region, address_code, location_x, location_y, location_view_ne_x, location_view_ne_y,
	location_view_sw_x, location_view_sw_y)
	select institution_address.institution_domicile_id, institution_address.institution_id, 
		institution_address.address_line_1, institution_address.address_line_2, institution_address.address_town, 
		institution_address.address_region, institution_address.address_code, institution_address.location_x, 
		institution_address.location_y, institution_address.location_view_ne_x, institution_address.location_view_ne_y,
		institution_address.location_view_sw_x, institution_address.location_view_sw_y
	from institution inner join institution_address
		on institution.institution_address_id = institution_address.id
	where institution.id = 5243
;

update advert
set institution_address_id = last_insert_id()
where id = 925
;

delete
from state_transition_pending
;

delete 
from comment_state
where comment_id in (
	select id
	from comment
	where action_id like "%_CONCLUDE")
;

delete 
from comment_transition_state
where comment_id in (
	select id
	from comment
	where action_id like "%_CONCLUDE")
;

delete
from comment
where action_id like "%_CONCLUDE"
;

alter table comment
	add column application_reserve_rating varchar(50) after application_recruiter_accept_appointment,
	add index (application_id, application_reserve_rating)
;

alter table comment
	drop column application_use_custom_referee_questions,
	drop column application_use_custom_recruiter_questions
;

alter table application
	add column application_reserve_rating varchar(50) after application_rating_average,
	add index (application_reserve_rating, sequence_identifier)
;

update state_transition
set state_transition_evaluation_id = null
where state_transition_evaluation_id = "APPLICATION_CONFIRMED_OFFER_OUTCOME"
;

delete
from state_transition_evaluation
where id = "APPLICATION_CONFIRMED_OFFER_OUTCOME"
;

delete
from state_transition_propagation
where state_transition_id in (
	select id 
	from state_transition
	where state_action_id in (
		select id
		from state_action
		where action_id in (
			select id
			from action
			where id like "%_CONCLUDE")))
;

delete
from state_transition_propagation
where propagated_action_id in (
	select id
	from action
	where id like "%_CONCLUDE")
;

delete 
from state_transition
where state_action_id in (
	select id
	from state_action
	where action_id in (
		select id
		from action
		where id like "%_CONCLUDE"))
;

delete
from state_action
where action_id in (
	select id
	from action
	where id like "%_CONCLUDE")
;

delete
from action
where id like "%_CONCLUDE"
;

alter table state_group
	modify column sequence_order int(2) unsigned not null
;

update state_group
set sequence_order = sequence_order + 20
;
