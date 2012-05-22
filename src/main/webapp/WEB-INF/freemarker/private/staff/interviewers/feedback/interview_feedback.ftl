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
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/state_transition.css' />"/>
				<!-- Styles for Application List Page -->

		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	
	    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/interviewer/comment/interviewComment.js' />"></script> 
	    
	    
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
				       	 <!--	if user is reviewer in program and haven't already declined-->	
				       	 	<#if !user.hasRespondedToProvideInterviewFeedbackForApplication(applicationForm)  >			        		
							
							    <h1>Interview feedback</h1>
							    <br/>
							   <p style="color:red;">Please note that once you submit your feedback you cannot re-submit or edit it.</p> 
							     <form>
							   
							    	<input type="hidden" name="applicationId" id="applicationId" value =  "${(applicationForm.id?string('#####'))!}"/>
			            			<div class="row">
										<label class="plain-label">Decline</label>
										<div class="field">        
	           								<input type="checkbox" name="declineCB" id="declineCB"/>
	           								<input type="hidden" name="declineValue" id="declineValue"/>
	   								 	</div>
	   								 </div>
	   								<div class="row"> 
			           					<span id="comment-lbl" class="plain-label">Comment<em>*</em></span>
			            				<div class="field">		            				
			            					<textarea name="comment" id="interview-comment" class="max" rows="6" cols="80" maxlength='5000'></textarea>
			            					<@spring.bind "comment.comment" /> 
	                						<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
			            				</div>
									</div>
	   								<div class="row">
			            				<span id="supervise-lbl" class="plain-label">Willing to supervise?<em>*</em></span>
										<div class="field">
											<label><input type="radio"   name="willingRB" value="TRUE" id="willingRB_true"/> Yes</label> 
											<label><input type="radio"  name="willingRB" value="FALSE" id="willingRB_false"/> No</label> 
											<@spring.bind "comment.willingToSupervice" /> 
	                						<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
										</div>
			    					</div>
	   								<div class="row">
			    					<span id="suitable-lbl" class="plain-label">Is candidate suitable for UCL?<em>*</em></span>
										<div class="field">
											<label><input type="radio"  name="suitableRB" value="TRUE" id="suitableRB_true"/> Yes</label> 
											<label><input type="radio"  name="suitableRB" value="FALSE" id="suitableRB_false"/> No</label> 
											<@spring.bind "comment.suitableCandidate" /> 
	                						<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
										</div>
									</div>
	   				
			    
			            			<div class="buttons">						        		
			            				<button type="button" id="cancelInterviewFeedbackBtn" value="cancel">Cancel</button>
							       		<button class="blue" id="submitInterviewFeedback" type="button" value="Submit">Submit</button>						        
									</div>
									</form>
			  					<hr/>
			  				<#else>
			  					<input type="hidden" name="applicationId" id="applicationId" value =  "${(applicationForm.id?string('#####'))!}"/>
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