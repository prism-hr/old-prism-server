<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/additional_information.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/address.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/documents.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/employment.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/funding.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/personal_details.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/programme.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/qualifications.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/references.css' />"/>
	
			
<!-- Programme -->
<section id="programmeDetailsSection" class="form-rows violet">
<#include "/private/staff/application/components/programme_details.ftl"/>
</section>

<!-- Personal Details -->
<section id="personalDetailsSection" class="form-rows purple">
<#include "/private/staff/application/components/personal_details.ftl"/>
</section>

<!-- Address -->
<section class="form-rows red">
<#include "/private/staff/application/components/address_details.ftl"/>
</section>

<!-- Qualifications -->
<section class="form-rows orange">
<#include "/private/staff/application/components/qualification_details.ftl"/>
</section>

<!-- Employment positions -->
<section class="form-rows yellow">
<#include "/private/staff/application/components/employment_position_details.ftl"/>
</section>

<!-- Funding -->
<section class="form-rows green">
<#include "/private/staff/application/components/funding_details.ftl"/>
</section>

<#if user.hasStaffRightsOnApplicationForm(applicationForm)>
<!-- References -->
<section class="form-rows navy">
<#include "/private/staff/application/components/references_details.ftl"/>
</section>
</#if>

<!-- Documents -->
<section class="form-rows blue">
<#include "/private/staff/application/components/documents.ftl"/>
</section>

<!-- Additional information -->
<#if user.canSeeRestrictedInformation(applicationForm) >
  <section id="additionalInformationSection" class="form-rows lightblue">
  <#include "/private/staff/application/components/additional_information.ftl"/>
  </section>
</#if>
