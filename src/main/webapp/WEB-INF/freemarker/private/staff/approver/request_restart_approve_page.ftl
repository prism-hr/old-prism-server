<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<html>

	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta name="prism.version" content="<@spring.message 'prism.version'/>" >
		
		<title>UCL Postgraduate Admissions</title>

		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />
		
		<!-- Styles for Application List Page -->
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/state_transition.css' />"/>
        <link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
        
        <!-- Styles for Application List Page -->

		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	
	    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
        <script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/admin/comment/upload.js'/>"></script>
    
        <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
		<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
	    
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
	
		<!-- Wrapper Starts -->
		<div id="wrapper">

			<#include "/private/common/global_header.ftl"/>
			
			 <!-- Middle Starts -->
			<div id="middle">
			
				<#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
				       <@header/>
				    <!-- Main content area. -->
				    <article id="content" role="main">		    
				      
					  <!-- "Tools" 
					  <div id="tools">
						<ul class="left">
						  <li class="icon-print"><a target="_blank" title="Click to Download" href="<@spring.url '/print?applicationFormId=${applicationForm.applicationNumber}'/>">Download PDF</a></li>
						  <li class="icon-feedback"><a title="Send Feedback" href="mailto:prism@ucl.ac.uk?subject=Feedback" target="_blank">Send Feedback</a></li>
						</ul>
					  </div>-->
      
				      <!-- content box -->				      
				      <div class="content-box">
				        <div class="content-box-inner">
				         <#include "/private/common/parts/application_info.ftl"/>
				        
				       	 	<#if user.isInRoleInProgram("APPROVER", applicationForm.program) >
									<section class="form-rows">
										<h2 class="no-arrow">
											Request Restart Of Approval Phase
										</h2>
										<div>
											<form method="POST" action="<@spring.url '/approval/submitRequestRestart'/>">
												<div class="row-group">
													<input type="hidden" name="applicationId" id="applicationId" value="${(applicationForm.applicationNumber)!}"/>
													<div class="row">
														<label class="plain-label" for="comment">Reason<em>*</em></label>
														<span class="hint" data-desc="<@spring.message 'approval.restartReason'/>"></span>
														<div class="field">		            				
															<textarea id="comment" name="comment" class="max" rows="6" cols="80"></textarea>
															<@spring.bind "comment.comment" /> 
															<#list spring.status.errorMessages as error> <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div></#list>
														</div>
													</div>
												</div>
												
												<div class="row-group">													
													<#include "/private/staff/admin/comment/documents_snippet.ftl"/>
												</div>
												
												<div class="buttons">						        		
													<button class="btn btn-primary" id="submitRequestRestart" type="submit" value="Submit">Submit</button>						        
												</div>
											</form>
											
										</div>
									</section>
			  				<#else>
			  					<input type="hidden" name="applicationId" id="applicationId" value="${(applicationForm.applicationNumber)!}"/>
		  					</#if>
		  					<#include "/private/staff/admin/comment/timeline_application.ftl"/>
		  					
				        </div><!-- .content-box-inner -->
				      </div><!-- .content-box -->
				      
				  
				    </article>
				
				
				
			</div>
			<!-- Middle Ends -->
			
			<#include "/private/common/global_footer.ftl"/>
			
		</div>
		<!-- Wrapper Ends -->
		   
	</body>
</html>