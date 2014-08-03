ALTER TABLE USER
	DROP FOREIGN KEY user_ibfk_1,
	DROP COLUMN parent_user_id
;

DROP PROCEDURE SP_MERGE_ENTITIES
;

CREATE PROCEDURE SP_MERGE_ENTITIES(
	IN schema_name TEXT, 
	IN table_to_merge_name TEXT, 
	IN remove_row_id INT, 
	IN merge_into_id INT)
BEGIN

	DECLARE iter INT DEFAULT 1;
	DECLARE relation_count INT DEFAULT 0;

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

			SET relation_count = (select count(*) from FOUND_RELATION);
			
			SET FOREIGN_KEY_CHECKS = 0;
			
			WHILE iter <= relation_count DO
			
				SELECT r.table_name, r.column_name
					INTO @table_name, @column_name
				FROM FOUND_RELATION r
				WHERE r.id = iter;

				SET @update_keys_statement = CONCAT('UPDATE IGNORE ', @table_name, ' SET ', @column_name, ' = ', merge_into_id, ' WHERE ', @column_name, ' = ', remove_row_id);
				PREPARE update_keys FROM @update_keys_statement;
				EXECUTE update_keys;
				
				SET @delete_keys_statement = CONCAT('DELETE FROM ', @table_name, ' WHERE ', @column_name, ' = ', remove_row_id);
				PREPARE delete_keys FROM @delete_keys_statement;
				EXECUTE delete_keys;
				
				SET iter = iter + 1;
				
			END WHILE;

		SET @delete_row_statement = CONCAT('DELETE FROM ', table_to_merge_name, ' WHERE id = ', remove_row_id);
		PREPARE delete_row FROM @delete_row_statement;
		EXECUTE delete_row;

		SET FOREIGN_KEY_CHECKS = 1;

	COMMIT;
	
	DROP TABLE FOUND_RELATION;
	
END
;
