Feature: SuccessLoginPage

Scenario: Display SuccessLoginPage
Given I am on the success login page
When I enter user admin and password admin and submit successfully
Then I should successfully see Zuehlke project holding page as a title