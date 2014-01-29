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
				<#assign user = stateChangeDTO.registeredUser>
				<#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
				<@header/>
				<!-- Main content area. -->
				<article id="content" role="main">
					<!-- content box -->				      
					<div class="content-box">
						<div class="content-box-inner">
							<#assign applicationForm = stateChangeDTO.applicationForm>
							<#include "/private/common/parts/application_info.ftl"/>	
							<section id="commentsection" class="form-rows">
								<h2 class="no-arrow">
									<#if stateChangeDTO.action?has_content>
										Move Application to Different Stage
									<#elseif stateChangeDTO.isInState('VALIDATION')>
										Complete Validation Stage
									<#elseif stateChangeDTO.isInState('REVIEW')>
										Complete Review Stage
									<#elseif stateChangeDTO.isInState('INTERVIEW')>
										Complete Interview Stage
									<#elseif stateChangeDTO.isInState('APPROVAL')>
										Complete Approval Stage
									</#if>
								</h2>
								<div>
									<form id="stateChangeForm" method ="POST" action="<@spring.url "/progress/submitEvaluationComment?applicationId=${(stateChangeDTO.applicationForm.applicationNumber)!}"/>">
										<@spring.bind "stateChangeDTO.confirmNextStage" />
										<div class=
											<#if spring.status.errorMessages?size &gt; 0>
												"alert alert-error" > <i class="icon-warning-sign"></i>
											<#else>
												"alert alert-info"> <i class="icon-info-sign"></i>
											</#if>
											<#if stateChangeDTO.action?has_content>
												Select a different stage to move the application to.
											<#elseif stateChangeDTO.isInState('VALIDATION')>
												Validate the application here.
											<#elseif stateChangeDTO.isInState('REVIEW')>
												Evaluate the reviewers' comments and decide which stage to progress the application to.
											<#elseif stateChangeDTO.isInState('INTERVIEW')>
												Evaluate the interviewers' comments and decide which stage to progress the application to.
											<#elseif stateChangeDTO.isInState('APPROVAL')>
												Evaluate the application here and decide which stage to progress the application to.
											</#if>
										</div>
										<div class="row-group">
											<div class="row">
												<label class="plain-label" for="comment">Comments<em>*</em></label>
												<span class="hint" data-desc="<@spring.message 'validateApp.comment'/>"></span>
												<div class="field">		            				
													<textarea id="state_change_comment" name="comment" class="max" rows="6" cols="80">${(stateChangeDTO.comment?html)!}</textarea>
													<@spring.bind "stateChangeDTO.comment" /> 
													<#list spring.status.errorMessages as error> 
														<div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error}</div>
													</#list>
												</div>
											</div>
										</div>
										<div class="row-group">
											<#assign comment = stateChangeDTO>
											<#include "comment/documents_snippet.ftl"/>
										</div>
										<#if stateChangeDTO.isInState('VALIDATION')>
											<div class="row-group">
												<div class="row">
													<label class="plain-label">Is the applicant qualified for postgraduate study at UCL?<em>*</em></label>
													<span class="hint" data-desc="<@spring.message 'validateApp.qualified'/>"></span>
													<div class="field">		            				
														<#list stateChangeDTO.validationQuestionOptions as option>
															<label>
																<input type="radio" name="qualifiedForPhd" value="${option}"
																<#if stateChangeDTO.qualifiedForPhd?? && stateChangeDTO.qualifiedForPhd == option> 
																	checked="checked"
																</#if>/>
																${option.displayValue}
															</label>
														</#list>
														<@spring.bind "stateChangeDTO.qualifiedForPhd" /> 
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
														<#list stateChangeDTO.validationQuestionOptions as option>
															<label>
																<input type="radio" name="englishCompentencyOk" value="${option}"
																<#if stateChangeDTO.englishCompentencyOk?? && stateChangeDTO.englishCompentencyOk == option>
																	checked="checked"
																</#if>/>
																${option.displayValue}
															</label>
														</#list>
														<@spring.bind "stateChangeDTO.englishCompentencyOk" /> 
														<#list spring.status.errorMessages as error>
															<div class="alert alert-error"> <i class="icon-warning-sign"></i>
																${error}
														  	</div>
													  	</#list>
													</div>
												</div>
												<div class="row">
													<label class="plain-label">What is the applicant's fee status?<em>*</em></label>
													<span class="hint" data-desc="<@spring.message 'validateApp.feeStatus'/>"></span>
													<div class="field">		            				
														<#list stateChangeDTO.homeOrOverseasOptions as option>
															<label>
																<input type="radio" name="homeOrOverseas" value="${option}"
																<#if stateChangeDTO.homeOrOverseas?? && stateChangeDTO.homeOrOverseas == option>
																	checked="checked"
																</#if>/>
																${option.displayValue}
															</label>
														</#list>
														<@spring.bind "stateChangeDTO.homeOrOverseas" /> 
														<#list spring.status.errorMessages as error>
															<div class="alert alert-error">
																<i class="icon-warning-sign"></i>
																${error}
															</div>
														</#list>
													</div>
												</div>
											</div>
											<div class="row-group" <#if !stateChangeDTO.displayCustomReferenceQuestionsOption()>style="display:none"</#if>>
												<div class="row" id="useCustomReferenceQuestions">
												    <label id="useCustomReferenceQuestionsLabel" class="plain-label normal">Do you wish to use custom reference questions?<em>*</em></label> 
												    <span class="hint" data-desc="<@spring.message 'validateApp.useCustomReferenceQuestions'/>"> </span>
												    <div class="field">
												        <input id="useCustomReferenceQuestions" type="radio" name="useCustomReferenceQuestions" value="false" <#if stateChangeDTO.useCustomReferenceQuestions?? && !stateChangeDTO.useCustomReferenceQuestions> checked="checked"</#if>/>
												            No
												        <input id="useCustomReferenceQuestions" type="radio" name="useCustomReferenceQuestions" value="true" <#if stateChangeDTO.useCustomReferenceQuestions?? && stateChangeDTO.useCustomReferenceQuestions> checked="checked"</#if>/>
												            Yes
													    <@spring.bind "stateChangeDTO.useCustomReferenceQuestions" /> 
														<#list spring.status.errorMessages as error>  
															<div class="alert alert-error"> <i class="icon-warning-sign"></i>
												            	${error}
												          	</div>
												        </#list>
												    </div>
												</div>
											</div>
										</#if>
										<div class="row-group">
											<div class="row">
												<label class="plain-label" for="selectedNextStatus">Next Stage<em>*</em></label>
												<span class="hint" data-desc="<@spring.message 'validateApp.nextStage'/>"></span>
												<div class="field" >		            				
													<select class="max" name="nextStatus" id="nextStatus">
														<option value="">Select...</option>
														<#list stateChangeDTO.stati as status>
															<option value="${status}" 
															<#if stateChangeDTO.nextStatus?has_content && stateChangeDTO.nextStatus == status> 
																selected="selected" 
															</#if>
															>${status.displayValue()}</option>
														</#list>
													</select>	
													<@spring.bind "stateChangeDTO.nextStatus" /> 
													<#list spring.status.errorMessages as error>  
														<div class="alert alert-error"> <i class="icon-warning-sign"></i>${error}</div>
													</#list>
												</div>
												<div id="customQuestionSection" <#if !stateChangeDTO.displayCustomQuestionsOption()>style="display:none"</#if>>
													<label id="useCustomQuestionsLabel" class="plain-label normal">Do you wish to use custom questions?<em>*</em></label> 
												    <span class="hint" data-desc="<@spring.message 'validateApp.useCustomQuestions'/>"> </span>
												    <div id="useCustomQuestionsDiv" class="field">
												        <input id="useCustomQuestions" type="radio" name="useCustomQuestions" value="false" <#if stateChangeDTO.useCustomQuestions?? && !stateChangeDTO.useCustomQuestions> checked="checked"</#if>/>
												            No
												        <input id="useCustomQuestions" type="radio" name="useCustomQuestions" value="true" <#if stateChangeDTO.useCustomQuestions?? && stateChangeDTO.useCustomQuestions> checked="checked"</#if>/>
												            Yes
													    <@spring.bind "stateChangeDTO.useCustomQuestions" /> 
														<#list spring.status.errorMessages as error>  
															<div class="alert alert-error"> <i class="icon-warning-sign"></i>
												            	${error}
												          	</div>
												        </#list>
												    </div>
												</div>
												<div id="fastTrackApplicationSection"
													<#if !stateChangeDTO.hasGlobalAdministrationRights() ||
														!stateChangeDTO.hasFastTrackOption()>
 														style="display:none"
 													</#if>>
													<div class="row" id="fastTrack" style="display:none">
													    <label id="fastTrackLabel" class="plain-label normal">Do you wish to fast-track this application?<em>*</em></label> 
													    <span class="hint" data-desc="<@spring.message 'validateApp.fastTrack'/>"> </span>
													    <div class="field">
													        <input id="fastTrackApplication" type="radio" name="fastTrackApplication" value="false" <#if stateChangeDTO.fastTrackApplication?? && !stateChangeDTO.fastTrackApplication> checked="checked"</#if>/>
													            No
													        <input id="fastTrackApplication" type="radio" name="fastTrackApplication" value="true" <#if stateChangeDTO.fastTrackApplication?? && stateChangeDTO.fastTrackApplication> checked="checked"</#if>/>
													            Yes
														    <@spring.bind "stateChangeDTO.fastTrackApplication" /> 
															<#list spring.status.errorMessages as error>  
																<div class="alert alert-error"> <i class="icon-warning-sign"></i>
													            	${error}
													          	</div>
													        </#list>
													    </div>
													</div>
												</div>
											</div>
										</div>									
										<div id="interivewDelegateDiv"
											<#if !stateChangeDTO.hasGlobalAdministrationRights()>
												style="display:none"
											</#if>>
											<div class="row-group" id="interviewDelegation" style="display:none">
												<div class="row">
													<label id="delegateLabel" class="plain-label normal">Delegate Administration<em>*</em></label> 
													<span class="hint" data-desc="<@spring.message 'validateApp.delegate'/>"> </span>
												    <div class="field">
												    	<input id="delegateProcessing" type="radio" name="delegate" value="false"
												        	<#if stateChangeDTO.delegate?? && !stateChangeDTO.delegate>checked="checked"</#if> />
												            No
												      	<input id="delegateProcessing" type="radio" name="delegate" value="true"
												          	<#if stateChangeDTO.delegate?? && stateChangeDTO.delegate>checked="checked"</#if> />
												            Yes
												        <@spring.bind "stateChangeDTO.delegate" />
													</div>
												</div>
												<div class="row">
													<label id="delegateFirstNameLabel" class="plain-label normal" for="delegateFirstName">Delegate First Name<em>*</em></label>
													<span class="hint" data-desc="<@spring.message 'validateApp.delegateFirstName'/>"></span>
													<div class="field">
														<input class="full" type="text" name="delegateFirstName" id="delegateFirstName" autocomplete="off" value="${(stateChangeDTO.delegateFirstName)!}"/>
														<@spring.bind "stateChangeDTO.delegateFirstName" /> 
														<#list spring.status.errorMessages as error>  
															<div class="alert alert-error"> <i class="icon-warning-sign"></i>
																${error}
															</div>
														</#list>
													</div>
												</div>
												<div class="row">
													<label id="delegateLastNameLabel" class="plain-label normal" for="delegateLastName">Delegate Last Name<em>*</em></label>
													<span class="hint" data-desc="<@spring.message 'validateApp.delegateLastName'/>"></span>
													<div class="field">
														<input class="full" type="text" name="delegateLastName" id="delegateLastName" autocomplete="off" value="${(stateChangeDTO.delegateLastName)!}"/>
														<@spring.bind "stateChangeDTO.delegateLastName" /> 
														<#list spring.status.errorMessages as error>  
															<div class="alert alert-error"> <i class="icon-warning-sign"></i>
																${error}
															</div>
														</#list>
													</div>
												</div>
												<div class="row">
													<label id="delegateEmailLabel" class="plain-label normal" for="delegateEmail">Delegate Email Address<em>*</em></label>
													<span class="hint" data-desc="<@spring.message 'validateApp.delegateEmail'/>"></span>
													<div class="field">
														<input class="full" type="email"  name="delegateEmail" id="delegateEmail" autocomplete="off" value="${(stateChangeDTO.delegateEmail)!}"/>
														<@spring.bind "stateChangeDTO.delegateEmail" /> 
														<#list spring.status.errorMessages as error>  
															<div class="alert alert-error"> <i class="icon-warning-sign"></i>
																${error}
															</div>
														</#list>
													</div>
												</div>
												<script type="text/javascript" src="<@spring.url '/design/default/js/autosuggest.js'/>"></script>
												<script type="text/javascript">
													$(document).ready(function() {
														autosuggest($("#delegateFirstName"), $("#delegateLastName"), $("#delegateEmail"));
													});
												</script>
											</div>
										</div>
										<@spring.bind "stateChangeDTO.confirmNextStage" />
										<div class=
											<#if spring.status.errorMessages?size &gt; 0>
												"alert alert-error">
											<#else>
												"alert">
											</#if>
											<div class="row">
												<label id="confirmNextStageLabel" class="terms-label" for="confirmNextStage">
													Confirm that you wish to move this application to the next stage.				
												</label>
												<div class="terms-field" >
													<input type="checkbox" name="confirmNextStage" id="confirmNextStage"/>
												</div>
											</div>
										</div>
										<#if stateChangeDTO.action?has_content>
											<input type="hidden" id="action" name="action" value="${(stateChangeDTO.action)!}"/>
										</#if>	
										<div class="buttons">
											<button class="btn btn-primary" type="button" id="changeStateButton" value="save">Submit</button>
										</div>
									</form>
									<input type="hidden" id="applicationId" value="${(stateChangeDTO.applicationForm.applicationNumber)!}"/>
									<#list stateChangeDTO.customQuestionCoverage as scoringStage>
										<input type="hidden" name="customQuestionCoverage" value="${(scoringStage)!}"/>
									</#list>
								</div>
							</section>
							<#include "/private/staff/admin/comment/timeline_application.ftl"/>
						</div>
					</div>
				</article>
			</div>
			<!-- Middle Ends -->
			<#include "/private/common/global_footer.ftl"/>
		</div>
		<!-- Wrapper Ends -->
	</body>
</html>