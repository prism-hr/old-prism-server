DROP TABLE IF EXISTS INSTITUTION_REFERENCE
;

CREATE TABLE INSTITUTION_REFERENCE (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  code varchar(10) NOT NULL DEFAULT '',
  name varchar(100) NOT NULL DEFAULT '',
  domicile_code varchar(2) NOT NULL DEFAULT '',
  enabled tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  KEY name_idx (name),
  KEY domicile_code_idx (code),
  KEY domicile_country_code_idx (domicile_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
;