DROP TABLE IF EXISTS APPLICATION_FORM_TRANSFER_ERROR
;

create table APPLICATION_FORM_TRANSFER_ERROR (
    id BIGINT NOT NULL AUTO_INCREMENT,
    transfer_id BIGINT NOT NULL,
    handling_time TIMESTAMP,
    diagnostic_info TEXT,
    problem_classification VARCHAR(50),
    error_handling_strategy VARCHAR(50),
    request_copy TEXT,
    response_copy TEXT,
    PRIMARY KEY(id),
    FOREIGN KEY (transfer_id) REFERENCES APPLICATION_FORM_TRANSFER(id)
)
ENGINE = InnoDB
;