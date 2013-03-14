
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
    
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
    <link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
    
    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
        <!-- Scripts -->
    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/admin/comment/upload.js'/>"></script>
    
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
	<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
	<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
  </head>
  
  <body>
  
    <div id="wrapper">
    
      <#include "/private/common/global_header.ftl"/>
      
        <!-- Middle. -->
        <div id="middle">
					<#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
					       <@header/>
          <!-- Main content area. -->
          <article id="content" role="main">
          
			  <!-- "Tools" -->
			  <div id="tools">
				<ul class="left">
				  <li class="icon-print"><a target="_blank" title="Click to Download" href="<@spring.url '/print?applicationFormId=${applicationForm.applicationNumber}'/>">Download PDF</a></li>
				  <li class="icon-feedback"><a title="Send Feedback" href="mailto:prism@ucl.ac.uk?subject=Feedback" target="_blank">Send Feedback</a></li>
				</ul>
			  </div>
      
						<div class="content-box">
							<div class="content-box-inner">
								<#include "/private/common/parts/application_info.ftl"/>
                
								<!---------- Reference -------------->
								<section class="form-rows">
									<h2>Provide Reference</h2>
									<div>
										<form id="documentUploadForm" method="POST" action="<@spring.url '/referee/submitReference'/>">
										
										<input type="hidden" name="applicationId" id="applicationId" value =  "${(applicationForm.applicationNumber)!}"/>
											<div class="section-info-bar">
												Provide an assessment of the applicant's suitability for postgraduate study and for their chosen study programme.
											</div>

											<div class="row-group">
											<div class="row"> 
												<span id="comment-lbl" class="plain-label">Comment<em>*</em></span>
												<span class="hint" data-desc="<@spring.message 'interviewOutcome.comment'/>"></span>
												<div class="field">		            				
													<textarea maxlength="2000" name="comment" id="comment" class="max" rows="6" cols="80" >${(comment.comment?html)!}</textarea>
													<@spring.bind "comment.comment" /> 
													<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
												</div>
											</div>
											</div>
											
											<div class="row-group">
												<#include "/private/staff/admin/comment/documents_snippet.ftl"/>
											</div>
											<div class="row-group">
												<h3>Applicant Suitability</h3>
											
												<div class="row">
													<span id="suitable-lbl" class="plain-label">Is the applicant suitable for postgraduate study at UCL?<em>*</em></span>
													<span class="hint" data-desc="<@spring.message 'interviewOutcome.suitsPG'/>"></span>
													<div class="field" id="field-issuitableucl">
														<label><input type="radio" name="suitableForUCL" value="true" id="suitableRB_true"
														<#if comment.isSuitableForUCLSet() && comment.suitableForUCL> checked="checked"</#if>
														/> Yes</label> 
														<label><input type="radio" name="suitableForUCL" value="false" id="suitableRB_false"
														<#if comment.isSuitableForUCLSet() && !comment.suitableForUCL> checked="checked"</#if>
														/> No</label>
														<@spring.bind "comment.suitableForUCL" /> 
															<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
														</div>
												</div>
											<div class="row multi-line" id="field-issuitableprog">
												<span id="supervise-lbl" class="plain-label">Is the applicant suitable for their chosen postgraduate study programme?<em>*</em></span>
												<span class="hint" data-desc="<@spring.message 'interviewOutcome.suitsPGP'/>"></span>
												<div class="field">
													<label><input type="radio" name="suitableForProgramme" value="true" id="willingRB_true"
													<#if comment.isSuitableForProgrammeSet() && comment.suitableForProgramme> checked="checked"</#if> 
													/> Yes</label> 
													<label><input type="radio" name="suitableForProgramme" value="false" id="willingRB_false"
													<#if comment.isSuitableForProgrammeSet() && !comment.suitableForProgramme> checked="checked"</#if>
													/> No</label> 
													<@spring.bind "comment.suitableForProgramme" /> 
													<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
												</div>
											</div>
									   </div>
												
												<div class="buttons">
<#--
													<button class="clear" type="button" value="cancel">Clear</button>
-->
													<button class="blue" type="submit" id="referenceSaveButton" value="close">Submit</button>              
												</div>                      
										</form>
                    <!---------- End Reference -------------->
                  
				</div>
				</section>
					<input type="hidden" name="applicationId" id="applicationId" value =  "${(applicationForm.applicationNumber)!}"/>
                    <#include "/private/staff/admin/comment/timeline_application.ftl"/>
							</div><!-- .content-box-inner -->
						</div><!-- .content-box -->
          
          </article>
        
        </div>
      
<#include "/private/common/global_footer.ftl"/>
    
    </div>
    

  </body>
</html>
