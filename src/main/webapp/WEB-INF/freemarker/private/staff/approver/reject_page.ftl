<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>UCL Postgraduate Admissions</title>
		
		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		
		<link type="text/css" rel="stylesheet" 
			href="<@spring.url '/design/default/css/private/global_private.css'/>" />
		<link type="text/css" rel="stylesheet" 
			href="<@spring.url '/design/default/css/private/application.css'/>" />
		
		<link type="text/css" rel="stylesheet" 
			href="<@spring.url '/design/default/css/private/staff/reject.css'/>" />
		
		<link type="text/css" rel="stylesheet"
				href="<@spring.url '/design/default/css/actions.css' />" />
		
		
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

				<!-- Main content area. -->
				<article id="content" role="main">

				    <#if !user.isInRole('APPLICANT')>
				    	<#include "/private/common/parts/tools.ftl"/>
				    </#if>

					<!-- FLOATING TOOLBAR -->
		            <ul id="view-toolbar">
		            	<li class="top"><a href="javascript:backToTop();" title="Back to top">Back to top</a></li>
		                <li class="print"><a href="<@spring.url '/print?applicationFormId=${applicationForm.id?string("######")}'/>" title="Print">Print</a></li>
					</ul>

				<!-- content box -->
				<div class="content-box">
					<div class="content-box-inner">
				
						<div id="programme-details">			          
						    	
					    	<div class="row">
					        	<label class="label">Programme</label>
					           	${applicationForm.program.code} - ${applicationForm.program.title}
					        </div>
					            
					        <div class="row">
					        	<label class="label">Application Number</label>
					            ${applicationForm.id?string("######")} 
					        </div>
					        
					        <#if applicationForm.isSubmitted()>
					        	<div class="row">
					            	<label>Date Submitted</label>
					              	${(applicationForm.submittedDate?string("dd-MMM-yyyy hh:mm a"))!}
					            </div>
					        </#if>
						</div>
						<hr />
						<@spring.bind "applicationForm.*" />
						<@spring.bind "availableReasons.*" />
						<section class="form-rows violet">
							<div>
								<form method="POST" action="<@spring.url '/rejectApplication/moveApplicationToReject'/>">
									<div class="row-group">
										
										<div class="row">
											<label class="label">Reasons for rejections<em>*</em></label>
											<div id="reasonList" class="field">
												<ul>
													<#list availableReasons as reason>
													<li>
													<input type="radio" name="rejectionReason" value="${reason.id?string("#######")}" class="reason"/><label>${reason.text}</label>
													</li> 
													</#list>
												</ul>
												<@spring.bind "rejection.rejectionReason" /> 
												<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list> 
											</div>
										</div>
									</div>

									<div class="row-group">					
										<div class="row">
											<label class="label">Include link to UCL prospectus?</label>
											<div class="field">					      
												<input type="checkbox" name="includeProspectusLink" id="includeProspectusLink" class="reason"/>											
											</div>
										</div>
										
										<div class="row">
											<div class="field">
												<span>
													<button type="submit" id="rejectButton" class="blue">Reject application</button>
												</span>
											</div>
										</div>
									</div>

									<div class="row-group">
										<div class="row">
											<label class="label"> Content of the e-mail for the applicant</label>
											<div id="emailText"></div> 
										</div>
									</div>
									
									<input type="hidden" id="applicationId" name="applicationId" value="${applicationForm.id?string("######")}"/> 
								</form>
							</div>
						</section>
					
						<#include "/private/common/feedback.ftl"/>
					
					</div>
					<!-- #actions -->

				</div>
				<!-- .content-box-inner -->
		</div>
		<!-- .content-box -->

		</article>

	</div>

	<!-- Footer. -->
	<div id="footer">
		<ul>
			<li><a href="#">Privacy</a></li>
			<li><a href="#">Terms &amp; conditions</a></li>
			<li><a href="#">Contact us</a></li>
			<li><a href="#">Glossary</a></li>
		</ul>
	</div>

	</div>

	<script type="text/javascript"
		src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	<script type="text/javascript"
		src="<@spring.url '/design/default/js/libraries.js'/>"></script>
	<script type="text/javascript"
		src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	<script type="text/javascript"
		src="<@spring.url '/design/default/js/approver/reject_page.js'/>"></script>
</body>
</html>
