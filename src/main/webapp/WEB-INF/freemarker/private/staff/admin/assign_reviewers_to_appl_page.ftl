<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>UCL Postgraduate Admissions</title>

<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />

<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/private/global_private.css'/>" />
<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/private/application.css'/>" />
<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/actions.css' />" />

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
        
 		<#include "/private/common/parts/application_info.ftl"/>
    		<input type="hidden" id="applicationId" value="${applicationForm.applicationNumber}"/>
          <section class="form-rows violet" id="reviewsection">
			<h2 class="no-arrow">Assign Reviewers</h2>
            <div>
              <form>
              
				<div id="add-info-bar-div" class="section-info-bar">
					Assign reviewers to the application here. You may also create new reviewers.
				</div>  
					
                <div id="assignReviewersToAppSection" class="row-group">
                    
                </div>
    
                <div class="row-group">        
                  <p><strong>Create New Reviewer</strong></p>                  
    
                  <div class="row">
                    <label class="plain-label">Reviewer First Name<em>*</em></label> 
										<span class="hint" data-desc="<@spring.message 'assignReviewer.firstName'/>"></span>
                    <div class="field">
                      <input class="full" type="text" name="newReviewerFirstName" id="newReviewerFirstName" value="${(reviewer.firstName?html)!}"/>
                      <@spring.bind "reviewer.firstName" /> 
                      <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>  
                    </div>
                  </div>
                  
                  <div class="row">
                    <label class="plain-label">Reviewer Last Name<em>*</em></label>
										<span class="hint" data-desc="<@spring.message 'assignReviewer.lastName'/>"></span>
                    <div class="field">
                      <input class="full" type="text" name="newReviewerLastName" id="newReviewerLastName" value="${(reviewer.lastName?html)!}"/>                                            
                      <@spring.bind "reviewer.lastName" /> 
                      <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
                    </div>
                  </div>
    
                  <div class="row">
                    <label class="plain-label">Email<em>*</em></label>
										<span class="hint" data-desc="<@spring.message 'assignReviewer.email'/>"></span>
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
                  	<button value="cancel" name="" id="" type="reset">Clear</button>
                    <button class="blue" type="button" id="moveToReviewBtn">Submit</button>
                  </div>
                </div>
    
                <input type="hidden" id="applicationId" name="applicationId" value="${applicationForm.applicationNumber}"/>
    
              </form>
            </div>
          </section>
    
          <form id="postReviewForm" method="post" <#if assignOnly?? && assignOnly> action ="<@spring.url '/review/assign'/>"<#else> action ="<@spring.url '/review/move'/>" </#if>></form>
          <form id="postReviewerForm" method="post" <#if assignOnly?? && assignOnly> action ="<@spring.url '/review/assignNewReviewer'/>" <#else> action ="<@spring.url '/review/createReviewer'/>" </#if>></form>
  
        </div><!-- .content-box-inner -->
      </div><!-- .content-box -->
    
    </article>
  
  </div>
  
<#include "/private/common/global_footer.ftl"/>
  
</div>

<script type="text/javascript"  src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script src="http://malsup.github.com/jquery.form.js"></script> 
<script type="text/javascript"  src="<@spring.url '/design/default/js/libraries.js'/>"></script>
<script type="text/javascript"  src="<@spring.url '/design/default/js/reviewer/review.js'/>"></script>
</body>
</html>
</section>
