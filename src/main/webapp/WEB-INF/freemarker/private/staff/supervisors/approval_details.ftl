<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>UCL Postgraduate Admissions</title>
		
		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />
		
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/private/global_private.css'/>" />
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/private/application.css'/>" />
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/actions.css' />" />
		
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/supervisor/approval.js'/>"></script>
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

				<#include "/private/common/parts/nav_with_user_info.ftl"/>
				<@header />
				<!-- Main content area. -->
				<article id="content" role="main">

				  <!-- "Tools" -->
				  <div id="tools">
					<ul class="left">
					  <li class="icon-print"><a target="_blank" title="Click to Download" href="<@spring.url '/print?applicationFormId=${applicationForm.applicationNumber}'/>">Download PDF</a></li>
					  <li class="icon-feedback"><a title="Send Feedback" href="mailto:prism@ucl.ac.uk?subject=Feedback" target="_blank">Send Feedback</a></li>
					</ul>
				  </div>
      
					<!-- FLOATING TOOLBAR -->
		            <ul id="view-toolbar" class="toolbar">
		            	<li class="top"><a href="javascript:backToTop();" title="Back to top">Back to top</a></li>
		                <li class="print"><a target="_blank" href="<@spring.url '/print?applicationFormId=${applicationForm.applicationNumber}'/>" title="Click to Download">Print</a></li>
					</ul>

				<!-- content box -->
				<div class="content-box">
					<div class="content-box-inner">
						<#include "/private/common/parts/application_info.ftl"/>
    					<input type="hidden" id="applicationId" value="${applicationForm.applicationNumber}"/>
							
							<section class="form-rows"  id="approvalsection">
								<h2 class="no-arrow">
									Assign Supervisors
								</h2>
								<div>
									<form>
									
										<div class="section-info-bar" id="add-info-bar-div">
											Assign supervisors to the application here. You may also create new supervisors.
										</div>
									
										<div class="row-group" id="assignSupervisorsToAppSection">			
											
										</div>
										
										<!-- Create supervisor -->
										<div class="row-group" id ="createsupervisorsection">
											
										</div>
															
										<div class="buttons">
<#--
											<button value="cancel" class="clear" type="reset">Clear</button>
-->
											<button class="blue" type="button" id="moveToApprovalBtn">Submit</button>
										</div>

										<input type="hidden" id="applicationId" name="applicationId" value="${applicationForm.applicationNumber}"/>
										<input type="hidden" id="approvalRoundId" name="approvalRoundId" value="<#if approvalRound.id??>${encrypter.encrypt(approvalRound.id)}</#if>" />  

								</form>
							</div>
						</section>
						
						<div id="postApprovalData"></div>
						<form id="postSupervisorForm" method="post" <#if assignOnly?? && assignOnly> action ="<@spring.url '/approval/assignNewSupervisor'/>" <#else> action ="<@spring.url '/approval/createSupervisor'/>" </#if>>				
							
  				<#include "/private/staff/admin/comment/timeline_application.ftl"/>
				</div>
				<!-- .content-box-inner -->
		</div>
		<!-- .content-box -->

		</article>

	</div>

	<#include "/private/common/global_footer.ftl"/>

</div>

</body>
</html>
</section>
