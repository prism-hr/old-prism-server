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
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/modal_window.css' />"/>
<!-- Styles for Application List Page -->

<!--[if lt IE 9]>
<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->

<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/admin/changeState.js' />"></script> 
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
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
					<#include "/private/common/modal_window.ftl">
					<#include "/private/common/parts/application_info.ftl"/>
					
					<section id="commentsection" class="form-rows">
						<h2 class="no-arrow">
						<#if applicationForm.isInState('VALIDATION')>
							Validate Application
						<#elseif applicationForm.isInState('REVIEW')>
							Review Application
						<#elseif applicationForm.isInState('INTERVIEW')>
							Evaluate Interview Outcomes
						<#elseif applicationForm.isInState('APPROVAL')>
							Approve Or Reject Application
						</#if>
						</h2>
		
						<div>
							<form>
							
								<div class="section-info-bar">
								<#if applicationForm.isInState('VALIDATION')>
									Validate the application here. You may <a href="#" id="notifyRegistryButton">refer the application to admissions</a> if you feel unable to assess the Applicant's eligbility.
								<#elseif applicationForm.isInState('REVIEW')>
									Evaluate the reviewers' comments and decide which stage to progress the application to.
								<#elseif applicationForm.isInState('INTERVIEW')>
									Evaluate the interviewers' comments and decide which stage to progress the application to.
								<#elseif applicationForm.isInState('APPROVAL')>
									Evaluate the application here and decide which stage to progress the application to.
								</#if>
								</div>
			
								<div class="row-group">
								
									<div class="row">
										<span class="plain-label">Comments<em>*</em></span>
										<span class="hint" data-desc="<@spring.message 'validateApp.comment'/>"></span>
										<div class="field">		            				
											<textarea id="comment" name="comment" class="max" rows="6" cols="80" maxlength='5000'>${(comment.comment?html)!}</textarea>
											<@spring.bind "comment.comment" /> 
											<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
										</div>
									</div>
	
								</div>
									
								<div class="row-group">
									<#include "comment/documents_snippet.ftl"/>
								</div><!-- close .row-group -->
	
								<#if applicationForm.isInState('VALIDATION')>
								<div class="row-group">
								
									<div class="row">
										<label class="plain-label">Is the applicant qualified for postgraduate study at UCL?<em>*</em></label>
										<span class="hint" data-desc="<@spring.message 'validateApp.qualified'/>"></span>
										<div class="field">		            				
											<#list validationQuestionOptions as option>
											<label><input type="radio" name="qualifiedForPhd" value="${option}"
											<#if comment.qualifiedForPhd?? && comment.qualifiedForPhd == option> checked="checked"</#if>
											/> ${option.displayValue}</label>
											</#list>
											<@spring.bind "comment.qualifiedForPhd" /> 
											<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
										</div>
									</div>
	
									<div class="row">
										<label class="plain-label">Does the applicant meet the required level of English language competence?<em>*</em></label>
										<span class="hint" data-desc="<@spring.message 'validateApp.english'/>"></span>
										<div class="field">		            				
											<#list validationQuestionOptions as option>
											<label><input type="radio" name="englishCompentencyOk" value="${option}"
											<#if comment.englishCompentencyOk?? && comment.englishCompentencyOk == option> checked="checked"</#if>/> ${option.displayValue}</label>
											</#list>
											<@spring.bind "comment.englishCompentencyOk" /> 
											<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
										</div>
									</div>
	
									<div class="row">
										<label class="plain-label">What is the applicant's fee status?<em>*</em></label>
										<span class="hint" data-desc="<@spring.message 'validateApp.feeStatus'/>"></span>
										<div class="field">		            				
											<#list homeOrOverseasOptions as option>
											<label><input type="radio" name="homeOrOverseas" value="${option}"
											<#if comment.homeOrOverseas?? && comment.homeOrOverseas == option> checked="checked"</#if>/> ${option.displayValue}</label>
											</#list>
											<@spring.bind "comment.homeOrOverseas" /> 
											<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
										</div>
									</div>
	
<#--
									<div class="row">
										<div class="registry"><#if applicationForm.getNotificationForType('REGISTRY_HELP_REQUEST')?? >The registry was last contacted on ${applicationForm.getNotificationForType('REGISTRY_HELP_REQUEST').date?string('dd MMM yyyy')}.</#if></div>
										<button class="blue registry" type="button" id="notifyRegistryButton">Request registry assistance</button>
										<div id="emailMessage"  class="registry"></div>
									</div>
-->								
								</div><!-- close .row-group -->
								</#if>
	
								<div class="row-group">
									<div class="row">
										<label class="plain-label">Next Stage<em>*</em></label>
										<span class="hint" data-desc="<@spring.message 'validateApp.nextStage'/>"></span>
										<div class="field">		            				
											<select class="max" name="status" id="status">
												<option value="">Select...</option>
												<#list stati as status>
												<option value="${status}"
												<#if  comment.nextStatus?? && comment.nextStatus == status>
													selected="selected"
												</#if> >${status.displayValue()}</option>               
												</#list>
											</select>	
											<@spring.bind "comment.nextStatus" /> 
											<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
										</div>
									</div>
									<#if reviewersWillingToInterview??>		            			
										<div class="row">
											<label id="delegateLabel" class="plain-label grey-label">Delegate Application Processing</label>
											<span class="hint" data-desc="<@spring.message 'validateApp.delegate'/>"></span>
											<div class="field">		   
												<form id="delegateForm" method="POST" action="<@spring.url '/delegate' />">   
													<input type="hidden" name="applicationId" value="${(applicationForm.applicationNumber)!}"/>     				
													<select class="max" name="applicationAdministrator" id="applicationAdministrator" disabled="disabled">
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
									<button class="clear" type="button" value="cancel">Clear</button>
									<button class="blue" type="button" id="changeStateButton" value="save">Submit</button>
									<#if user.isInRoleInProgram('APPROVER', applicationForm.program) && applicationForm.isInState('APPROVAL')>
										<button class="blue" type="button" id="requestRestartButton" value="requestRestart">Request restart of approval</button>
									</#if>
								</div>
								
							</form>
							
							<#if applicationForm.isInState('VALIDATION')>
						 		<form id="stateChangeForm" method ="POST" action="<@spring.url '/progress/submitValidationComment' />">
						 	<#else>
						 		<form id="stateChangeForm" method ="POST" action="<@spring.url '/progress/submitEvaluationComment' />">
						 	</#if>
						 		 <input type="hidden" id="applicationId" name ="applicationId" value =  "${(applicationForm.applicationNumber)!}"/>
						 		 <input type="hidden" id="commentField" name="comment" value=""/>				
						 		 <input type="hidden" id="nextStatus" name="nextStatus"  value=""/>
						 		 <#if applicationForm.isInState('VALIDATION')>
									<input type="hidden" id="commentType" name="type" value="VALIDATION"/>
									<#elseif applicationForm.isInState('REVIEW')>
									<input type="hidden" id="commentType" name="type" value="REVIEW_EVALUATION"/>
									<#elseif applicationForm.isInState('INTERVIEW')>
									<input type="hidden" id="commentType" name="type" value="INTERVIEW_EVALUATION"/>
									<#elseif applicationForm.isInState('APPROVAL')>
									<input type="hidden" id="commentType" name="type" value="APPROVAL_EVALUATION"/>
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