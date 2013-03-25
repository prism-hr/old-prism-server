CREATE TABLE EMAIL_TEMPLATE (
        id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
        name VARCHAR(100),
        content LONGTEXT,
	version TIMESTAMP NULL,
	active TINYINT(1) DEFAULT 0,
        PRIMARY KEY(id),
	UNIQUE KEY name_version (name, version)
)
ENGINE = innoDB
;
