<#-- Assignments -->

<#if model.user.isInRole('APPLICANT')>
	<#assign formDisplayState = "close"/>
<#else>
	<#assign formDisplayState = "open"/>
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
			      
			      		<!-- content box -->
				    	<div class="content-box">
					        <div class="content-box-inner">
			
					            <p style="color:red;">${model.message!}</p>
								<div id="programme-details">
			          
						          	<div class="row">
						            	<label>Programme Name</label>
						              <input disabled size="109" value="${model.applicationForm.project.program.code} - ${model.applicationForm.project.program.title}" />
						            </div>
						            
						          	<div class="row half">
						            	<label>Application Number</label>
						              <input id="applicationNumber" disabled size="20" value="${model.applicationForm.id?string("######")}" />
						            </div>
				            
					          	</div>
			          
					            <hr/>
			
					          	<section id="programmeDetailsSection" class="folding violet">
					          		<#include "/private/pgStudents/form/components/programme_details.ftl"/>
					          	</section>
			          
			          			<section id="personalDetailsSection" class="folding purple">
			             			<#include "/private/pgStudents/form/components/personal_details.ftl"/>
			          			</section>
			          
			          			<!-- Address -->
			          			<section id="addressSection" class="folding red">
			             			<#include "/private/pgStudents/form/components/address_details.ftl"/>
			          			</section>
			          
			           			<section id="qualificationsSection" class="folding orange">
			            			<#include "/private/pgStudents/form/components/qualification_details.ftl"/>
			          			</section>
			          
			           			<section id="positionSection" class="folding yellow">
			             			<#include "/private/pgStudents/form/components/employment_position_details.ftl"/>
			          			</section>
			          
			           			<section id="fundingSection" class="folding green">
			             			<#include "/private/pgStudents/form/components/funding_details.ftl"/>
			          			</section>
			          
			           			<section id="referencesSection" class="folding navy">
			           				<#include "/private/pgStudents/form/components/references_details.ftl"/>
			          			</section>
			          
			          			<section class="folding blue">
									<#include "/private/pgStudents/form/components/documents.ftl"/>
			          			</section>
			          
			          			<section class="folding lightblue">
			          				<#include "/private/pgStudents/form/components/additional_information.ftl"/>
			          			</section>

			          			<hr/>
			          
			          			<a class="button" href="<@spring.url '/applications'/>">Close</a>
			          			<#if !model.applicationForm.isSubmitted() && model.user.isInRole('APPLICANT')>
			             			
			             			<a id="submitButton" class="button">Submit</a>
			             			
			             			<form id="submitApplicationForm" action="<@spring.url "/submit"/>" method="POST">
			          	      			<input type="hidden" id="applicationFormId" name="applicationFormId" 
			          	      									value="${model.applicationForm.id?string("######")}"/>
									</form>
									
								</#if>
								<p></p>		    	 
			          			<#include "/private/common/feedback.ftl"/>
			        
			        		</div><!-- .content-box-inner -->
			      
			      		</div><!-- .content-box -->
			  
			    	</article>
		
				</div>
			  
			  <#include "/private/common/global_footer.ftl"/>
		
		</div> 
		
		<!-- Scripts -->
			
		<script type='text/javascript' language="javascript" src="<@spring.url '/dwr/engine.js'/>"></script>
	    <script type='text/javascript' language="javascript" src="<@spring.url '/dwr/util.js'/>"></script>
			
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/formActions.js'/>"></script>
		
	</body>
	
</html>
