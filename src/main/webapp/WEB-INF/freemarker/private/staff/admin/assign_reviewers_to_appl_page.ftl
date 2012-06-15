<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<#assign avaliableOptionsSize = (programmeReviewers?size + previousReviewers?size + 4)/>
<#if (avaliableOptionsSize > 25)>
	<#assign avaliableOptionsSize = 25 />
</#if> 
<#assign selectedOptionsSize = (applicationReviewers?size + pendingReviewers?size) + 1/>
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
				          			
							<div id="add-info-bar-div" class="section-info-bar">
								You can optionally assign new reviewers here.
							</div>	
							
							<section class="form-rows violet">
								<div>
									<form>
									
										<div id="assignReviewersToAppSection" class="row-group">			
											<div class="row">
												<label class="label">Reviewers</label>
												<div class="field">
													<p><strong>Available Reviewers</strong></p>
													<select id="programReviewers" multiple="multiple" size="${avaliableOptionsSize}">
														<option value="" disabled="disabled" id="default">Default reviewers</option>
														<#list programmeReviewers as reviewer>
														<option value="${applicationForm.applicationNumber}|${encrypter.encrypt(reviewer.id)}" category="default">${reviewer.firstName?html} ${reviewer.lastName?html} <#if !reviewer.enabled> - Pending</#if></option>
														</#list>
														<option value="" disabled="disabled"></option>
														<option value="" disabled="disabled" id="previous">Previous reviewers in this programme</option>
														
														<#list previousReviewers as reviewer>
														<option value="${applicationForm.applicationNumber}|${encrypter.encrypt(reviewer.id)}" category="previous">${reviewer.firstName?html} ${reviewer.lastName?html} <#if !reviewer.enabled> - Pending</#if></option>
														</#list>							
														<option value="" disabled="disabled"></option>								
													</select>
												</div>
												
											</div>
						
					
											<!-- Available Reviewer Buttons -->
											<div class="row reviewer-buttons">
												<div class="field">
													<span>
														<button class="blue" type="button" id="addReviewerBtn">Add <span class="icon-down"></span></button>
														<button type="button" id="removeReviewerBtn"><span class="icon-up"></span> Remove</button>
													</span>
												</div>
											</div>
						
										<!-- Already reviewers of this application -->
											<div class="row">
												<div class="field">
													<p>
														<strong>Selected Reviewers</strong>
													</p>
													<select id="applicationReviewers" multiple="multiple" <#if assignOnly?? && assignOnly> disabled="disabled"</#if> size="${selectedOptionsSize}">
														<#list applicationReviewers as reviewer>
															<option value="${applicationForm.applicationNumber}|${encrypter.encrypt(reviewer.id)}">
																${reviewer.firstName?html} ${reviewer.lastName?html} <#if !reviewer.enabled> - Pending</#if>
															</option>
														</#list>
														<#list pendingReviewers as unsaved>									
															<option value="${applicationForm.applicationNumber}|${encrypter.encrypt(unsaved.id)}">
																${unsaved.firstName?html} ${unsaved.lastName?html} <#if !unsaved.enabled> - Pending</#if> (*)
															</option>
														</#list>
													</select>
											<@spring.bind "reviewRound.reviewers" /> 
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
											<p>
												<strong>Create New Reviewer</strong>
											</p>									
											
											<div class="row">
				                               	<label class="label normal">Reviewer First Name<em>*</em></label> 
			                                   	<div class="field">
				                                   <input class="full" type="text" name="newReviewerFirstName" id="newReviewerFirstName" value="${(reviewer.firstName?html)!}"/>
				                               	<@spring.bind "reviewer.firstName" /> 
					                			 <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>	
				                               	</div>
					                        </div>
			                                <div class="row">
			                                    <label class="label normal">Reviewer Last Name<em>*</em></label>
			                                    <div class="field">
			                                        <input class="full" type="text" name="newReviewerLastName" id="newReviewerLastName" value="${(reviewer.lastName?html)!}"/>			                                      
			                                      <@spring.bind "reviewer.lastName" /> 
				                			   		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
			                                    </div>
			                                </div>
					                                
				                             <div class="row">
			                                	<label class="label normal">Email<em>*</em></label>
		                                        <div class="field">
		                                         	<input class="full" type="text"  name="newReviewerEmail" id="newReviewerEmail" value="${(reviewer.email?html)!}"/>			                                         
		                                         <@spring.bind "reviewer.email" /> 
			                			   		 <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
		                                        </div>
				                             </div>
										
											<div class="row">
												<div class="field">
													<button class="blue" type="button" id="createReviewer">Create reviewer</button>
												</div>
											</div>
											
											<div class="buttons">
												<button class="blue" type="button" id="moveToReviewBtn">Continue</button>
											</div>
										</div>
															
										<input type="hidden" id="applicationId" name="applicationId" value="${applicationForm.applicationNumber}"/>
										  

								</form>
							</div>
						</section>
						
						<form id="postReviewForm" method="post" <#if assignOnly?? && assignOnly> action ="<@spring.url '/review/assign'/>"<#else> action ="<@spring.url '/review/move'/>" </#if>></form>
						<form id="postReviewerForm" method="post" <#if assignOnly?? && assignOnly> action ="<@spring.url '/review/assignNewReviewer'/>" <#else> action ="<@spring.url '/review/createReviewer'/>" </#if>></form>
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
	<script type="text/javascript"	src="<@spring.url '/design/default/js/reviewer/review.js'/>"></script>
</body>
</html>
</section>
