<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<#assign avaliableOptionsSize = (programmeSupervisors?size + programmeSupervisors?size + 4)/>
<#if (avaliableOptionsSize > 25)>
	<#assign avaliableOptionsSize = 25 />
</#if> 
<#assign selectedOptionsSize = (applicationSupervisors?size + pendingSupervisors?size) + 1/>
<#if (selectedOptionsSize > 25)>
	<#assign selectedOptionsSize = 25 />
</#if> 
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>UCL Postgraduate Admissions</title>
		
		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		
		<link type="text/css" rel="stylesheet" 
			href="<@spring.url '/design/default/css/private/global_private.css'/>" />
		<link type="text/css" rel="stylesheet" 
			href="<@spring.url '/design/default/css/private/application.css'/>" />
		
		<link type="text/css" rel="stylesheet" 
			href="<@spring.url '/design/default/css/private/staff/add_reviewer.css'/>" />
		
		<link type="text/css" rel="stylesheet"
				href="<@spring.url '/design/default/css/actions.css' />" />
		
		
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

				<!-- Main content area. -->
				<article id="content" role="main">

					<!-- FLOATING TOOLBAR -->
		            <ul id="view-toolbar">
		            	<li class="top"><a href="javascript:backToTop();" title="Back to top">Back to top</a></li>
		                <li class="print"><a href="<@spring.url '/print?applicationFormId=${applicationForm.id?string("######")}'/>" title="Print">Print</a></li>
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
						            ${applicationForm.id?string("######")} 
						        </div>
						        
						        <#if applicationForm.isSubmitted()>
						        	<div class="row">
						            	<label>Date Submitted</label>
						              	${(applicationForm.submittedDate?string("dd-MMM-yyyy hh:mm a"))!}
						            </div>
						        </#if>
							</div>
							
							<hr />
				          			
							<section class="folding violet">
								<div>
									<form>
										<div>			
											<section id="assignSupervisorsToAppSection" >
											
												<div class="row">
													<label class="label">Supervisors</label>
													<div class="field">
														<p>
															<strong>Available Supervisors</strong>
														</p>
														<select id="programSupervisors" multiple="multiple" size="${avaliableOptionsSize}">
															<option value="" disabled="disabled" id="default">Default supervisors</option>
															<#list programmeSupervisors as supervisor>
															  <option value="${applicationForm.id?string("######")}|${supervisor.id?string('#####')}" category="default">${supervisor.firstName?html} ${supervisor.lastName?html} <#if !supervisor.enabled> - Pending</#if></option>
															</#list>
															<option value="" disabled="disabled"></option>
															<option value="" disabled="disabled" id="previous">Previous supervisors in this programme</option>
															
															<#list previousSupervisors as supervisor>
															  <option value="${applicationForm.id?string("######")}|${supervisor.id?string('#####')}" category="previous">${supervisor.firstName?html} ${supervisor.lastName?html} <#if !supervisor.enabled> - Pending</#if></option>
															</#list>							
															<option value="" disabled="disabled"></option>								
														</select>
													</div>
												</div>
											</section>	          				
						
					
											<!-- Available Supervisor Buttons -->
											<div class="row supervisor-buttons">
												<div class="field">
													<span>
														<button class="blue" type="button" id="addSupervisorBtn">Add</button>
														<button class="blue" type="button" id="removeSupervisorBtn">Remove</button>
													</span>
												</div>
											</div>
						
										<!-- Already supervisors of this application -->
											<div class="row">
												<div class="field">
													<p>
														<strong>Selected Supervisors</strong>
													</p>
													<select id="applicationSupervisors" multiple="multiple" <#if assignOnly?? && assignOnly> disabled="disabled"</#if> size="${selectedOptionsSize}">
														<#list applicationSupervisors as supervisor>
															<option value="${applicationForm.id?string("######")}|${supervisor.id?string('#####')}">
																${supervisor.firstName?html} ${supervisor.lastName?html} <#if !supervisor.enabled> - Pending</#if>
															</option>
														</#list>
														<#list pendingSupervisors as unsaved>									
															<option value="${applicationForm.id?string("######")}|${unsaved.id?string('#####')}">
																${unsaved.firstName?html} ${unsaved.lastName?html} <#if !unsaved.enabled> - Pending</#if> (*)
															</option>
														</#list>
													</select>
												</div>
											</div>
											<@spring.bind "approvalRound.supervisors" /> 
				                			 <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
										</div>
										<div>				
											<#if mesage??>			
												<p>${message?html}</p>
											</#if>
											<#if RequestParameters.message??>			
												<p>${RequestParameters.message?html}</p>
											</#if>
											<p>
												<strong>Create New Supervisor</strong>
											</p>									
											
											<div class="row">
				                               	<label class="label normal">Supervisor First Name<em>*</em></label> 
			                                   	<div class="field">
				                                   <input class="full" type="text" name="newSupervisorFirstName" id="newSupervisorFirstName" value="${(supervisor.firstName?html)!}"/>
				                               	</div>
				                               	<@spring.bind "supervisor.firstName" /> 
					                			 <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>	
					                        </div>
			                                <div class="row">
			                                    <label class="label normal">Supervisor Last Name<em>*</em></label>
			                                    <div class="field">
			                                        <input class="full" type="text" name="newSupervisorLastName" id="newSupervisorLastName" value="${(supervisor.lastName?html)!}"/>			                                      
			                                    </div>
			                                      <@spring.bind "supervisor.lastName" /> 
				                			   		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
			                                </div>
					                                
				                             <div class="row">
			                                	<label class="label normal">Email<em>*</em></label>
		                                        <div class="field">
		                                         	<input class="full" type="text"  name="newSupervisorEmail" id="newSupervisorEmail" value="${(supervisor.email?html)!}"/>			                                         
		                                        </div>
		                                         <@spring.bind "supervisor.email" /> 
			                			   		 <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
				                             </div>
										
											<div class="row">
												<div class="field">
													<button class="blue" type="button" id="createSupervisor">Create supervisor</button>
													<button class="blue" type="button" id="moveToApprovalBtn">Continue</button>
										
												</div>
											</div>
										</div>
															
										<input type="hidden" id="applicationId" name="applicationId" value="${applicationForm.id?string("######")}"/>
										<input type="hidden" id="approvalRoundId" name="approvalRoundId" value="${(approvalRound.id?string("######"))!}"/>  
									</div>
								</form>
							</div>
						</section>
						<form id="postApprovalForm" method="post" action ="<@spring.url '/approval/move'/>" >
							
						</form>
						<form id="postSupervisorForm" method="post" <#if assignOnly?? && assignOnly> action ="<@spring.url '/approval/assignNewSupervisor'/>" <#else> action ="<@spring.url '/approval/createSupervisor'/>" </#if>>				
					
					<#include "/private/common/feedback.ftl"/>
				</div>
				<!-- .content-box-inner -->
		</div>
		<!-- .content-box -->

		</article>

	</div>

	<!-- Footer. -->
	<div id="footer">
		<ul>
			<li><a href="#">Privacy</a></li>
			<li><a href="#">Terms &amp; conditions</a></li>
			<li><a href="#">Contact us</a></li>
			<li><a href="#">Glossary</a></li>
		</ul>
	</div>

	</div>

	<script type="text/javascript"	src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	<script type="text/javascript"	src="<@spring.url '/design/default/js/libraries.js'/>"></script>
	<script type="text/javascript"	src="<@spring.url '/design/default/js/supervisor/approval.js'/>"></script>
</body>
</html>
</section>
