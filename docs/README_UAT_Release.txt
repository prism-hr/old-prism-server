            Prism Release Instructions

========================================================================
                      Setup on your local machine
========================================================================

Make sure you've got that in your .m2/settings.xml

<settings>
...
    <servers>
        <server>
			<id>prism-dev.alumeni.co.uk</id>
			<username>bitnami</username>
			<privateKey>!!!!!PATH TO DEVELOPMENT SERVER PRIVATE KEY!!!!!</privateKey>
			<passphrase>!!!!!DEVELOPMENT SERVER PRIVATE KEY PASSPHRASE!!!!!</passphrase>
		</server>
		<server>
			<id>mercurial.alumeni.co.uk</id>
            <username>!!!!!YOUR MERCURIAL USERNAME!!!!!</username>
            <password>!!!!!YOUR MERCURIAL PASSWORD!!!!!</password>
		</server>
    </servers>
...
</settings>

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

