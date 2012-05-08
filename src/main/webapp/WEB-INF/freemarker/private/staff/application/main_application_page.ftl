<#-- Assignments -->

<#if user.isInRole('APPLICANT')>
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
		<title>UCL Postgraduate Admissions</title>
		
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
		
		<!-- Styles for Application List Page -->
		
		<!-- Scripts -->
		

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
			    
			    <!-- "Tools" -->
                <div id="tools">
                    <ul class="left">
                        <li class="icon-print"><a href="<@spring.url '/print?applicationFormId=${applicationForm.id?string("######")}'/>">Print Page</a></li>
                    </ul>
    
                <ul class="right">
                    <li class="icon-progress"><a href="#">Progress</a></li>
                    <li class="current icon-application"><a href="#">View Application</a></li>
                </ul>
                </div>
			      
			      
			      <!-- FLOATING TOOLBAR -->
		          <ul id="view-toolbar">
		          	<li class="top"><a href="javascript:backToTop();" title="Back to top">Back to top</a></li>
		          	<li class="print"><a href="<@spring.url '/print?applicationFormId=${applicationForm.id?string("######")}'/>" title="Print">Print</a></li>
		          </ul>
			      
			      
			      <!-- content box -->
			      <div class="content-box">
			        <div class="content-box-inner">
			
				 	 <#include "/private/common/parts/application_info.ftl"/>
			
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
			          
			          
			          <hr />
			          
			          <a class="button" href="<@spring.url '/applications'/>">Close</a>
			          
			    	 <p></p>
			          <#include "/private/common/feedback.ftl"/>
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
