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
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
		<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
		<!-- Styles for Application List Page -->

		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	
	    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
        <script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
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
				      
				      <!-- content box -->				      
				      <div class="content-box">
				        <div class="content-box-inner">
				         <#include "/private/common/parts/application_info.ftl"/>
				        
									<section class="form-rows">
										<#if isConfirmEligibilityComment??>
											<h2 class="no-arrow">Confirm Applicant Eligibility</h2>
										<#else>
											<h2 class="no-arrow">Comment</h2>
										</#if>
										<div>
											<form method="POST" <#if isConfirmEligibilityComment??>action= "<@spring.url '/admitter/confirmEligibility'/> <#else>action= "<@spring.url '/comment'/> </#if>">
												<div class="row-group">
													<input type="hidden" name="applicationId" id="applicationId" value =  "${(applicationForm.applicationNumber)!}"/>
													<div class="row">
														<label class="plain-label" for="comment">Comment<em>*</em></label>
														<span class="hint" data-desc="<@spring.message 'interviewOutcome.comment'/>"></span>
														<div class="field">
														    <textarea id="genericComment" name="comment" class="max" rows="6" cols="80">${(comment.comment?html)!}</textarea>
															<@spring.bind "comment.comment" /> 
															<#list spring.status.errorMessages as error> 
                                                            <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error}</div>
                                                            </#list>
														</div>
													</div>
												</div>
												
												<div class="row-group">													
													<#include "documents_snippet.ftl"/>
												</div>
												
												<div class="row-group">
												
												<#if isConfirmEligibilityComment??>
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
												</#if>
											     
												<div class="buttons">						        		
													<button class="btn btn-primary" type="submit" value="Submit">Submit</button>						        
												</div>
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