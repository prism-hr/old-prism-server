alter table action
    add column action_condition varchar(50) after action_category,
    add index (action_category)
;

alter table state_action
    drop index action_condition,
    drop column action_condition
;
