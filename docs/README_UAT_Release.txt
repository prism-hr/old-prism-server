		 _    _      _______   _____      _                     
		| |  | |  /\|__   __| |  __ \    | |                    
		| |  | | /  \  | |    | |__) |___| | ___  __ _ ___  ___ 
		| |  | |/ /\ \ | |    |  _  // _ \ |/ _ \/ _` / __|/ _ \
		| |__| / ____ \| |    | | \ \  __/ |  __/ (_| \__ \  __/
		 \____/_/    \_\_|    |_|  \_\___|_|\___|\__,_|___/\___|
		                                                        
    	Release Instructions for Zuhlke Internal UTA Release

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