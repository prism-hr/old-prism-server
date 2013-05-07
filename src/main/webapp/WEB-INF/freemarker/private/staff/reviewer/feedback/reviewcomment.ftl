
  <!DOCTYPE HTML>
  <#import "/spring.ftl" as spring />
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
  <script type="text/javascript" src="<@spring.url '/design/default/js/reviewer/comment/reviewComment.js' />"></script>
  <script type="text/javascript" src="<@spring.url '/design/default/js/scores.js' />"></script>
  <script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
  <script type="text/javascript" src="<@spring.url '/design/default/js/admin/comment/upload.js'/>"></script>
  <script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
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
      <div id="reviewcommentsectopm" >
      <!-- Main content area. -->
      <article id="content" role="main"> 
        
        <!-- content box -->
        <div class="content-box">
          <div class="content-box-inner"> <#include "/private/common/parts/application_info.ftl"/>
            <section class="form-rows">
              <h2 class="no-arrow"> Review Application </h2>
              <div>
                <form id ="reviewForm" method="POST" action= "<@spring.url '/reviewFeedback'/>"/>
                
                <input type="hidden" name="applicationId" id="applicationId" value =  "${(applicationForm.applicationNumber)!}"/>
                <div class="alert alert-info">
          			<i class="icon-info-sign"></i>  Provide an assessment of the applicant's suitability for postgraduate study and for their chosen study programme. </div>
                <div class="row-group">
                  <div class="row">
                    <label for="review-comment" id="comment-lbl" class="plain-label">Comment<em>*</em></label>
                    <span class="hint" data-desc="<@spring.message 'interviewOutcome.comment'/>"></span>
                    <div class="field">
                      <textarea name="comment" id="review-comment" class="max" rows="6" cols="80" maxlength='5000'>${(comment.comment?html)!}</textarea>
                      <@spring.bind "comment.comment" />
                      <#list spring.status.errorMessages as error>
                      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                        ${error}
                      </div>
                      </#list> </div>
                  </div>
                </div>
                <div class="row-group"> <#include "/private/staff/admin/comment/documents_snippet.ftl"/> </div>
                <div class="row-group">
                  <h3>Applicant Suitability</h3>
                  <div class="row">
                    <label id="suitable-lbl" class="plain-label">Is the applicant suitable for postgraduate study at UCL?<em>*</em></label>
                    <span class="hint" data-desc="<@spring.message 'interviewOutcome.suitsPG'/>"></span>
                    <div class="field" id="field-issuitableucl">
                      <label><input type="radio" name="suitableCandidateForUcl" value="true" id="suitableRB_true" <#if comment.suitableCandidateForUcl?? && comment.suitableCandidateForUcl> checked="checked"</#if> /> Yes</label>
                      <label><input type="radio" name="suitableCandidateForUcl" value="false" id="suitableRB_false" <#if comment.suitableCandidateForUcl?? && !comment.suitableCandidateForUcl> checked="checked"</#if> /> No</label>
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
                      <label><input type="radio" name="suitableCandidateForProgramme" value="true" id="suitableRB_true" <#if comment.suitableCandidateForProgramme?? && comment.suitableCandidateForProgramme> checked="checked"</#if> /> Yes</label>
                      <label><input type="radio" name="suitableCandidateForProgramme" value="false" id="suitableRB_false" <#if comment.suitableCandidateForProgramme?? && !comment.suitableCandidateForProgramme> checked="checked"</#if> /> No</label>
                      <@spring.bind "comment.suitableCandidateForProgramme" />
                      <#list spring.status.errorMessages as error> 
                      <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
                      </#list> </div>
                  </div>
                  <div class="row multi-line"> 
                  <label id="supervise-lbl" class="plain-label">Would you like to interview the applicant with a view to working with them?<em>*</em></label> 
                  <span class="hint" data-desc="<@spring.message 'review.interview'/>"></span>
                    <div class="field" id="field-wouldinterview">
                      <label><input type="radio" name="willingToInterview" value="true" id="willingRB_true" <#if comment.willingToInterviewSet && comment.willingToInterview> checked="checked"</#if> /> Yes</label>
                      <label><input type="radio" name="willingToInterview" value="false" id="willingRB_false" <#if comment.willingToInterviewSet && !comment.willingToInterview> checked="checked"</#if> /> No</label>
                      <@spring.bind "comment.willingToInterview" />
                      <#list spring.status.errorMessages as error>
                      <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
                      </#list> </div>
                  </div>
                </div>

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
               		<@spring.bind "comment.confirmNextStage" />
				    <#if spring.status.errorMessages?size &gt; 0>
			     		<div class="alert alert-error" >
				    <#else>
				        <div class="alert" >
				    </#if>
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
                  <button class="btn btn-primary" id="submitReviewFeedback" type="submit" value="Submit">Submit</button>
                </div>
                </form>
              </div>
            </section>
            <#include "/private/staff/admin/comment/timeline_application.ftl"/> </div>
          <!-- .content-box-inner --> 
        </div>
        <!-- .content-box --> 
        
      </article>
      </div>
    </div>
    <!-- Middle Ends --> 
    
    <#include "/private/common/global_footer.ftl"/> </div>
  <!-- Wrapper Ends -->
  
  </body>
  </html>
