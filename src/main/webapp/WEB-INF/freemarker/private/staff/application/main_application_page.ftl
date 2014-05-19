<#-- Assignments -->

<#if user.isInRole('APPLICANT')>
  <#assign formDisplayState = "close"/>
<#else>
  <#assign formDisplayState = "open"/>
</#if>

<#-- Personal Details Rendering -->

<!DOCTYPE HTML>
<#setting locale = "en_US">
<#import "/spring.ftl" as spring />
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="prism.version" content="<@spring.message 'prism.version'/>" >

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
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/timeline.css' />"/>

<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>

<!-- Scripts -->
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/timeline_application.js'/>"></script>

<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>

<!--[if lt IE 9]>
<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->
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
  
    <#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
     <@header/>
    <!-- Main content area. -->
    <article id="content" role="main">
      
    <!-- content box -->
	<div class="content-box">
      	<div class="content-box-inner">
        
        	<#include "/private/common/parts/application_info.ftl"/>
          	<div id="timelineview">
	    		<ul class="tabs">				
					<li class="current"><a href="#application" id="applicationBtn">Application</a></li>
					<li><a href="#timeline" id="timelineBtn">Timeline</a></li>
      				<li><a href="#opportunity" id="opportunityBtn">Opportunities</a></li>
				</ul>
				<div class="tab-page" id="applicationTab">
		        	<#include "/private/staff/application/main_application_view.ftl"/>
	         	</div>
				<div class="tab-page" id="timelineTab"></div>
	        	<div class="tab-page" id="opportunityTab"></div>
			</div><!-- timlelint -->
			
      	</div><!-- .content-box-inner -->
	</div><!-- .content-box -->
    
    </article>
  
  </div>
  <input type="hidden" name="applicationId" id="applicationId" value =  "${(applicationForm.applicationNumber)!}"/>
  <#include "/private/common/global_footer.ftl"/>
  
</div>

</body>
</html>