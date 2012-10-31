create table application_transfer_log_item (
    id bigint primary key,
    application_id integer not null references application_form,
    transfer_begin_timepoint timestamp,
    transfer_end_timepoint timestamp,
    was_webservice_call_successful boolean,
    was_files_transfer_successful boolean,
    status varchar(50)
)
ENGINE = InnoDB;
