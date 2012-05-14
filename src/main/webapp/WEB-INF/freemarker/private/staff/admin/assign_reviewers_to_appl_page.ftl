<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
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

				    <#if !user.isInRole('APPLICANT')>
				    	<#include "/private/common/parts/tools.ftl"/>
				    </#if>

					<!-- FLOATING TOOLBAR -->
		            <ul id="view-toolbar">
		            	<li class="top"><a href="javascript:backToTop();" title="Back to top">Back to top</a></li>
		                <li class="print"><a href="<@spring.url '/print?applicationFormId=${applicationForm.id?string("######")}'/>" title="Print">Print</a></li>
					</ul>


					<!-- content box -->
					<div class="content-box">
						<div class="content-box-inner">
						
							<@spring.bind "applicationForm.*" />
							<@spring.bind "availableReviewers.*" />
							<@spring.bind "applicationReviewers.*" />
							<@spring.bind "programme.*" />
							<@spring.bind "unsavedReviewers.*" />
						
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
							
				          	<section id="assignReviewersToAppSection" class="folding violet">
            					<div>
              						<form>
						            	<div>
						               
                  							<!-- Available reviewers in programme -->
                  							<div class="row">
                    							<label class="label">Reviewers</label>
                    							<div class="field">
                    								<p><strong>Available Reviewers</strong></p>
													<select id="reviewers" multiple="multiple">
														<#list availableReviewers as reviewer>
								  							<option value="${reviewer.id?string('#####')}">
								  								${reviewer.firstName?html} ${reviewer.lastName?html} 
								  								<#if !reviewer.enabled> - Pending</#if>
								  							</option>
														</#list>
													</select>
						                		</div>
						                	</div>
						                	
						                	<!-- Available Reviewer Buttons -->
						                  	<div class="row reviewer-buttons">
						                    	<div class="field">
						                    		<span>
						                    			<button class="blue" type="submit" id="addReviewerBtn">Add</button>
						                    			<button class="blue" type="submit" id="removeReviewerBtn">Remove</button>
						                      		</span>
						                    	</div>
						                  	</div>						                	
						                	
						                	<!-- Already reviewers of this application -->
                  							<div class="row">
                    							<div class="field">
                    								<p><strong>Selected Reviewers</strong></p>
                      								<select id="assignedReviewers" multiple="multiple">
                      									<#list applicationReviewers as reviewer>
                      										<option disabled="disabled" value="${reviewer.id?string('#####')}">
                      											${reviewer.firstName?html} ${reviewer.lastName?html} 
                      											<#if !reviewer.enabled> - Pending</#if>
                      										</option>	
                      									</#list>	
														<#list unsavedReviewers as unsaved>
							   								<#if applicationReviewers?seq_index_of(unsaved) < 0>
																<option value="${unsaved.id?string('#####')}">
																	${unsaved.firstName?html} ${unsaved.lastName?html} 
																	<#if !unsaved.enabled> - Pending</#if>
																</option>
							   								</#if>
														</#list>
                      									
						                			</select>
						                		</div>
						                	</div>
						                	
						               	</div>
						               	
                						<div>
                
                							<p>${message!}</p>
                							<p><strong>Create New Reviewer</strong></p>
                							
                							<!-- Supervisor First Name -->
						                  	<div class="row">
						                    	<label class="label normal">Reviewer First Name<em>*</em></label>
						                    	<span class="hint" data-desc="Tooltip demonstration."></span>
						                    	<div class="field">
						                    		<input class="full" type="text" name="newReviewerFirstName" id="newReviewerFirstName"/>
						                    	</div>
						                    	<@spring.bind "uiReviewer.firstName" />
						                    	<#list spring.status.errorMessages as error> 
						                    		<span class="invalid">${error}</span>
						                    	</#list>
						                  	</div>

											<!-- Supervisor Last Name -->
						                  	<div class="row">
						                    	<label class="label normal">Reviewer Last Name<em>*</em></label>
						                    	<span class="hint" data-desc="Tooltip demonstration."></span>
						                    	<div class="field">
						                    		<input class="full" type="text" name="newReviewerLastName" id="newReviewerLastName"/>
						                    	</div>
						                    	<@spring.bind "uiReviewer.lastName" />
						                    	<#list spring.status.errorMessages as error> 
						                    		<span class="invalid">${error}</span>
						                    	</#list>
						                  	</div>  
						                  	
						                  	<!-- Supervisor Email -->
						                  	<div class="row">
						                    	<label class="label normal">Reviewer Email<em>*</em></label>
						                    	<span class="hint" data-desc="Tooltip demonstration."></span>
						                    	<div class="field">
						                    		<input class="full" type="text" name="newReviewerEmail" id="newReviewerEmail"/>
						                    	</div>
						                    	<@spring.bind "uiReviewer.email" />
						                    	<#list spring.status.errorMessages as error> 
						                    		<span class="invalid">${error}</span>
						                    	</#list>
						                  	</div>  
						                  	
											<div class="row">
                    							<div class="field">
		                  							<button class="blue" type="submit" id="createReviewer">Create reviewer</button>
		                  							<button class="blue" type="submit" id="moveToReviewBtn">Create reviewer</button>
                    							</div>
                  							</div>
                							
						               	</div>
						               	
						               	<input type="hidden" id="applicationId" name="applicationId" value="${applicationForm.id?string("######")}"/>
						            
						            </form>
						        </div>
							</section>
							
							<!-- #actions -->
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

		<script type="text/javascript"
			src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript"
			src="<@spring.url '/design/default/js/libraries.js'/>"></script>
		<script type="text/javascript"
			src="<@spring.url '/design/default/js/script.js' />"></script>
		<script type="text/javascript"
			src="<@spring.url '/design/default/js/admin/assignReviewerToApplication.js'/>"></script>
	</body>
</html>