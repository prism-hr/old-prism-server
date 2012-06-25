<section id="reviewcommentsectopm" >					          	
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
	    <script type="text/javascript" src="<@spring.url '/design/default/js/reviewer/comment/reviewComment.js' />"></script> 
	    <script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/ajaxfileupload.js'/>"></script>
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
						 <!--	if user is reviewer in program and haven't already declined-->	
							<#if !user.hasRespondedToProvideReviewForApplicationLatestRound(applicationForm)>			        		
					
							<section class="form-rows">
								<h2>Review Application</h2>
								<div>
									<form id ="reviewForm" method="POST" action= "<@spring.url '/reviewFeedback'/>"/>
									
										<input type="hidden" name="applicationId" id="applicationId" value =  "${(applicationForm.applicationNumber)!}"/>
										
										<div class="section-info-bar">
											Provide an assessment of the applicant's suitability for postgraduate study and for their chosen study programme.
										</div>
	
<#--
										<div class="row-group">
											<div class="row">
												<label class="plain-label">Decline</label>
												<span class="hint" data-desc=""></span>
												<div class="field">        
													<input type="checkbox" name="decline" id="decline"/>	           								
												</div>
											</div>
										</div>
-->
											
										<div class="row-group">
											<div class="row"> 
												<span id="comment-lbl" class="plain-label">Comment<em>*</em></span>
												<span class="hint" data-desc=""></span>
												<div class="field">		            				
													<textarea name="comment" id="review-comment" class="max" rows="6" cols="80" maxlength='5000'>${(comment.comment?html)!}</textarea>
													<@spring.bind "comment.comment" /> 
													<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
												</div>
											</div>
										</div>
											
										<div class="row-group">
											<#include "/private/staff/admin/comment/documents_snippet.ftl"/>
											<h3>Applicant Suitability</h3>
											
											<div class="row">
												<span id="suitable-lbl" class="plain-label">Is the applicant suitable for postgraduate study at UCL?<em>*</em></span>
												<span class="hint" data-desc=""></span>
												<div class="field">
													<label><input type="radio" name="suitableCandidate" value="true" id="suitableRB_true"
													<#if comment.suitableCandidateSet && comment.suitableCandidate> checked="checked"</#if>
													/> Yes</label> 
													<label><input type="radio" name="suitableCandidate" value="false" id="suitableRB_false"
													<#if comment.suitableCandidateSet && !comment.suitableCandidate> checked="checked"</#if>
													/> No</label> 
													<@spring.bind "comment.suitableCandidate" /> 
													<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
												</div>
											</div>
										</div>
											
										<div class="row-group">
											<div class="row">
												<span id="supervise-lbl" class="plain-label">Would you like to interview the applicant with a view to working with them?<em>*</em></span>
												<span class="hint" data-desc=""></span>
												<div class="field">
													<label><input type="radio" name="willingToInterview" value="true" id="willingRB_true"
													<#if comment.willingToInterviewSet && comment.willingToInterview> checked="checked"</#if> 
													/> Yes</label> 
													<label><input type="radio" name="willingToInterview" value="false" id="willingRB_false"
													<#if comment.willingToInterviewSet && !comment.willingToInterview> checked="checked"</#if>
													/> No</label> 
													<@spring.bind "comment.willingToInterview" /> 
													<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
												</div>
											</div>
	
										</div>
	
										<div class="buttons">						        		
											<button type="button" id="cancelReviewBtn" value="cancel">Cancel</button>
											<button class="blue" id="submitReviewFeedback" type="submit" value="Submit">Submit</button>						        
										</div>
									</form>
									
								</div>							
							</section>
							
							<hr/>
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
</section>