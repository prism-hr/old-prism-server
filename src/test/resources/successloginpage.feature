Feature: SuccessLoginPage

Scenario: Display SuccessLoginPage
Given I am on the success login page
When I enter user bob and password password and submit successfully
Then I should successfully see Zuehlke project holding page as a title