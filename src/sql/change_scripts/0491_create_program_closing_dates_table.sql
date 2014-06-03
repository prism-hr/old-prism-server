CREATE TABLE PROGRAM_CLOSING_DATES (
   id int(10) unsigned NOT NULL AUTO_INCREMENT,
   program_id int(10) unsigned NOT NULL,
   closing_date DATE NOT NULL,
   study_places int(5),
   PRIMARY KEY (id),
   CONSTRAINT program_closing_dates_fk FOREIGN KEY (program_id) REFERENCES PROGRAM(id)  
)
;