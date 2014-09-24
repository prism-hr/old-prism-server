  <!DOCTYPE HTML>
  <#import "/spring.ftl" as spring />
  <#setting locale = "en_US">
  <html>
  <head>
  <meta name="prism.version" content="<@spring.message 'prism.version'/>" >
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <title>UCL Postgraduate Admissions</title>
  
  <!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
  <meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />
  
  <!-- Styles for Application List Page -->
  <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/terms_and_condition.css' />"/>
  <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
  <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
  <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/state_transition.css' />"/>
  <link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
  <!-- Styles for Application List Page -->
  
  <!--[if lt IE 9]>
<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->
  
  <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
  <script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
  <script type="text/javascript" src="<@spring.url '/design/default/js/interviewer/comment/interviewComment.js' />"></script>
  <script type="text/javascript" src="<@spring.url '/design/default/js/scores.js' />"></script>
  <script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
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
  <div id="wrapper"> <#include "/private/common/global_header.ftl"/> 
    
    <!-- Middle Starts -->
    <div id="middle"> <#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
      <@header/>
      <!-- Main content area. -->
      <section id="reviewcommentsectopm" >
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
          <div class="content-box-inner"> <#include "/private/common/parts/application_info.ftl"/>
            <section class="form-rows">
              <h2>Provide Interview Feedback</h2>
              <div>
                <form id="interviewForm" method="POST" action="<@spring.url '/interviewFeedback'/>"/>
                <@spring.bind "comment.confirmNextStage" />
			    <#if spring.status.errorMessages?size &gt; 0>
		     		<div class="alert alert-error" >
                    <i class="icon-warning-sign"></i>
			    <#else>
		            <div class="alert alert-info">
          			<i class="icon-info-sign"></i>
	          	</#if>
                
                Following their interview, reassess the applicant's suitability for postgraduate research and their chosen study programme. </div>
                <input type="hidden" name="applicationId" id="applicationId" value =  "${(applicationForm.applicationNumber)!}"/>
                <div class="row-group">
                
                  <div class="row">
                    <label for="interview-comment" id="comment-lbl" class="plain-label">Comment<em>*</em></label>
                    <span class="hint" data-desc="<@spring.message 'interviewOutcome.comment'/>"></span>
                    <div class="field">
                      <textarea name="comment" id="interview-comment" class="max" rows="6" cols="80">${(comment.comment?html)!}</textarea>
                      <@spring.bind "comment.comment" />
                      <#list spring.status.errorMessages as error>
                      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                        ${error}
                      </div>
                      </#list> </div>
                  </div>
                  
                  <div class="row multi-line"> 
                    <label id="supervise-work-lbl" class="plain-label"><@spring.message 'feedbackComment.applicantRating'/><em>*</em></label> 
                    <span class="hint" data-desc="<@spring.message 'interviewOutcome.applicantRating'/>"></span>
                    <div class="field" id="field-applicantRating">
                      <ul class="rating-list clearfix">
                        <li><i class="icon-thumbs-down"></i></li>
                        <li><i class="icon-star-empty"></i></li>
                        <li><i class="icon-star-empty"></i></li>
                        <li><i class="icon-star-empty"></i></li>
                        <li><i class="icon-star-empty"></i></li>
                        <li><i class="icon-star-empty"></i></li>
                      </ul>
                      <input id="applicantRating" name="applicantRating" class="rating-input" type="number" value="${comment.applicantRating!}" min="0" max="5" />
                      
                      <@spring.bind "comment.applicantRating" />
                      <#list spring.status.errorMessages as error>
                      <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
                      </#list>
                    </div>
                  </div>
                  
                </div>
                <div class="row-group"> <#include "/private/staff/admin/comment/documents_snippet.ftl"/> </div>
                <div class="row-group">
                  <div class="row">
                    <label id="suitable-lbl" class="plain-label">Is the applicant suitable for postgraduate study at UCL?<em>*</em></label>
                    <span class="hint" data-desc="<@spring.message 'interviewOutcome.suitsPG'/>"></span>
                    <div class="field" id="field-issuitableucl">
                      <label><input type="radio"  name="suitableCandidateForUcl" value="true" id="suitableRB_true"
											<#if comment.suitableCandidateSet && comment.suitableCandidateForUcl> checked="checked"</#if>
                        /> Yes</label>
                      <label><input type="radio"  name="suitableCandidateForUcl" value="false" id="suitableRB_false"
											<#if comment.suitableCandidateSet && !comment.suitableCandidateForUcl> checked="checked"</#if>
                        /> No</label>
                      <@spring.bind "comment.suitableCandidateForUcl" />
                      <#list spring.status.errorMessages as error>
                      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                        ${error}
                      </div>
                      </#list> </div>
                  </div>
                  <div class="row multi-line">
                    <label id="suitable-lbl" class="plain-label">Is the applicant suitable for their chosen postgraduate study programme?<em>*</em></label>
                    <span class="hint" data-desc="<@spring.message 'interviewOutcome.suitsPGP'/>"></span>
                    <div class="field" id="field-issuitableprog">
                      <label><input type="radio"  name="suitableCandidateForProgramme" value="true" id="suitableRB_true"
											<#if comment.suitableCandidateForProgramme?? && comment.suitableCandidateForProgramme> checked="checked"</#if>
                        /> Yes</label>
                      <label><input type="radio"  name="suitableCandidateForProgramme" value="false" id="suitableRB_false"
											<#if comment.suitableCandidateForProgramme?? && !comment.suitableCandidateForProgramme> checked="checked"</#if>
                        /> No</label>
                      <@spring.bind "comment.suitableCandidateForProgramme" />
                      <#list spring.status.errorMessages as error>
                      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                        ${error}
                      </div>
                      </#list> </div>
                  </div>
                  <div class="row multi-line">
                    <label id="supervise-lbl" class="plain-label">Would you like to supervise/direct the applicant?<em>*</em></label>
                    <span class="hint" data-desc="<@spring.message 'interviewOutcome.admit'/>"></span>
                    <div class="field" id="field-wouldsupervise">
                      <label><input type="radio" name="willingToSupervise" value="true" id="willingRB_true"
											<#if comment.willingToSuperviseSet && comment.willingToSupervise> checked="checked"</#if> 
                        /> Yes</label>
                      <label><input type="radio" name="willingToSupervise" value="false" id="willingRB_false"
											<#if comment.willingToSuperviseSet && !comment.willingToSupervise> checked="checked"</#if>
                        /> No</label>
                      <@spring.bind "comment.willingToSupervise" />
                      <#list spring.status.errorMessages as error>
                      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                        ${error}
                      </div>
                      </#list> </div>
                  </div>
                  
                </div>
              	<#if applicationForm.latestInterview.useCustomQuestions>  
	              <#assign scores = comment.scores>
	              <#if (scores)?has_content>
	              <div id="scoring-questions" class="row-group">
	                  <#if comment.alert??>
	                  	<#assign alertForScoringQuestions=comment.alert>
	                  </#if>
	                  <#assign errorsContainerName = "comment">
	                  	<h3>Programme Specific Questions</h3>
	                  	<#include "/private/staff/scores.ftl"/>
	        	  </div>
	              </#if>
	           	</#if>
                  
                <@spring.bind "comment.confirmNextStage" />
			    <#if spring.status.errorMessages?size &gt; 0>
		     		<div class="alert alert-error" >
			    <#else>
			        <div class="alert" >
			    </#if>
					<div class="row">
						<label id="confirmNextStageLabel" class="terms-label" for="confirmNextStage">
							Please confirm that you are satisfied with your comments.				
						</label>
						<div class="terms-field">
							<input type="checkbox" name="confirmNextStage" id="confirmNextStage"/>
						</div>
						<input type="hidden" name="confirmNextStageValue" id="confirmNextStageValue"/>
					</div>
				</div>

				
                <div class="buttons">
                  <button class="btn btn-primary" id="submitInterviewFeedback" type="button" value="Submit">Submit</button>
                </div>
                </form>
              </div>
            </section>
            <#include "/private/staff/admin/comment/timeline_application.ftl"/> </div>
          <!-- .content-box-inner --> 
        </div>
        <!-- .content-box --> 
        
      </article>
      </section>
    </div>
    <!-- Middle Ends --> 
    
    <#include "/private/common/global_footer.ftl"/> </div>
  <!-- Wrapper Ends -->
  
  </body>
  </html>