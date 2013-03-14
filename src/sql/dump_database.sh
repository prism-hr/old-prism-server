read -p "This script will dump the database to a backup file..."

mysqldump \
--user=pgadmissions \
--password=pgadmissions \
--add-drop-database pgadmissions \
--add-drop-table \
--complete-insert \
--dump-date \
--result-file=pgadmissions.dump.`date +"%d_%m_%Y_%H_%M_%S"`.sql