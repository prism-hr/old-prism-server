CREATE PROCEDURE SP_MERGE_ENTITIES (IN `schema_name` TEXT, IN `table_to_merge_name` TEXT, IN `remove_row_id` INT, IN `merge_into_id` INT)
BEGIN

DECLARE iter INT DEFAULT 1;
DECLARE relation_count INT DEFAULT 0;

DECLARE table_name VARCHAR(100);
DECLARE column_name VARCHAR(100);

DECLARE rows_matched INT DEFAULT -1;
DECLARE rows_updated INT DEFAULT -1;

DROP TABLE IF EXISTS FOUND_RELATION;
CREATE TEMPORARY TABLE FOUND_RELATION (
 id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
 table_name VARCHAR(50) NOT NULL,
 column_name VARCHAR(50) NOT NULL,
 PRIMARY KEY (id)
) ENGINE = MEMORY;

START TRANSACTION;

INSERT INTO FOUND_RELATION(table_name, column_name)
 SELECT cu.TABLE_NAME AS table_name, 
  cu.COLUMN_NAME AS column_name
 FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE cu
 WHERE cu.CONSTRAINT_SCHEMA = schema_name
  AND cu.REFERENCED_TABLE_NAME = table_to_merge_name;

set relation_count = (select count(*) from FOUND_RELATION);

SET FOREIGN_KEY_CHECKS=0;

WHILE iter <= relation_count DO

	SELECT r.table_name,r.column_name
	into @table_name, @column_name
	from FOUND_RELATION r
	where r.id = iter;
	
	SET @find_keys_to_change_statement = CONCAT('SELECT count(*) INTO @rows_matched FROM ', @table_name, ' WHERE ', @column_name, ' = ', remove_row_id);
	SET @change_keys_statement = CONCAT('UPDATE ', @table_name, ' SET ', @column_name, ' = ', merge_into_id, ' WHERE ', @column_name, ' = ', remove_row_id);
	
	PREPARE find_keys_to_change FROM @find_keys_to_change_statement;
	PREPARE change_keys FROM @change_keys_statement;
	
	execute find_keys_to_change;
	execute change_keys;
	
	SET @rows_updated = (SELECT ROW_COUNT());
	
	-- IF @rows_updated != @rows_matched THEN
	-- 	SIGNAL SQLSTATE 'HY000';
	-- END IF;

	set iter = iter + 1;
END WHILE;

-- now remove given row
SET @delete_given_row_statement = CONCAT('DELETE FROM ', table_to_merge_name, ' WHERE id = ', remove_row_id);
PREPARE delete_given_row FROM @delete_given_row_statement;
EXECUTE delete_given_row;

SET FOREIGN_KEY_CHECKS=1;

COMMIT;

DROP TABLE FOUND_RELATION;

END
