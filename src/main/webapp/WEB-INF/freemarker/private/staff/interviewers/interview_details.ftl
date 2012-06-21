<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<#assign avaliableOptionsSize = (programmeInterviewers?size + previousInterviewers?size + 4)/>
<#if (avaliableOptionsSize > 25)>
<#assign avaliableOptionsSize = 25 />
</#if> 
<#assign selectedOptionsSize = (applicationInterviewers?size + pendingInterviewers?size) + 1/>
<#if (selectedOptionsSize > 25)>
<#assign selectedOptionsSize = 25 />
</#if> 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>UCL Postgraduate Admissions</title>

<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />

<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/private/global_private.css'/>" />
<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/private/application.css'/>" />
<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/private/staff/add_interviewer.css'/>" />
<link type="text/css" rel="stylesheet"href="<@spring.url '/design/default/css/actions.css' />" />

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
	       <@header/>
		<!-- Main content area. -->
		<article id="content" role="main">
		
			<!-- FLOATING TOOLBAR -->
			<ul id="view-toolbar" class="toolbar">
				<li class="top"><a href="javascript:backToTop();" title="Back to top">Back to top</a></li>
				<li class="print"><a target="_blank" href="<@spring.url '/print?applicationFormId=${applicationForm.applicationNumber}'/>" title="Click to Download">Print</a></li>
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
							${applicationForm.applicationNumber} 
						</div>
		
						<#if applicationForm.isSubmitted()>
						<div class="row">
							<label>Submitted</label>
							${(applicationForm.submittedDate?string("dd MMM yyyy"))!}
						</div>
						</#if>
					</div>
		
					<hr />
					
					<section class="form-rows">
						<h2>Assign Interviewers</h2>
						<div>
							<form>
							
								<div class="section-info-bar">
									Assign interviewers to the application here. You may also create new interviewers.
								</div>
					
								<div class="row-group">			
								
										<div class="row" id="assignInterviewersToAppSection">
											<span class="plain-label">Assign Interviewers</span>
											<span class="hint" data-desc="<@spring.message 'assignInterviewer.assign'/>"></span>
											<div class="field">
												<#-- <select id="programInterviewers" multiple="multiple" size="${avaliableOptionsSize}"> -->
												<select id="programInterviewers" multiple="multiple" size="8">
													<optgroup id="default" label="Default interviewers">
														<#list programmeInterviewers as interviewer>
														<option value="${encrypter.encrypt(interviewer.id)}" category="default">${interviewer.firstName?html} ${interviewer.lastName?html} <#if !interviewer.enabled> - Pending</#if></option>
														</#list>
													</optgroup>
													<optgroup id="previous" label="Previous interviewers">
														<#list previousInterviewers as interviewer>
														<option value="${encrypter.encrypt(interviewer.id)}" category="previous">${interviewer.firstName?html} ${interviewer.lastName?html}</option>
														</#list>
														<#list applicationInterviewers as interviewer>
														<option value="${encrypter.encrypt(interviewer.id)}" class="selected" disabled="disabled">
															${interviewer.firstName?html} ${interviewer.lastName?html}
														</option>
														</#list>
														<#list pendingInterviewers as unsaved>									
														<option value="${encrypter.encrypt(unsaved.id)}" class="selected" disabled="disabled">
															${unsaved.firstName?html} ${unsaved.lastName?html}
														</option>
														</#list>
														<#list willingToInterviewReviewers as willingReviewer>									
														<option value="${encrypter.encrypt(willingReviewer.id)}" class="selected" disabled="disabled">
															${willingReviewer.firstName?html} ${willingReviewer.lastName?html}
														</option>
														</#list>
													</optgroup>
												</select>
											</div>
										</div>
		
										<!-- Available Reviewer Buttons -->
										<div class="row interviewer-buttons">
											<div class="field">
												<span>
													<button class="blue" type="button" id="addInterviewerBtn"><span class="icon-down"></span> Add</button>
													<button type="button" id="removeInterviewerBtn"><span class="icon-up"></span> Remove</button>
												</span>
											</div>
										</div>
		
										<!-- Already interviewers of this application -->
										<div class="row">
											<div class="field">
												<select id="applicationInterviewers" multiple="multiple" <#if assignOnly?? && assignOnly> disabled="disabled"</#if> size="${selectedOptionsSize}">
													<#list applicationInterviewers as interviewer>
													<option value="${encrypter.encrypt(interviewer.id)}">
														${interviewer.firstName?html} ${interviewer.lastName?html}
													</option>
													</#list>
													<#list pendingInterviewers as unsaved>									
													<option value="${encrypter.encrypt(unsaved.id)}">
														${unsaved.firstName?html} ${unsaved.lastName?html}
													</option>
													</#list>
													<#list willingToInterviewReviewers as willingReviewer>									
													<option value="${encrypter.encrypt(willingReviewer.id)}">
														${willingReviewer.firstName?html} ${willingReviewer.lastName?html}
													</option>
													</#list>
												</select>
												<@spring.bind "interview.interviewers" /> 
												<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
											</div>
										</div>
									</div>
		
									<div class="row-group">				
										<#if mesage??>			
										<p>${message?html}</p>
										</#if>
										<#if RequestParameters.message??>			
										<p>${RequestParameters.message?html}</p>
										</#if>
										<p><strong>Create New Interviewer</strong></p>									

										<div class="row">
											<label class="label normal">Interviewer First Name<em>*</em></label> 
											<span class="hint" data-desc="<@spring.message 'assignInterviewer.firstName'/>"></span>
											<div class="field">
												<input class="full" type="text" name="newInterviewerFirstName" id="newInterviewerFirstName"  value="${(interviewer.firstName?html)!}"/>
												<@spring.bind "interviewer.firstName" /> 
												<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>	
											</div>
										</div>
										
										<div class="row">
											<label class="label normal">Interviewer Last Name<em>*</em></label>
											<span class="hint" data-desc="<@spring.message 'assignInterviewer.lastName'/>"></span>
											<div class="field">
												<input class="full" type="text" name="newInterviewerLastName" id="newInterviewerLastName" value="${(interviewer.lastName?html)!}"/>			                                      
												<@spring.bind "interviewer.lastName" /> 
												<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
											</div>
										</div>
			
										<div class="row">
											<label class="label normal">Interviewer Email Address<em>*</em></label>
											<span class="hint" data-desc="<@spring.message 'assignInterviewer.email'/>"></span>
											<div class="field">
												<input class="full" type="text"  name="newInterviewerEmail" id="newInterviewerEmail" value="${(interviewer.email?html)!}"/>			                                         
												<@spring.bind "interviewer.email" /> 
												<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
											</div>
										</div>
		
										<div class="row">
											<div class="field">
												<button class="blue" type="button" id="createInterviewer">Create interviewer</button>
											</div>
										</div>
									</div>
		
									<div class="row-group">
										<p><strong>Interview Arrangements</strong></p>
										<div class="row">
											<label class="label normal">Interview Date<em>*</em></label>
											<span class="hint" data-desc="<@spring.message 'assignInterviewer.interviewDate'/>"></span>
											<div class="field">
												<#if assignOnly?? && assignOnly>
												<input class="half date" disabled="disabled" type="text" name="interviewDate" id="interviewDate" value="${(interview.interviewDueDate?string('dd MMM yyyy'))!}" />
												<#else>
												<input class="half date" type="text" name="interviewDate" id="interviewDate" value="${(interview.interviewDueDate?string('dd MMM yyyy'))!}" />
												</#if>
												<@spring.bind "interview.interviewDueDate" /> 
												<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
											</div>
										</div>
									
										<div class="row">
											<label class="label normal">Interview Time (GMT/BST)<em>*</em></label>
											<span class="hint" data-desc="<@spring.message 'assignInterviewer.interviewTime'/>"></span>
											<div class="field">
												<#if assignOnly?? && assignOnly>
												<input disabled="disabled" type="text" value="${(interview.interviewTime)!}" />
												<#else>
												<#include "/private/staff/interviewers/time_dropdown.ftl"/>
												<span class="invalid" name="timeInvalid" style="display:none;"></span>
												<@spring.bind "interview.interviewTime" /> 
												<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
												</#if>
											</div>
										</div>
									
										<div class="row">
											<label class="label normal">Interview Instructions<em>*</em></label>
											<span class="hint" data-desc="<@spring.message 'assignInterviewer.instructions'/>"></span>
											<div class="field">
												<#if assignOnly?? && assignOnly>
												<textarea id="furtherDetails" readonly="readonly" disabled="disabled" name="furtherDetails" class="max" rows="6" cols="80" maxlength='5000'>${interview.furtherDetails!}</textarea>
												<#else>
												<textarea id="furtherDetails" name="furtherDetails" class="max" rows="6" cols="80" maxlength='5000'>${interview.furtherDetails!}</textarea>
												</#if>
												<@spring.bind "interview.furtherDetails" /> 
												<#list spring.status.errorMessages as error><br /><span class="invalid">${error}</span></#list>
											</div>
										</div>
		
										<div class="row">
											<label class="label normal">Interviewer Location<em>*</em></label>
											<span class="hint" data-desc="<@spring.message 'assignInterviewer.location'/>"></span>
											<div class="field">
												<#if assignOnly?? && assignOnly>
												<textarea id="interviewLocation" readonly="readonly" disabled="disabled" name="interviewLocation" class="max" rows="1" cols="80" maxlength='5000'>${(interview.locationURL?html)!}</textarea>
												<#else>
												<textarea id="interviewLocation" name="interviewLocation" class="max" rows="1" cols="80" maxlength="5000" placeholder="e.g. http://www.ucl.ac.uk/locations/ucl-maps/">${(interview.locationURL?html)!}</textarea>
												</#if>				                                            
												<@spring.bind "interview.locationURL" /> 
												<#list spring.status.errorMessages as error><br /><span class="invalid">${error}</span></#list>
											</div>
										</div>
									
								</div>
								
								<div class="buttons">
									<button class="blue" type="button" id="moveToInterviewBtn">Continue</button>
								</div>								
								
								<input type="hidden" id="applicationId" name="applicationId" value="${applicationForm.applicationNumber}"/>
								<input type="hidden" id="interviewId" name="interviewId" value="<#if interview?? && interview.id?? >${encrypter.encrypt(interview.id)}</#if>"/> 
							</form>
						</div>
					</section>
		
					<form id="postInterviewForm" method="post" <#if assignOnly?? && assignOnly> action ="<@spring.url '/interview/assign'/>"<#else> action ="<@spring.url '/interview/move'/>" </#if>></form>
					<form id="postInterviewerForm" method="post" <#if assignOnly?? && assignOnly> action ="<@spring.url '/interview/assignNewInterviewer'/>" <#else> action ="<@spring.url '/interview/createInterviewer'/>" </#if>></form>

				</div><!-- .content-box-inner -->
		
			</div><!-- .content-box -->
		
		</article>
	
	</div><!-- #middle -->
	
	<#include "/private/common/global_footer.ftl"/>

</div><!-- #wrapper -->

<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/interviewer/interview.js'/>"></script>
</body>
</html>
</section>
