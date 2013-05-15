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
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/terms_and_condition.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/state_transition.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/jquery-ui-1.8.23.custom.css' />"/>
<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>

<!-- Styles for Application List Page -->

<!--[if lt IE 9]>
<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->

<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery-ui-1.8.23.custom.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/admin/changeState.js' />"></script> 
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
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
					
                    
                    
					<section id="commentsection" class="form-rows">
						<h2 class="no-arrow">
						<#if user.isInRole('ADMITTER')>
							Confirm Eligibility
						<#else>
							<#if applicationForm.isInState('VALIDATION')>
								Validate Application
							<#elseif applicationForm.isInState('REVIEW')>
								Evaluate Application Reviews
							<#elseif applicationForm.isInState('INTERVIEW')>
								Evaluate Interview Outcomes
							<#elseif applicationForm.isInState('APPROVAL')>
								Approve Application
							</#if>
						</#if>
						</h2>
		
						<div>
							<form>
								<@spring.bind "comment.confirmNextStage" />
							    <#if spring.status.errorMessages?size &gt; 0>
						     		<div class="alert alert-error" > <i class="icon-warning-sign"></i>
							    <#else>
							        <div class="alert alert-info"> <i class="icon-info-sign"></i>
							    </#if>
                                
								<#if user.isInRole('ADMITTER')>
									Confirm the applicant eligibility
								<#else>
									<#if applicationForm.isInState('VALIDATION')>
										Validate the application here.
									<#elseif applicationForm.isInState('REVIEW')>
										Evaluate the reviewers' comments and decide which stage to progress the application to.
									<#elseif applicationForm.isInState('INTERVIEW')>
										Evaluate the interviewers' comments and decide which stage to progress the application to.
									<#elseif applicationForm.isInState('APPROVAL')>
										Evaluate the application here and decide which stage to progress the application to.
									</#if>
								</#if>
								
								</div>
			
								<div class="row-group">
								
									<div class="row">
										<label class="plain-label" for="comment">Comments<em>*</em></label>
										<span class="hint" data-desc="<@spring.message 'validateApp.comment'/>"></span>
										<div class="field">		            				
											<textarea id="comment" name="comment" class="max" rows="6" cols="80" maxlength='2000'>${(comment.comment?html)!}</textarea>
											<@spring.bind "comment.comment" /> 
											<#list spring.status.errorMessages as error> 
                                              <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                                                ${error}
                                              </div>
                                            </#list>
										</div>
									</div>
	
								</div>
									
								<div class="row-group">
									<#include "comment/documents_snippet.ftl"/>
								</div><!-- close .row-group -->
	
								<#if applicationForm.isInState('VALIDATION') || user.isInRole('ADMITTER')>
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
											<#list spring.status.errorMessages as error> 
                                             <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                                                ${error}
                                              </div>
                                             </#list>
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
											<#list spring.status.errorMessages as error>  <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                                                ${error}
                                              </div></#list>
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
											<#list spring.status.errorMessages as error>  <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                                                ${error}
                                              </div></#list>
										</div>
									</div>
	
							
								</div><!-- close .row-group -->
								
								<#if user.isNotInRole('ADMITTER')>
									<div class="row-group">
	    								<div class="row">
	                                        <label for="closingDate" class="plain-label">Assign to Closing Date</label>
	    								    <span class="hint" data-desc="<@spring.message 'badge.closingDate'/>"></span>
	                                        <div class="field">
	                                            <input type="text" class="half date" value="${(closingDate?string('dd MMM yyyy'))!}" name="closingDate" id="closingDate"/>
	                                            <#if closingDate_error??>
	                                                <span class="invalid">${closingDate_error}</span>
	                                            </#if>
	                                        </div>
	                                    </div>
	                                    <div class="row">
	                                        <label class="plain-label" for="projectTitle">Assign to Project</label>
	                                        <span class="hint" data-desc="<@spring.message 'badge.projectTitle'/>"></span>
	                                        <div class="field">
	                                            <input id="projectTitle" name="projectTitle" class="full ui-autocomplete-input" type="text" value="${(projectTitle)!}" autocomplete="off" role="textbox" aria-autocomplete="list" aria-haspopup="true">
	                                            <#if projectTitle_error??>
	                                                <span class="invalid">${projectTitle_error}</span>
	                                            </#if>
	                                        </div>
	                                     </div>
									</div><!-- close .row-group -->
								  </#if>
								
								</#if>
	
								<div class="row-group" <#if user.isInRole('ADMITTER')>style="display: none;"</#if>>
									<div class="row">
										<label class="plain-label" for="status">Next Stage<em>*</em></label>
										<span class="hint" data-desc="<@spring.message 'validateApp.nextStage'/>"></span>
										<div class="field" >		            				
											<select class="max" name="status" id="status">
												<option value="">Select...</option>
												<#list stati as status>
												    <option value="${status}" <#if comment.nextStatus?? && comment.nextStatus == status> selected="selected" </#if> >${status.displayValue()}</option>               
												</#list>
											</select>	
											<@spring.bind "comment.nextStatus" /> 
											<#list spring.status.errorMessages as error>  <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                                                ${error}
                                              </div></#list>
										</div>
									</div>
								</div><!-- close .row-group -->
								
								<div class="row-group" id="interviewDelegation" style="display:none">
								  <#include "/private/staff/admin/interview_delegation.ftl"/>
								</div>
								
							    <@spring.bind "comment.confirmNextStage" />
							    <#if spring.status.errorMessages?size &gt; 0>
						     		<div class="alert alert-error" >
							    <#else>
							        <div class="alert" <#if user.isInRole('ADMITTER')>style="display: none;"</#if>>
							    </#if>
									<div class="row">
										<label id="confirmNextStageLabel" class="terms-label" for="confirmNextStage">
											Confirm that you wish to move this application to the next stage.				
										</label>
										<div class="terms-field" >
											<input type="checkbox" name="confirmNextStage" id="confirmNextStage"/>
										</div>
										<input type="hidden" name="confirmNextStageValue" id="confirmNextStageValue"/>
									</div>
								</div>
                              		
								<div class="buttons">
								    <button class="btn btn-primary" type="button" id="changeStateButton" value="save">Submit</button>
								</div>
								
							</form>
							
							<#if user.isInRole('ADMITTER') >
						 		<form id="stateChangeForm" method ="POST" action="<@spring.url '/progress/submitRegistryValidationComment' />">
							<#elseif applicationForm.isInState('VALIDATION')>
						 		<form id="stateChangeForm" method ="POST" action="<@spring.url '/progress/submitValidationComment' />">
						 	<#else>
						 		<form id="stateChangeForm" method ="POST" action="<@spring.url '/progress/submitEvaluationComment' />">
						 	</#if>
						 		 <input type="hidden" id="applicationId" name ="applicationId" value =  "${(applicationForm.applicationNumber)!}"/>
						 		 <input type="hidden" id="commentField" name="comment" value=""/>				
						 		 <input type="hidden" id="nextStatus" name="nextStatus"  value=""/>
						 		 <#if user.isInRole('ADMITTER') >
									<input type="hidden" id="commentType" name="type" value="ADMITTER_COMMENT"/>
						 		 	<#elseif applicationForm.isInState('VALIDATION')>
									<input type="hidden" id="commentType" name="type" value="VALIDATION"/>
									<#elseif applicationForm.isInState('REVIEW')>
									<input type="hidden" id="commentType" name="type" value="REVIEW_EVALUATION"/>
									<#elseif applicationForm.isInState('INTERVIEW')>
									<input type="hidden" id="commentType" name="type" value="INTERVIEW_EVALUATION"/>
									<#elseif applicationForm.isInState('APPROVAL')>
									<input type="hidden" id="commentType" name="type" value="APPROVAL_EVALUATION"/>
								</#if>
								<input type="hidden" id="delegate" name ="delegate" value="false"/>
								<input type="hidden" id="firstName" name ="firstName"/>
								<input type="hidden" id="lastName" name ="lastName"/>
								<input type="hidden" id="email" name ="email"/>
								<input type="hidden" id="confirmNextStageField" name="confirmNextStage" />
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