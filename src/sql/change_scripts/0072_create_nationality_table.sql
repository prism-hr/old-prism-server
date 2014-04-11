CREATE TABLE IF NOT EXISTS NATIONALITY(
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT, 
  country_id INTEGER UNSIGNED,
  nationality_type varchar(50),
  PRIMARY KEY(id),
  CONSTRAINT nationality_country_fk FOREIGN KEY (country_id) REFERENCES COUNTRIES(id)
)
ENGINE = InnoDB;

--//@UNDO

