<#-- Assignments -->

<#if model.user.isInRole('APPLICANT')>
	<#assign formDisplayState = "close"/>
<#else>
	<#assign formDisplayState = "open"/>
</#if>

<#if model.message?has_content>
	<#assign globalMsg = true/>
<#else>
	<#assign globalMsg = false/>
</#if>

<#-- Personal Details Rendering -->

<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Shell template</title>
		
		<!-- Styles for Application Page -->		
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

		<div id="wrapper">
			
			  <#include "/private/common/global_header.ftl"/>
			  
			  <!-- Middle. -->
			  <div id="middle">
			  		
			  		<#include "/private/common/parts/nav_with_user_info.ftl"/>
			    
			  		<!-- Main content area. -->
			    	<article id="content" role="main">
			    	
			    	<div id="tools">
                        <ul class="left">
                            <li class="icon-print"><a href="<@spring.url '/print?applicationFormId=${model.applicationForm.id?string("######")}'/>">Print Page</a></li>
                        </ul>
                    </div>
			    	
			    	<!-- FLOATING TOOLBAR -->
                  <ul id="view-toolbar">
                    <li class="top"><a href="javascript:backToTop();" title="Back to top">Back to top</a></li>
                    <li class="print"><a href="<@spring.url '/print?applicationFormId=${model.applicationForm.id?string("######")}'/>" title="Print">Print</a></li>
                  </ul>
			      
			      		<!-- content box -->
				    	<div class="content-box">
					        <div class="content-box-inner">
								<#if globalMsg>
					            	<p class="invalid">${model.message!}</p>
					            </#if>
								<div id="programme-details">
			          
						          	<div class="row">
						            	<label>Programme</label>
						              <input disabled size="109" value="${model.applicationForm.project.program.code} - ${model.applicationForm.project.program.title}" />
						            </div>
						            
						          	<div class="row half">
						            	<label>Application Number</label>
						              <input id="applicationNumber" disabled size="20" value="${model.applicationForm.id?string("######")}" />
						            </div>
						            <#if model.applicationForm.isSubmitted()>
						          	<div class="row">
						            	<label>Date Submitted</label>
						              <input id="applicationNumber" disabled size="20" value="${(model.applicationForm.submittedDate?string("dd-MMM-yyyy hh:mm a"))!}" />
						            </div>
						            </#if>
				            
					          	</div>
			          
					            <hr/>
								<input type="hidden" id="applicationId" name="applicationId" value="${model.applicationForm.id?string("######")}"/>
					          	<section id="programmeDetailsSection" class="folding violet <#if model.hasError('programmeDetails')>error</#if>">					          	
					          		<#include "/private/pgStudents/form/components/programme_details.ftl"/>
					          	</section>
			          
			          			<section id="personalDetailsSection" class="folding purple <#if model.hasError('personalDetails')>error</#if>">
			             			<#include "/private/pgStudents/form/components/personal_details.ftl"/>
			          			</section>
			          
			          			<!-- Address -->			          			
			          			<section id="addressSection" class="folding red <#if model.hasError('numberOfContactAddresses') || model.hasError('numberOfAddresses')>error</#if>">
			             			<#include "/private/pgStudents/form/components/address_details.ftl"/>
			          			</section>
			          
			           			<section id="qualificationsSection" class="folding orange">
			            		
			          			</section>
			          
			           			<section id="positionSection" class="folding yellow">
			             			<#include "/private/pgStudents/form/components/employment_position_details.ftl"/>
			          			</section>
			          
			           			<section id="fundingSection" class="folding green">
			             			<#include "/private/pgStudents/form/components/funding_details.ftl"/>
			          			</section>
			          
			           			<section id="referencesSection" class="folding navy <#if model.hasError('numberOfReferees')>error</#if>">
			           				<#include "/private/pgStudents/form/components/references_details.ftl"/>
			          			</section>
			          
			          			<section class="folding blue <#if model.uploadErrorCode?? || model.uploadTwoErrorCode?? || model.hasError('supportingDocuments') >error</#if>">
									<#include "/private/pgStudents/form/components/documents.ftl"/>
			          			</section>
			          
			          			<section id="additionalInformationSection" class="folding lightblue">
			          				<#include "/private/pgStudents/form/components/additional_information.ftl"/>
			          			</section>

			          			<hr/>
			          			
			          			<div class="buttons">
			          			
									<div style="float:left">		    	 
			          					<#include "/private/common/feedback.ftl"/>
			          				</div>
			          				
			          				
			          			
			          				<#if !model.applicationForm.isSubmitted() && model.user.isInRole('APPLICANT')>
			             			
			             			
			             			
			             				<form id="submitApplicationForm" action="<@spring.url "/submit"/>" method="POST">
			          	      				<input type="hidden" id="applicationFormId" name="applicationFormId" 
			          	      									value="${model.applicationForm.id?string("######")}"/>
			          	      										<a class="button" href="<@spring.url '/applications'/>">Close</a>
			          	      										<button id="submitButton" type="submit" class="button">Submit</button>
										</form>
									<#else>
										<a class="button" href="<@spring.url '/applications'/>">Close</a>
									</#if>

			          			</div>
			        
			        		</div><!-- .content-box-inner -->
			      
			      		</div><!-- .content-box -->
			  
			    	</article>
		
				</div>
			  
			  <#include "/private/common/global_footer.ftl"/>
		
		</div> 
		
		<!-- Scripts -->
			
	
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/formActions.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/common.js'/>"></script>
	</body>
	
</html>

