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
<script type="text/javascript" src="<@spring.url '/design/default/js/admin/changeState.js' />"></script> 


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
					
					
					<section class="form-rows">
						<div>
							<div class="row-group">
							
								<#if applicationForm.isInState('VALIDATION')>
								<h3>Validate application</h3>
								<#elseif applicationForm.isInState('REVIEW')>
								<h3>Evaluate reviews</h3>
								<#elseif applicationForm.isInState('INTERVIEW')>
								<h3>Evaluate interview feedback</h3>
								</#if>

								<div class="row">
									<span class="plain-label">Comment</span>
									<div class="field">		            				
										<textarea id="comment" name="comment" class="max" rows="6" cols="80" maxlength='5000'></textarea>
									</div>
									
								</div>

							</div><!-- close .row-group -->

							<#if applicationForm.isInState('VALIDATION')>
							<div class="row-group">
							
								<div class="row">
									<label class="plain-label">Is the applicant qualified for PhD entry to UCL?</label>
									<div class="field">		            				
										<#list validationQuestionOptions as option>
										<label><input type="radio" name="qualifiedForPhd" value="${option}"/> ${option.displayValue}</label>
										</#list>
									</div>
								</div>

								<div class="row">
									<label class="plain-label">Does the applicant meeting the minimum required standard of English Language competence?</label>
									<div class="field">		            				
										<#list validationQuestionOptions as option>
										<label><input type="radio" name="englishCompentencyOk" value="${option}"/> ${option.displayValue}</label>
										</#list>
									</div>
								</div>

								<div class="row">
									<label class="plain-label">What is the applicant's fee status?</label>
									<div class="field">		            				
										<#list homeOrOverseasOptions as option>
										<label><input type="radio" name="homeOrOverseas" value="${option}"/> ${option.displayValue}</label>
										</#list>
									</div>
								</div>

								<div class="row">
									<div class="registry"><#if applicationForm.getNotificationForType('REGISTRY_HELP_REQUEST')?? >The registry was last contacted on ${applicationForm.getNotificationForType('REGISTRY_HELP_REQUEST').date?string('dd-MMM-yyyy HH:mm')}.</#if></div>
									<button class="blue registry" type="button" id="notifyRegistryButton">Request registry assistance</button>
									<div id="emailMessage"  class="registry"></div>
								</div>
								
							</div><!-- close .row-group -->
							</#if>

							<div class="row-group">
								<div class="row">
									<label class="plain-label">Next stage</label>
									<div class="field">		            				
										<select class="max" name="status" id="status">
											<option value="">Select...</option>
											<#list stati as status>
											<option value="${status}" >${status.displayValue()}</option>               
											</#list>
										</select>	
									</div>
								</div>
								<#if reviewersWillingToInterview??>		            			
									<div class="row">
										<label class="plain-label">Delegate interview management to</label>
										<div class="field">		   
											<form id="delegateForm" method ="POST" action="<@spring.url '/delegate' />">   
												<input type="hidden" name = "applicationId" value =  "${(applicationForm.applicationNumber)!}"/>     				
												<select class="max" name="applicationAdministrator" id="appliationAdmin" disabled="disabled">
													<option value="">Select...</option>
													<#list reviewersWillingToInterview as reviewerWillingToInterview>
													<option value="${encrypter.encrypt(reviewerWillingToInterview.id)}" >${reviewerWillingToInterview.firstName?html} ${reviewerWillingToInterview.lastName?html}</option>               
													</#list>
												</select>	
											</form>
										</div>
									</div>
								</#if>
								
							</div><!-- close .row-group -->

							<div class="buttons">						        		
								<button type="reset" value="cancel">Cancel</button>
								<button class="blue" type="button" id="changeStateButton" value="save">Submit</button>						        
							</div>
						 	<form id="stateChangeForm" method ="POST" action="<@spring.url '/progress' />">
						 		 <input type="hidden" id="applicationId" name ="application" value =  "${(applicationForm.applicationNumber)!}"/>
						 		 <input type="hidden" id="commentField" name="comment" value=""/>				
						 		 <input type="hidden" id="nextStatus" name="nextStatus"  value=""/>
						 		 <#if applicationForm.isInState('VALIDATION')>
									<input type="hidden" id="commentType" name="type" value="VALIDATION"/>
									<#elseif applicationForm.isInState('REVIEW')>
									<input type="hidden" id="commentType" name="type" value="REVIEW_EVALUATION"/>
									<#elseif applicationForm.isInState('INTERVIEW')>
									<input type="hidden" id="commentType" name="type" value="INTERVIEW_EVALUATION"/>
								</#if>
						 	</form>
						</div>
					</section>

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