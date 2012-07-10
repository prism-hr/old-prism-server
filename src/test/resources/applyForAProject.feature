Feature: Apply for a Project

Scenario: Fred applies for a project
Given I am on projects page
When I click on Apply Now button for project KLM and log in as fred
Then I should see my application form

Scenario: Fred submits application form
Given I am on the application view page
When I click on submit
Then I should a success message