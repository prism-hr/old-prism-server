<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>

	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>UCL Postgraduate Admissions</title>

		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		
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
	    <script type="text/javascript" src="<@spring.url '/design/default/js/application/common.js' />"></script>
	    
	    
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
				
				    <!-- Main content area. -->
				    <article id="content" role="main">		    
				      
				      <!-- content box -->				      
				      <div class="content-box">
				        <div class="content-box-inner">
				         <#include "/private/common/parts/application_info.ftl"/>
				        
				       	 	<#if user.hasAdminRightsOnApplication(applicationForm) >
									<section class="form-rows">
										<div>
											<form method="POST" action= "<@spring.url '/comment'/>">
												<div class="row-group">
													<h3>Add comment</h3>
													<input type="hidden" name="applicationId" id="applicationId" value =  "${(applicationForm.applicationNumber)!}"/>
													<div class="row">
														<span class="plain-label">Comment</span>
														<div class="field">		            				
															<textarea name="comment" class="max" rows="6" cols="80" maxlength='5000'></textarea>
															<@spring.bind "comment.comment" /> 
															<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
														</div>
													</div>
								
													<div class="buttons">						        		
														<button type="reset" value="cancel">Cancel</button>
														<button class="blue" type="submit" value="Submit">Submit</button>						        
													</div>
												</div>
											</form>
										</div>
									</section>
			  				<#else>
			  					<input type="hidden" name="applicationId" id="applicationId" value =  "${(applicationForm.applicationNumber)!}"/>
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