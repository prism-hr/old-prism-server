<!DOCTYPE HTML>
<#-- Assignments -->
<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<#if user.isInRole('APPLICANT')>
  <#assign formDisplayState = "close"/>
<#else>
  <#assign formDisplayState = "open"/>
</#if>

<#if message?has_content>
  <#assign globalMsg = true/>
<#else>
  <#assign globalMsg = false/>
</#if>
<!---- validation errors -->
 <@spring.bind "applicationForm.programmeDetails" />
<#if spring.status.errorMessages?has_content >
  <#assign programDetailsError = true/>
<#else>
  <#assign programDetailsError = false/>
</#if>
 <@spring.bind "applicationForm.programmeDetails.studyOption" />
<#if spring.status.errorMessages?has_content >
  <#assign studyOptionError = true />
<#else>
  <#assign studyOptionError = false />
</#if>

 <@spring.bind "applicationForm.program" />
<#if spring.status.errorMessages?has_content >
  <#assign programError = true />
<#else>
  <#assign programError = false />
</#if>

<@spring.bind "applicationForm.personalDetails" />
<#if spring.status.errorMessages?has_content >
  <#assign personalDetailsError = true/>
<#else>
  <#assign personalDetailsError = false/>
</#if>
<@spring.bind "applicationForm.currentAddress" />
<#if spring.status.errorMessages?has_content >
  <#assign currentAddressError = true/>
<#else>
  <#assign currentAddressError = false/>
</#if>
<@spring.bind "applicationForm.contactAddress" />
<#if spring.status.errorMessages?has_content >
  <#assign contactAddressError = true/>
<#else>
  <#assign contactAddressError = false/>
</#if>

<@spring.bind "applicationForm.personalStatement" />
<#if spring.status.errorMessages?has_content >
  <#assign personalStatementError = true/>
<#else>
  <#assign personalStatementError = false/>
</#if>
<@spring.bind "applicationForm.referees" />
<#if spring.status.errorMessages?has_content >
  <#assign refereesError = true/>
<#else>
  <#assign refereesError = false/>
</#if>
<@spring.bind "applicationForm.additionalInformation" />
<#if spring.status.errorMessages?has_content >
  <#assign additionalInformationError = true/>
<#else>
  <#assign additionalInformationError = false/>
</#if>
<@spring.bind "applicationForm.acceptedTermsOnSubmission" />
<#if spring.status.errorMessages?has_content >
  <#assign termsAndConditionsError = true/>
<#else>
  <#assign termsAndConditionsError = false/>
</#if>
<#-- Personal Details Rendering -->
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="prism.version" content="<@spring.message 'prism.version'/>" >
<title>UCL Postgraduate Admissions</title>

<!-- Styles for Application Page -->    
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/jquery-ui-1.8.23.custom.css' />"/>

<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/field_controls.css' />"/>

<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/additional_information.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/address.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/documents.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/employment.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/funding.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/personal_details.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/programme.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/qualifications.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/references.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/terms_and_condition.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/timeline.css' />"/>

<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>

<!-- Scripts -->
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery-ui-1.8.23.custom.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>    
<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/formActions.js'/>"></script> 

<script type="text/javascript" src="<@spring.url '/design/default/js/application/timeline_application.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/actions.js'/>"></script>

<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>

<!--[if lt IE 9]>
<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->

<!-- Styles for Application Page -->
</head>

<!--[if IE 9]>
<body class="ie9">
<![endif]-->
<!--[if lt IE 9]>
<body class="old-ie">
<![endif]-->
<!--[if (gte IE 9)|!(IE)]><!-->
<body>
<!--<![endif]-->

<input type="hidden" id="programDetailsError" value="${programDetailsError?string}"/>
<input type="hidden" id="personalDetailsError" value="${personalDetailsError?string}"/>
<input type="hidden" id="addressError" value="<#if currentAddressError || contactAddressError>true<#else>false</#if>"/>
<input type="hidden" id="personalStatementError" value="${personalStatementError?string}"/>
<input type="hidden" id="refereesError" value="${refereesError?string}"/>
<input type="hidden" id="studyOptionError" value="${studyOptionError?string}"/>
<input type="hidden" id="programError" value="${programError?string}"/>
<input type="hidden" id="additionalInformationError" value="${additionalInformationError?string}"/>
<input type="hidden" id="termsAndConditionsError" value="${termsAndConditionsError?string}"/>

<div id="wrapper">
      
  <#include "/private/common/global_header.ftl"/>
  
  <!-- Middle. -->
  <div id="middle">
  
    <#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
    <@header/>
    <!-- Main content area. -->
    <article id="content" role="main">

      <!-- FLOATING TOOLBAR -->
      <ul id="view-toolbar" class="toolbar">
        <li class="top"><a href="javascript:backToTop();" title="Back to top">Back to top</a></li>
        <li class="download"><a target="_blank" title="Download" href="<@spring.url '/print?applicationFormId=${applicationForm.applicationNumber}'/>">Download</a></li>
        
        <li class="tool-separator"><a href="#"></a></li>
        
        <li id="tool-programme" class="tool-button"><a href="#programmeDetailsSection" title="Back to Programme">Back to Programme</a></li>
        <li id="tool-personal" class="tool-button"><a href="#personalDetailsSection" title="Back to Personal Details">Back to Personal Details</a></li> 
        <li id="tool-address" class="tool-button"><a href="#addressSection" title="Back to Address">Back to Address</a></li>
        <li id="tool-qualification" class="tool-button"><a href="#qualificationsSection" title="Back to Qualifications">Back to Qualifications</a></li>
        <li id="tool-employment" class="tool-button"><a href="#positionSection" title="Back to Employment">Back to Employment</a></li> 
        <li id="tool-funding" class="tool-button"><a href="#fundingSection" title="Back to Funding">Back to Funding</a></li> 
        <li id="tool-references" class="tool-button"><a href="#referencesSection" title="Back to References">Back to References</a></li>
        <li id="tool-documents" class="tool-button"><a href="#documentSection" title="Back to Documents">Back to Documents</a></li> 
        <li id="tool-information" class="tool-button tool-information"><a href="#additionalInformationSection" title="Back to Additional Information">Back to Additional Information</a></li> 
      </ul>

      <!-- content box -->
      <div class="content-box">
        <div class="content-box-inner">
          <#include "/private/common/parts/application_info.ftl"/>

          <@spring.bind "applicationForm.*" />
          <#if spring.status.errorMessages?has_content>
          
          <div class="alert alert-error big">
          <i class="icon-warning-sign"></i>
         	 Some required fields are missing, please review your application form.
          </div>
          </#if>

		  
          <input type="hidden" id="applicationId" name="applicationId" value="${applicationForm.applicationNumber}"/>
					<div id="timelineview">
						<ul class="tabs">				
							<li class="current"><a href="#application" id="applicationBtn">Application</a></li>
							<li><a href="#timeline" id="timelineBtn">Timeline</a></li>
						</ul>
				
						<div class="tab-page" id="applicationTab">
	
		          <!-- Programme Details -->
		          <section id="programmeDetailsSection" class="folding form-rows violet <#if programDetailsError || studyOptionError>error</#if>">                      
		          </section>
		          
		          <!-- Personal Details -->
		          <section id="personalDetailsSection" class="folding form-rows purple <#if personalDetailsError>error</#if>">
		          </section>
		          
		          <!-- Address -->                      
		          <section id="addressSection" class="folding form-rows red <#if currentAddressError || contactAddressError>error</#if>">                         
		          </section>
		          
		          <!-- Qualifications -->
		          <section id="qualificationsSection" class="folding form-rows orange">
		          </section>
		          
		          <!-- Employment -->
		          <section id="positionSection" class="folding form-rows yellow">
		          </section>
		          
		          <!-- Funding -->
		          <section id="fundingSection" class="folding form-rows green">                     
		          </section>
		          
		          <!-- References -->
		          <section id="referencesSection" class="folding form-rows navy <#if refereesError> error</#if>">
		          </section>
		          
		          <!-- documents -->
		          <section id="documentSection" class="folding form-rows blue <#if personalStatementError>error</#if>">              
		          </section>
		          
		          <!-- Additional Information -->
		          <section id="additionalInformationSection" class="folding form-rows lightblue <#if additionalInformationError> error</#if>">
		          </section>
		
		          <hr />
		          
		          <#if applicationForm.isInState('UNSUBMITTED')>
			          <!-- Terms -->
			          <section id="acceptTermsSection" class="folding form-rows lightgrey">
			          </section>
			         	<hr />
		          </#if>  
		
		          <div class="buttons">
		            <#if applicationForm.isSubmitted() && !applicationForm.isDecided() && !applicationForm.isWithdrawn() && user.isInRole('APPLICANT') >
		            <form id="withdrawApplicationForm" action="<@spring.url "/withdraw"/>" method="POST">
		              <input type="hidden" id="wapplicationFormId" name="applicationId" value="${applicationForm.applicationNumber}"/>
		              <button id="saveAndClose" type="button" class="btn btn-large">Save &amp; Close</button>
		            </form>                                      
		            <#elseif !applicationForm.isSubmitted() && user.isInRole('APPLICANT')>                     
		            <form id="submitApplicationForm" action="<@spring.url "/submit"/>" method="POST">
		              <input type="hidden" id="applicationFormId" name="applicationId" value="${applicationForm.applicationNumber}"/>
		              <button id="saveAndClose" type="button" class="btn btn-large">Save &amp; Close</button>
		              <button id="submitAppButton" type="button"  class="btn btn-primary btn-large">Submit</button>
		            </form>
		            <#else>
		            <form>
		              <button class="btn btn-large" id="saveAndClose" type="button">Save &amp; Close</a>
		            </form>
		            </#if>
		          </div>
		          		          
	         	</div>
						<div class="tab-page" id="timeline">
		
						</div>
					</div><!-- timleline -->
			
        </div><!-- .content-box-inner -->

      </div><!-- .content-box -->

    </article>

  </div>

  <#include "/private/common/global_footer.ftl"/>

</div>

</body>
</html>