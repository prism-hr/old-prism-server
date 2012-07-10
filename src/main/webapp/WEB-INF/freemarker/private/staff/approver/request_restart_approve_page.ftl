<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>

	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>UCL Postgraduate Admissions</title>

		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=8,chrome=1" />
		
		<!-- Styles for Application List Page -->
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/state_transition.css' />"/>
				<!-- Styles for Application List Page -->

		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	
	    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
      <script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/admin/comment/upload.js'/>"></script>
    
	    
	    
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
			
				<#include "/private/common/parts/nav_with_user_info.ftl"/>
				       <@header/>
				    <!-- Main content area. -->
				    <article id="content" role="main">		    
				      
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
														<span class="plain-label">Reason<em>*</em></span>
														<span class="hint" data-desc="<@spring.message 'approval.restartReason'/>"></span>
														<div class="field">		            				
															<textarea name="comment" class="max" rows="6" cols="80" maxlength='5000'></textarea>
															<@spring.bind "comment.comment" /> 
															<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
														</div>
													</div>
												</div>
												
												<div class="row-group">													
													<#include "/private/staff/admin/comment/documents_snippet.ftl"/>
												</div>
												
												<div class="buttons">						        		
<#--
													<button class="clear" type="button" value="cancel">Clear</button>
-->
													<button class="blue" id="submitRequestRestart" type="submit" value="Submit">Submit</button>						        
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