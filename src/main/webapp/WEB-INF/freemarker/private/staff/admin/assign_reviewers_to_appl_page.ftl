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
    
          <section class="form-rows violet">
						<h2 class="no-arrow">Assign Reviewers</h2>
            <div>
              <form>
              
								<div id="add-info-bar-div" class="section-info-bar">
									Assign reviewers to the application here. You may also create new reviewers.
								</div>  
					
                <div id="assignReviewersToAppSection" class="row-group">
  
                  <div class="row">
                    <span class="plain-label">Assign Reviewers<#if !user.isInRole('REVIWER')><em>*</em></#if></span>
										<span class="hint" data-desc="<@spring.message 'assignReviewer.defaultReviewers'/>"></span>
                    <div class="field">
                      <select id="programReviewers" class="list-select-from" multiple="multiple" size="${avaliableOptionsSize}">
                        <optgroup id="default" label="Default reviewers">
                          <#list programmeReviewers as reviewer>
                          <option value="${applicationForm.applicationNumber}|${encrypter.encrypt(reviewer.id)}" category="default">${reviewer.firstName?html} ${reviewer.lastName?html}</option>
                          </#list>
                        </optgroup>
                        <optgroup id="previous" label="Previous reviewers">
                          <#list previousReviewers as reviewer>
                          <option value="${applicationForm.applicationNumber}|${encrypter.encrypt(reviewer.id)}" category="previous">${reviewer.firstName?html} ${reviewer.lastName?html}</option>
                          </#list>
						<#list applicationReviewers as reviewer>
							<option value="${applicationForm.applicationNumber}|${encrypter.encrypt(reviewer.id)}" class="selected" disabled="disabled">
							${reviewer.firstName?html} ${reviewer.lastName?html}
						</option>
						</#list>
						<#list pendingReviewers as unsaved>									
														<option value="${encrypter.encrypt(unsaved.id)}" class="selected" disabled="disabled">
															${unsaved.firstName?html} ${unsaved.lastName?html}
														</option>
						</#list>
                        </optgroup>
                      </select>
                    </div>
                  </div>
      
                  <!-- Available Reviewer Buttons -->
                  <div class="row list-select-buttons">
                    <div class="field">
                      <span>
                        <button class="blue" type="button" id="addReviewerBtn"><span class="icon-down"></span> Add</button>
                        <button type="button" id="removeReviewerBtn"><span class="icon-up"></span> Remove</button>
                      </span>
                    </div>
                  </div>
    
                  <!-- Already reviewers of this application -->
                  <div class="row">
                    <div class="field">
                      <select id="applicationReviewers" class="list-select-to" multiple="multiple" <#if assignOnly?? && assignOnly> disabled="disabled"</#if> size="${selectedOptionsSize}">
                        <#list applicationReviewers as reviewer>
                        <option value="${applicationForm.applicationNumber}|${encrypter.encrypt(reviewer.id)}">
                          ${reviewer.firstName?html} ${reviewer.lastName?html}
                        </option>
                        </#list>
                        <#list pendingReviewers as unsaved>									
														<option value="${encrypter.encrypt(unsaved.id)}" class="selected" disabled="disabled">
															${unsaved.firstName?html} ${unsaved.lastName?html}
														</option>
						</#list>
                      </select>
                      <@spring.bind "reviewRound.reviewers" /> 
                      <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
                    </div>
                  </div>
                  
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
