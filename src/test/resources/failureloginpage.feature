Feature: FailureLoginPage

Scenario: Display FailureLoginPage
Given I am on the failed login page
When I enter user fred and password password and submit unsuccessfully
Then I should see HTTP Status 403 as a failed title