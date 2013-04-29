		 _    _      _______   _____      _                     
		| |  | |  /\|__   __| |  __ \    | |                    
		| |  | | /  \  | |    | |__) |___| | ___  __ _ ___  ___ 
		| |  | |/ /\ \ | |    |  _  // _ \ |/ _ \/ _` / __|/ _ \
		| |__| / ____ \| |    | | \ \  __/ |  __/ (_| \__ \  __/
		 \____/_/    \_\_|    |_|  \_\___|_|\___|\__,_|___/\___|
		                                                        
    	Release Instructions for Zuhlke Internal UTA Release

========================================================================
                      Setup on your local machine
========================================================================

Make sure you've got that in your .m2/settings.xml

<settings>
    <servers>
    	...
        <server>
			<id>C12545-2</id>
			<username>pgadmin</username>
			<password>pg@m1ss1on</password>
		</server>
		<server>
			<id>repo.zuehlke.com</id>
			<username>!!!!!YOUR MERCURIAL USERNAME!!!!!</username>
			<password>!!!!!YOUR MERCURIAL PASSWORD!!!!!</password>
		</server>
    </servers>
    
    <proxies>
        <proxy>
            <active>true</active>
            <protocol>http</protocol>
            <host>proxy.zuehlke.com</host>
            <port>8080</port>
            <nonProxyHosts>localhost</nonProxyHosts>
        </proxy>
    </proxies>

========================================================================
                          Release Procedure
========================================================================

		1) Open a terminal and "cd" to your PRISM project folder
			# cd PRISM_HOME

		2) Make sure all the tests are succeeding
			# mvn clean test

		3) Prepare the release
			# mvn release:prepare

		4) Perform the release
			# mvn release:perform

		5) Go to Jenkins and deploy the release to UAT
			# http://beacon.zuehlke.com/jenkins/job/pgadmissions-release/