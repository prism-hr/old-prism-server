alter table action
    add column action_sequence_start int(1) unsigned after visible_action,
    add column action_sequence_close int(1) unsigned after action_sequence_start,
    add index (action_sequence_start),
    add index (action_sequence_close)
;

update action
set action_sequence_start = false,
    action_sequence_close = false
;

alter table action
    modify column action_sequence_start int(1) unsigned not null,
    modify column action_sequence_close int(1) unsigned not null
;

alter table action
    drop column action_sequence_start,
    drop column action_sequence_close,
    add column replicable_action int(1) unsigned after visible_action,
    add index (replicable_action)
;

update action
set replicable_action = 0
;

alter table action
    modify column replicable_action int(1) unsigned not null
;

alter table action
    add column replicable_action_sequence_start int(1) unsigned not null default 0 after replicable_action,
    add column replicable_action_sequence_close int(1) unsigned not null default 0 after replicable_action_sequence_start
;

alter table action
    modify column replicable_action_sequence_start int(1) unsigned not null,
    modify column replicable_action_sequence_close int(1) unsigned not null
;

update action
set transition_action = false
where transition_action is null
;

alter table action
    modify transition_action int(1) unsigned not null
;

alter table action
    drop column replicable_action
;

alter table action
    drop column replicable_action_sequence_start,
    drop column replicable_action_sequence_close
;

alter table state_action
    add column replicable_sequence_start int(1) unsigned not null default 0 after raises_urgent_flag,
    add column replicable_sequence_close int(1) unsigned not null default 0 after replicable_sequence_start
;

alter table state_action
    add index (replicable_sequence_start),
    add index (replicable_sequence_close)
;

alter table state_action
    modify column replicable_sequence_start int(1) unsigned not null,
    modify column replicable_sequence_close int(1) unsigned not null
;

update application
set state_id = "APPLICATION_VALIDATION"
where state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

update application
set previous_state_id = "APPLICATION_VALIDATION"
where previous_state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

update comment
set state_id = "APPLICATION_VALIDATION"
where state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

update comment
set transition_state_id = "APPLICATION_VALIDATION"
where transition_state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

update comment_state
set state_id = "APPLICATION_VALIDATION"
where state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

update comment_transition_state
set state_id = "APPLICATION_VALIDATION"
where state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

update department
set state_id = "APPLICATION_VALIDATION"
where state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

update department
set previous_state_id = "APPLICATION_VALIDATION"
where previous_state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

alter table institution
    add foreign key (previous_state_id) references state (id)
;

update institution
set state_id = "APPLICATION_VALIDATION"
where state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

update institution
set previous_state_id = "APPLICATION_VALIDATION"
where previous_state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

update program
set state_id = "APPLICATION_VALIDATION"
where state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

update program
set previous_state_id = "APPLICATION_VALIDATION"
where previous_state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

update project
set state_id = "APPLICATION_VALIDATION"
where state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

update project
set previous_state_id = "APPLICATION_VALIDATION"
where previous_state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

update resource_state
set state_id = "APPLICATION_VALIDATION"
where state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

update resource_previous_state
set state_id = "APPLICATION_VALIDATION"
where state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

delete from role_transition
where state_transition_id in (
    select id from state_transition
    where state_action_id in (
        select id
        from state_action
        where state_id = "APPLICATION_VALIDATION")
        or transition_state_id = "APPLICATION_VALIDATION")
;

delete from state_transition
where state_action_id in (
    select id
    from state_action
    where state_id = "APPLICATION_VALIDATION")
    or transition_state_id = "APPLICATION_VALIDATION"
;

delete from state_action_assignment
where state_action_id in (
    select id
    from state_action
    where state_id = "APPLICATION_VALIDATION")
;

delete from state_action
where state_id = "APPLICATION_VALIDATION"
;

update state_action
set state_id = "APPLICATION_VALIDATION"
where state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

update state_transition
set transition_state_id = "APPLICATION_VALIDATION"
where transition_state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

alter table system  
    add foreign key (previous_state_id) references state (id)
;

update system
set state_id = "APPLICATION_VALIDATION"
where state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

update system
set previous_state_id = "APPLICATION_VALIDATION"
where previous_state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

delete from state
where id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
;

alter table advert
    add column closing_date date after pay_converted,
    add index (closing_date)
;

update advert inner join advert_closing_date
    on advert.id = advert_closing_date.advert_id
set advert.closing_date = advert_closing_date.closing_date
;

alter table advert
    drop index advert_closing_date_id,
    drop foreign key advert_ibfk_2,
    drop column advert_closing_date_id
;

drop table advert_closing_date
;

alter table advert
    drop index fee_currency,
    drop index pay_currency_at_locale,
    drop column pay_currency_at_locale,
    change column pay_currency_specified pay_currency varchar(10),
    change column month_pay_minimum_specified pay_minimum decimal(10, 2) unsigned,
    change column month_pay_maximum_specified pay_maximum decimal(10, 2) unsigned,
    change column year_pay_minimum_specified pay_minimum_normalized decimal(10, 2) unsigned,
    change column year_pay_maximum_specified pay_maximum_normalized decimal(10, 2) unsigned,
    drop index month_pay_minimum_at_locale,
    drop index month_pay_maximum_at_locale,
    drop index year_pay_minimum_at_locale,
    drop index year_pay_maximum_at_locale,
    drop column month_pay_minimum_at_locale,
    drop column month_pay_maximum_at_locale,
    drop column year_pay_minimum_at_locale,
    drop column year_pay_maximum_at_locale
;

alter table advert
    change column last_currency_conversion_date last_pay_conversion_date date after pay_maximum_normalized,
    drop index last_currency_conversion_date,
    add index (last_pay_conversion_date),
    drop column pay_converted
;

alter table state_transition
    add column replicable_sequence_close int(1) unsigned not null default 0 after transition_action_id,
    add index (replicable_sequence_close)
;

alter table state_transition
    modify column replicable_sequence_close int(1) unsigned not null
;

update state
set state_duration_evaluation = null
;

alter table state_action
    drop index replicable_sequence_close,
    drop column replicable_sequence_close
;

alter table advert
    add column pay_hours_week_minimum integer (2) unsigned after pay_interval,
    add column pay_hours_week_maximum integer (2) unsigned after pay_hours_week_minimum
;

alter table advert
    drop index month_pay_minimum_specified,
    add index (pay_minimum, sequence_identifier),
    drop index month_pay_maximum_specified,
    add index (pay_maximum, sequence_identifier),
    drop index year_pay_minimum_specified,
    drop index year_pay_maximum_specified,
    add index (pay_minimum_normalized),
    add index (pay_maximum_normalized)
;   

alter table advert
    change column pay_minimum_normalized pay_minimum_normalized_year decimal(10, 2) after pay_minimum,
    add column pay_minimum_normalized_month decimal(10, 2) after pay_minimum_normalized_year,
    add column pay_minimum_normalized_week decimal(10, 2) after pay_minimum_normalized_month,
    add column pay_minimum_normalized_day decimal(10, 2) after pay_minimum_normalized_week,
    add column pay_minimum_normalized_hour decimal(10, 2) after pay_minimum_normalized_day,
    drop index pay_minimum_normalized,
    add index (pay_minimum_normalized_year, sequence_identifier),
    add index (pay_minimum_normalized_month, sequence_identifier),
    add index (pay_minimum_normalized_week, sequence_identifier),
    add index (pay_minimum_normalized_day, sequence_identifier),
    add index (pay_minimum_normalized_hour, sequence_identifier),
    change column pay_maximum_normalized pay_maximum_normalized_year decimal(10, 2) after pay_maximum,
    add column pay_maximum_normalized_month decimal(10, 2) after pay_maximum_normalized_year,
    add column pay_maximum_normalized_week decimal(10, 2) after pay_maximum_normalized_month,
    add column pay_maximum_normalized_day decimal(10, 2) after pay_maximum_normalized_week,
    add column pay_maximum_normalized_hour decimal(10, 2) after pay_maximum_normalized_day,
    drop index pay_maximum_normalized,
    add index (pay_maximum_normalized_year, sequence_identifier),
    add index (pay_maximum_normalized_month, sequence_identifier),
    add index (pay_maximum_normalized_week, sequence_identifier),
    add index (pay_maximum_normalized_day, sequence_identifier),
    add index (pay_maximum_normalized_hour, sequence_identifier)
;

alter table advert
    change column pay_minimum_normalized_year pay_minimum_normalized decimal(10, 2),
    drop column pay_minimum_normalized_month,
    drop column pay_minimum_normalized_week,
    drop column pay_minimum_normalized_day,
    drop column pay_minimum_normalized_hour,
    drop index pay_minimum_normalized_year,
    add index (pay_minimum_normalized, sequence_identifier),
    drop index pay_minimum_normalized_month,
    drop index pay_minimum_normalized_week,
    drop index pay_minimum_normalized_day,
    drop index pay_minimum_normalized_hour,
    change column pay_maximum_normalized_year pay_maximum_normalized decimal(10, 2),
    drop column pay_maximum_normalized_month,
    drop column pay_maximum_normalized_week,
    drop column pay_maximum_normalized_day,
    drop column pay_maximum_normalized_hour,
    drop index pay_maximum_normalized_year,
    add index (pay_maximum_normalized, sequence_identifier),
    drop index pay_maximum_normalized_month,
    drop index pay_maximum_normalized_week,
    drop index pay_maximum_normalized_day,
    drop index pay_maximum_normalized_hour
;

alter table advert
    modify column pay_minimum_normalized decimal(10, 2) unsigned after pay_maximum,
    modify column pay_maximum_normalized decimal(10, 2) unsigned
;

alter table advert
    add column pay_minimum_normalized_hour decimal(10, 2) unsigned after pay_maximum_normalized,
    add column pay_maximum_normalized_hour decimal(10, 2) unsigned after pay_minimum_normalized_hour,
    add index (pay_minimum_normalized_hour, sequence_identifier),
    add index (pay_maximum_normalized_hour, sequence_identifier)
;


