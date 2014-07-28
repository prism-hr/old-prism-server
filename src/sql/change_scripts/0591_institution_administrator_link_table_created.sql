CREATE TABLE INSTITUTION_ADMINISTRATOR_LINK (
institution_id INT(10) UNSIGNED NOT NULL,
administrator_id INT(10) UNSIGNED NOT NULL,
CONSTRAINT institution_administrator_link_administrator_fk FOREIGN KEY (administrator_id) REFERENCES REGISTERED_USER(id),
CONSTRAINT institution_administrator_link_institution_fk FOREIGN KEY (institution_id) REFERENCES INSTITUTION(id)
) ENGINE = INNODB
;
