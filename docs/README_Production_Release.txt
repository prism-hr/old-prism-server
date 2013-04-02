		 _____  _____  _  _____ __  __   _____      _                     
		|  __ \|  __ \(_)/ ____|  \/  | |  __ \    | |                    
		| |__) | |__) |_| (___ | \  / | | |__) |___| | ___  __ _ ___  ___ 
		|  ___/|  _  /| |\___ \| |\/| | |  _  // _ \ |/ _ \/ _` / __|/ _ \
		| |    | | \ \| |____) | |  | | | | \ \  __/ |  __/ (_| \__ \  __/
		|_|    |_|  \_\_|_____/|_|  |_| |_|  \_\___|_|\___|\__,_|___/\___|

		Release Instructions for http://prism.ucl.ac.uk

========================================================================
                          Release Procedure
========================================================================

    1) SSH in to prism@prism.ucl.ac.uk (ssh prism@prism.ucl.ac.uk)    

    2) Become the root user and enter the password
        # sudo -s

    3) Make a backup of the existing database
        # cd /root/pgadmissions-backup
        # mkdir 2013-mm-dd (enter the current date)
        # mysqldump -u pgadmissions -ppgadmissions pgadmissions > out.sql
    
    4) Become the jboss user and update the current sources, build the WAR file 
        and run the database change scripts if necessary
        # su -l jboss
        # cd pgadmissions
        # hg pull
        # hg update
        # mvn clean package -DskipTests -Pucl-prod
        # mvn dbdeploy:update (runs the database change scripts)
        # cp target/pgadmissions.war /usr/local/jboss/current/standalone/deployments

    5) Check the log files if everything works like expected
        # tail -f /usr/local/jboss/current/standalone/log/server.log

========================================================================
                Accessing the JBoss Management Console
========================================================================

Create a SSH tunnel and forward the JBoss Management Console port to your 
local machine by issuing the following command (make sure you are in zred):    

    ssh -L 9990:localhost:9990 prism@prism.ucl.ac.uk

Then point your browser to:
    
    http://localhost:9990/console/

Username: prism / Password: pg@m1ss1on

