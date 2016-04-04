alter table state_transition
    add column replicable_sequence_filter_primary_theme int(1) unsigned not null default 0 after replicable_sequence_close,
    add column replicable_sequence_filter_secondary_theme int(1) unsigned not null default 0 after replicable_sequence_filter_primary_theme,
    add column replicable_sequence_filter_primary_location int(1) unsigned not null default 0 after replicable_sequence_filter_secondary_theme,
    add column replicable_sequence_filter_secondary_location int(1) unsigned not null default 0 after replicable_sequence_filter_primary_location
;

alter table state_transition
    modify column replicable_sequence_filter_primary_theme int(1) unsigned not null,
    modify column replicable_sequence_filter_secondary_theme int(1) unsigned not null,
    modify column replicable_sequence_filter_primary_location int(1) unsigned not null,
    modify column replicable_sequence_filter_secondary_location int(1) unsigned not null
;

alter table state_transition
    change column replicable_sequence_filter_primary_theme replicable_sequence_filter_theme int(1) unsigned not null,
    change column replicable_sequence_filter_primary_location replicable_sequence_filter_location int(1) unsigned not null
;
