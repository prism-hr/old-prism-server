Feature: Application Rejection 

Scenario: Admin rejects an application form
Given I am logged in as admin and on view applications page
When I reject a project
Then I should see Your have successfully rejected the application as a message

