<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<!--
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/field_controls.css' />"/>
-->
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
  <section id="programmeDetailsSection" class="folding violet">
  	<#include "/private/staff/application/components/programme_details.ftl"/>
  </section>
  
  <!-- Personal Details -->
   <section id="personalDetailsSection" class="folding purple">
  	<#include "/private/staff/application/components/personal_details.ftl"/>
  </section>
  
  <!-- Address -->
  <section class="folding red">
    <#include "/private/staff/application/components/address_details.ftl"/>
  </section>
  
   <section class="folding orange">
    <#include "/private/staff/application/components/qualification_details.ftl"/>
  </section>
  
   <section class="folding yellow">
     <#include "/private/staff/application/components/employment_position_details.ftl"/>
  </section>
  
   <section class="folding green">
     <#include "/private/staff/application/components/funding_details.ftl"/>
  </section>
  
  <#if !user.isRefereeOfApplicationForm(applicationForm) || user.isInRole('SUPERADMINISTRATOR') >
   <section class="folding navy">
     <#include "/private/staff/application/components/references_details.ftl"/>
  </section>
  </#if>
  
   <section class="folding blue">
     <#include "/private/staff/application/components/documents.ftl"/>
  </section>
  
  <section id="additionalInformationSection" class="folding lightblue">
       <#include "/private/staff/application/components/additional_information.ftl"/>
  </section>
  
  
<!--
  <hr />
  

  
			    	
			
	<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
	<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
	<script type="text/javascript" src="<@spring.url '/design/default/js/application/formActions.js'/>"></script>
-->